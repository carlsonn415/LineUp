package com.example.lineup_app.presentation.discover_screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup_app.common.Constants
import com.example.lineup_app.common.Resource
import com.example.lineup_app.data.model.Event
import com.example.lineup_app.data.remote.event_dto.DateData
import com.example.lineup_app.data.remote.event_dto.EventImage
import com.example.lineup_app.domain.model.DistanceUnit
import com.example.lineup_app.domain.repository.PreferencesRepository
import com.example.lineup_app.domain.use_case.display_event.DisplayEventUseCase
import com.example.lineup_app.domain.use_case.get_events_from_network.GetEventsUseCase
import com.example.lineup_app.presentation.discover_screen.components.WeekendDates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DiscoverScreenViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val displayEventUseCase: DisplayEventUseCase,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverScreenUiState())
    val uiState: StateFlow<DiscoverScreenUiState> = _uiState.asStateFlow()

    val weekend = getWeekendDates()
    val apiFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    val startDateTime: String = weekend.startDateTime.format(apiFormatter)
    val endDateTime: String = weekend.endDateTime.format(apiFormatter)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            loadAllEvents()
        }
    }

    fun loadAllEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            loadEventsThisWeekend()
            delay(500)
            loadEventsNearYou()
            delay(500)
            loadMusicEvents()
            delay(500)
            loadSportsEvents()
            delay(500)
            loadArtsEvents()
        }
    }

    fun refreshAllEvents() {
        updateIsRefreshing(true)
        viewModelScope.launch(Dispatchers.IO) {
            loadEventsThisWeekend()
            delay(500)
            updateIsRefreshing(false)
            loadEventsNearYou()
            delay(500)
            loadMusicEvents()
            delay(500)
            loadSportsEvents()
            delay(500)
            loadArtsEvents()
        }
    }

    suspend fun loadEventsThisWeekend() {
        getEvents(
            startDateTime = startDateTime,
            endDateTime = endDateTime
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            eventsThisWeekend = resource
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            eventsThisWeekend = resource
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            eventsThisWeekend = Resource.Loading()
                        )
                    }
                }
            }
        }
    }

    suspend fun loadEventsNearYou() {
        getEvents(
            sort = "distance,asc",
            radius = Constants.EVENTS_NEAR_YOU_RADIUS
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            eventsNearYou = resource
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            eventsNearYou = resource
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            eventsNearYou = Resource.Loading()
                        )
                    }
                }
            }
        }
    }

    suspend fun loadMusicEvents() {
        getEvents(
            segmentName = "Music"
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            musicEvents = resource
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            musicEvents = resource
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            musicEvents = Resource.Loading()
                        )
                    }
                }
            }
        }
    }

    suspend fun loadSportsEvents() {
        getEvents(
            segmentName = "Sports"
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            sportsEvents = resource
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            sportsEvents = resource
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            sportsEvents = Resource.Loading()
                        )
                    }
                }
            }
        }
    }

    suspend fun loadArtsEvents() {
        getEvents(
            segmentName = "Arts & Theatre"
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            artsEvents = resource
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            artsEvents = resource
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            artsEvents = Resource.Loading()
                        )
                    }
                }
            }
        }
    }

    // load events from repository
    fun getEvents(
        geoPoint: String = preferencesRepository.getLocationPreferences().getLocation(),
        radius: String = "",
        startDateTime: String = "",
        endDateTime: String = "",
        sort: String = "", // Default sort is relevance
        segmentName: String? = null,
        pageSize: String? = Constants.DISCOVER_PAGE_SIZE,
    ): Flow<Resource<List<Event>>> {
        return getEventsUseCase(
            radius = radius,
            geoPoint = geoPoint,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            sort = sort,
            segmentName = segmentName,
            pageSize = pageSize
        )
    }

    fun getWeekendDates(): WeekendDates {
        val today = LocalDate.now(ZoneId.systemDefault()) // Get today in system's default timezone
        val saturday12Am = LocalTime.MIDNIGHT
        val monday12Am = LocalTime.MIDNIGHT

        // Find the upcoming Saturday
        val upcomingSaturdayLd: LocalDate = if (today.dayOfWeek == DayOfWeek.SATURDAY && LocalTime.now().isAfter(saturday12Am.minusSeconds(1))) {
            // If today is Saturday but it's already past midnight (or exactly midnight), look for next Saturday
            today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        } else if (today.dayOfWeek.value > DayOfWeek.SATURDAY.value) {
            // If today is Sunday, look for next Saturday
            today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
        } else {
            // If today is before Saturday, or it's Saturday before/at 12 AM
            today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        }

        val weekendStartLocal = LocalDateTime.of(upcomingSaturdayLd, saturday12Am)
        val weekendEndLocal = LocalDateTime.of(upcomingSaturdayLd.plusDays(2), monday12Am)

        // Convert local start/end to ZonedDateTime in system default, then to UTC
        val systemDefaultZone = ZoneId.systemDefault()
        val weekendStartUtc = ZonedDateTime.of(weekendStartLocal, systemDefaultZone)
            .withZoneSameInstant(ZoneId.of("UTC"))
        val weekendEndUtc = ZonedDateTime.of(weekendEndLocal, systemDefaultZone)
            .withZoneSameInstant(ZoneId.of("UTC"))

        return WeekendDates(startDateTime = weekendStartUtc, endDateTime = weekendEndUtc)
    }

    fun getImageUrl(
        images: List<EventImage>?,
        aspectRatio: String = "16_9",
        minImageWidth: Int = 1080
    ): String? {
        val url = displayEventUseCase.getImageUrl(images, aspectRatio, minImageWidth)
        return url
    }

    @SuppressLint("NewApi")
    fun getFormattedEventStartDates(dates: DateData?): String {
        val formattedString = displayEventUseCase.getFormattedEventStartDates(dates) ?: "No Start Date Provided"
        return formattedString
    }

    fun getDistanceFromLocation(
        latitude: Double?,
        longitude: Double?,
        unit: DistanceUnit
    ): String {
        if (latitude != null && longitude != null && unit == DistanceUnit.Miles) {
            val dist = "${displayEventUseCase.getDistanceToEvent(latitude, longitude, unit)} mi"
            return dist
        } else if (latitude != null && longitude != null && unit == DistanceUnit.Kilometers) {
            val dist = "${displayEventUseCase.getDistanceToEvent(latitude, longitude, unit)} km"
            return dist
        } else {
            return "No Distance Provided"
        }
    }

    fun updateIsRefreshing(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(
                isRefreshing = isRefreshing
            )
        }
    }

    fun changeEventSaved(
        id: String,
        save: Boolean,
        eventList: List<Event>,
        category: Int // 0 = this weekend, 1 = near you, 2 = music, 3 = sports, 4 = arts
    ) {
        val newEventsResource: Resource<List<Event>> = Resource.Success<List<Event>>(data = eventList.map { event ->
            if (event.id == id) {
                event.copy(saved = save)
            } else {
                event
            }
        })

        _uiState.update { currentState ->
            when (category) {
                0 -> currentState.copy(eventsThisWeekend = newEventsResource)
                1 -> currentState.copy(eventsNearYou = newEventsResource)
                2 -> currentState.copy(musicEvents = newEventsResource)
                3 -> currentState.copy(sportsEvents = newEventsResource)
                4 -> currentState.copy(artsEvents = newEventsResource)
                else -> currentState
            }
        }
    }

    fun updateEventsSaved(eventSavedIds: Set<String>) {
        _uiState.update { currentState ->
            // Helper function to update a single list within the current state
            fun updateCategoryEvents(
                currentResource: Resource<List<Event>>,
                savedIds: Set<String>
            ): Resource<List<Event>> {
                return when (currentResource) {
                    is Resource.Success -> {
                        val updatedEvents = currentResource.data?.map { event ->
                            event.copy(saved = savedIds.contains(event.id)) // Use event.id directly
                        }
                        Resource.Success(updatedEvents ?: emptyList())
                    }
                    else -> currentResource
                }
            }

            currentState.copy(
                eventsThisWeekend = updateCategoryEvents(currentState.eventsThisWeekend, eventSavedIds),
                eventsNearYou = updateCategoryEvents(currentState.eventsNearYou, eventSavedIds),
                musicEvents = updateCategoryEvents(currentState.musicEvents, eventSavedIds),
                sportsEvents = updateCategoryEvents(currentState.sportsEvents, eventSavedIds),
                artsEvents = updateCategoryEvents(currentState.artsEvents, eventSavedIds)
            )
        }
    }
}