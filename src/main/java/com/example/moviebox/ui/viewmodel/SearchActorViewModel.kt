package com.example.moviebox.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviebox.data.MovieRepository

import androidx.lifecycle.viewModelScope
import com.example.moviebox.data.local.MovieEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchActorViewModel(private val repository: MovieRepository) : ViewModel() {

    // StateFlow for the search query input by the user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow for status/error messages (optional, can add if needed)
    private val _statusMessage = MutableStateFlow<String>("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // StateFlow for the search results
    // It reacts to changes in _searchQuery
    val searchResults: StateFlow<List<MovieEntity>> = _searchQuery
        .debounce(300) // Add debounce to avoid searching on every key press
        .distinctUntilChanged() // Only search if query actually changed
        .flatMapLatest { query ->
            if (query.length < 2) { // Only search if query is reasonably long
                flowOf(emptyList()) // Return empty list if query is too short
            } else {
                repository.searchMoviesByActor(query)
                    .catch { e ->
                         // Handle errors from the flow, e.g., update status message
                        _statusMessage.value = "Error searching actors: ${e.message}"
                        emit(emptyList()) // Emit empty list on error
                    }
            }
        }
        // Convert the Flow to StateFlow, sharing the result across collectors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep flow active 5s after last subscriber
            initialValue = emptyList() // Initial value before query/search happens
        )

    // Update search query state
    fun onQueryChange(query: String) {
        _searchQuery.value = query
        _statusMessage.value = "" // Clear message on new query
    }

    // Explicit search function (optional, as search is reactive now)
    // fun search() {
    //    // Triggered by button press - might not be needed with reactive flow
    //    // Potentially update a message, but results update automatically.
    //    if (_searchQuery.value.isBlank()) {
    //        _statusMessage.value = "Please enter an actor name."
    //    }
    // }
}
