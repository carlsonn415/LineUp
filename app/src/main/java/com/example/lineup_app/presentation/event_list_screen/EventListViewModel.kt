package com.example.lineup_app.presentation.event_list_screen

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup_app.common.Constants
import com.example.lineup_app.common.Constants.PARAM_KEYWORD
import com.example.lineup_app.common.Resource
import com.example.lineup_app.data.model.Event
import com.example.lineup_app.data.remote.event_dto.DateData
import com.example.lineup_app.data.remote.event_dto.EventImage
import com.example.lineup_app.domain.model.DistanceUnit
import com.example.lineup_app.domain.repository.PreferencesRepository
import com.example.lineup_app.domain.use_case.display_event.DisplayEventUseCase
import com.example.lineup_app.domain.use_case.get_events_from_network.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val displayEventUseCase: DisplayEventUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var paginationJob: Job? = null

    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState = _uiState.asStateFlow()


    // load events from repository
    fun getEvents(
        hasLoadedOnce: Boolean,                                                             // hasLoadedOnce is used to determine if this is a new search or not
        keyWord: String? = savedStateHandle.get<String>(PARAM_KEYWORD),                             // keyword to gets passed from search bar screen
        geoPoint: String = preferencesRepository.getLocationPreferences().getLocation(),            // \
        radius: String = preferencesRepository.getFilterPreferences().getRadius(),                  // |
        startDateTime: String = preferencesRepository.getFilterPreferences().getStartDateTime(),    // |
        endDateTime: String = "", //TODO implement date filtering                                   // |
        sort: String = preferencesRepository.getFilterPreferences().getSort(),                      // filters are fetched from saved preferences
        genres: List<String>? = preferencesRepository.getFilterPreferences().getGenres(),           // |
        subgenres: List<String>? = preferencesRepository.getFilterPreferences().getSubgenres(),     // |
        segment: String? = preferencesRepository.getFilterPreferences().getSegment(),               // /
        segmentName: String = "",
        page: String = uiState.value.page.toString(),                                               // ui state holds current page number
    ) {

        if (!hasLoadedOnce) {
            paginationJob?.cancel()
            _uiState.update { currentState ->
                currentState.copy(
                    eventsResource = Resource.Loading<List<Event>>(),
                )
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                isLoadingMore = true
            )
        }

        // launch coroutine to get events from repository
        paginationJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("EventListViewModel", "Getting events - Page: $page, hasLoadedOnce: $hasLoadedOnce, isLoadingMore: ${uiState.value.isLoadingMore}")

            Log.d("EventListViewModel", "Keyword: $keyWord")
            Log.d("EventListViewModel", "GeoPoint: $geoPoint")
            Log.d("EventListViewModel", "Radius: $radius")
            Log.d("EventListViewModel", "StartDateTime: $startDateTime")
            Log.d("EventListViewModel", "EndDateTime: $endDateTime")
            Log.d("EventListViewModel", "Sort: $sort")
            Log.d("EventListViewModel", "Genres: $genres")
            Log.d("EventListViewModel", "Subgenres: $subgenres")
            Log.d("EventListViewModel", "Segment: $segment")
            Log.d("EventListViewModel", "SegmentName: $segmentName")

            // get events from repository
            getEventsUseCase(
                radius = radius,
                geoPoint = geoPoint,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                sort = sort,
                genres = genres,
                subgenres = subgenres,
                segmentId = segment,
                segmentName = segmentName,
                keyWord = keyWord,
                page = page,
                pageSize = Constants.EVENT_LIST_PAGE_SIZE
            ).collect { result ->
                ensureActive() // ensure that the coroutine is active
                when (result) {
                    is Resource.Success -> {                                                        // if success, update ui state
                        val newEvents = result.data ?: emptyList()
                        val currentEvents = _uiState.value.eventsResource.data ?: emptyList()
                        _uiState.update { currentState ->
                            val updatedEvents = if (hasLoadedOnce) {
                                currentEvents + newEvents
                            } else {
                                newEvents
                            }

                            currentState.copy(
                                eventsResource = Resource.Success(updatedEvents),
                                canLoadMore = (result.totalPages?.toInt() ?: 0) > page.toInt() + 1,
                                isLoadingMore = false
                            )
                        }
                    }

                    is Resource.Error -> {                                                          // if error, update ui state
                        if (uiState.value.eventsResource.data?.isEmpty() != false) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    eventsResource = result,
                                    isLoadingMore = false
                                )
                            }
                            Log.d("EventListViewModel", "Error: ${result.message}")
                        }
                    }

                    is Resource.Loading -> {                                                        // if loading, update ui state
                        if (uiState.value.eventsResource.data?.isEmpty() != false) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    eventsResource = result,
                                )
                            }
                            Log.d("EventListViewModel", "Loading")
                        }
                    }
                }
            }
        }
    }

    fun getDistanceFromLocation(
        latitude: Double?,
        longitude: Double?,
        unit: DistanceUnit
    ): String {
        return if (latitude != null && longitude != null && unit == DistanceUnit.Miles) {
            "${displayEventUseCase.getDistanceToEvent(latitude, longitude, unit)} mi"
        } else if (latitude != null && longitude != null && unit == DistanceUnit.Kilometers) {
            "${displayEventUseCase.getDistanceToEvent(latitude, longitude, unit)} km"
        } else {
            "No Location Provided"
        }
    }

    @SuppressLint("NewApi")
    fun getFormattedEventStartDates(dates: DateData?): String? {
        return displayEventUseCase.getFormattedEventStartDates(dates)
    }

    fun getImageUrl(
        images: List<EventImage>?,
        aspectRatio: String = "16_9",
        minImageWidth: Int = 1080
    ): String? {
        return displayEventUseCase.getImageUrl(images, aspectRatio, minImageWidth)
    }

    fun loadMoreEvents(
        radius: String = preferencesRepository.getFilterPreferences().getRadius(),
        startDateTime: String = preferencesRepository.getFilterPreferences().getStartDateTime(),
        endDateTime: String = "", //TODO implement date filtering
        sort: String = preferencesRepository.getFilterPreferences().getSort(),
        segmentName: String = "",
    ) {
        // Ensure that there is more events to load and that we are not already loading more events
        if (_uiState.value.canLoadMore && !_uiState.value.isLoadingMore) {
            // Update page number
            _uiState.update { currentState ->
                currentState.copy(
                    // Increase page number by 1
                    page = (currentState.page.toInt() + 1).toString(),
                    isLoadingMore = true
                )
            }

            // Get the events
            getEvents(
                hasLoadedOnce = true,
                radius = radius,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                sort = sort,
                segmentName = segmentName
            )
        } else {
            return
        }
    }

    fun changeEventSaved(id: String, save: Boolean) {
        val newEventsResource: Resource<List<Event>> = Resource.Success<List<Event>>(data = _uiState.value.eventsResource.data?.map { event ->
            if (event.id == id) {
                event.copy(saved = save)
            } else {
                event
            }
        } ?: emptyList())

        _uiState.update { currentState ->
            currentState.copy(
                eventsResource = newEventsResource
            )
        }
    }

    fun updateEventsSaved(eventSavedIds: Set<String>) {
        val newEventsResource: Resource<List<Event>> = Resource.Success<List<Event>>(data = _uiState.value.eventsResource.data?.map { event ->
            if (eventSavedIds.contains(event.id.toString())) {
                event.copy(saved = true)
            } else {
                event.copy(saved = false)
            }
        } ?: emptyList())

        _uiState.update { currentState ->
            currentState.copy(
                eventsResource = newEventsResource
            )
        }
    }
}