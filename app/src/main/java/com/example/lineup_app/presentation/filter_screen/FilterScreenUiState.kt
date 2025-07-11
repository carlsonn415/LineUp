package com.example.lineup_app.presentation.filter_screen

import com.example.lineup_app.data.model.Genre
import com.example.lineup_app.data.model.Segment
import com.example.lineup_app.data.model.Subgenre

data class FilterScreenUiState(
    val isLocationPreferencesMenuExpanded: Boolean = false, // set to false to reduce maps api calls
    val locationSearchQuery: String = "",
    val isSortMenuExpanded: Boolean = true,
    val isFilterMenuExpanded: Boolean = true,
    val currentSortOption: String = "",
    val segmentOptions: List<Segment> = emptyList(),
    val genreOptions: List<Genre> = emptyList(),
    val subgenreOptions: List<Subgenre> = emptyList(),
    val currentSegment: String = "",
    val currentGenres: List<String> = emptyList(),
    val currentSubgenres: List<String> = emptyList(),
    val isSegmentPreferencesExpanded: Boolean = false,
    val isGenrePreferencesExpanded: Boolean = false,
    val isSubgenrePreferencesExpanded: Boolean = false,
    val preferencesUpdated: Boolean = false
)
