package com.example.moviebox.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviebox.data.MovieRepository

import androidx.lifecycle.viewModelScope
import com.example.moviebox.data.remote.OmdbSearchResultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchWebServiceViewModel(private val repository: MovieRepository) : ViewModel() {

    // StateFlow for the search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow for the search results list
    private val _searchResults = MutableStateFlow<List<OmdbSearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<OmdbSearchResultItem>> = _searchResults.asStateFlow()

    // StateFlow for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for status/error messages
    private val _statusMessage = MutableStateFlow<String>("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // Update search query state
    fun onQueryChange(query: String) {
        _searchQuery.value = query
        // Clear results and message when query changes significantly
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _statusMessage.value = ""
        }
    }

    // Perform search using the OMDB API
    fun searchTitles() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _statusMessage.value = "Please enter a search query."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Searching..."
            _searchResults.value = emptyList() // Clear previous results
            try {
                val results = repository.searchMoviesFromApi(query)
                if (results.isNotEmpty()) {
                    _searchResults.value = results
                    _statusMessage.value = "Found ${results.size} results."
                } else {
                    _searchResults.value = emptyList()
                    _statusMessage.value = "No movies found for '$query'."
                }
            } catch (e: Exception) {
                 _searchResults.value = emptyList()
                 _statusMessage.value = "Error searching API: ${e.message}"
                 // Log the exception for debugging
                 println("API Search Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
