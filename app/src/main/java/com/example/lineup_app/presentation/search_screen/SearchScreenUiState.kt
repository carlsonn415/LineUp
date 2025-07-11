package com.example.lineup_app.presentation.search_screen

data class SearchScreenUiState(
    val isSearchBarExpanded: Boolean = false,
    val searchQuery: String = "",
    val isLocationPreferencesMenuExpanded: Boolean = false,
    val locationSearchQuery: String = "",
    val searchHistory: List<String> = emptyList(),
)