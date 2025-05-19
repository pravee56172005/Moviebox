package com.example.moviebox.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviebox.data.MovieRepository

import androidx.lifecycle.viewModelScope
import com.example.moviebox.data.local.MovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchMovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // StateFlow for the search query input by the user
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow for the retrieved movie details
    private val _movieResult = MutableStateFlow<MovieEntity?>(null)
    val movieResult: StateFlow<MovieEntity?> = _movieResult.asStateFlow()

    // StateFlow for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for status/error messages
    private val _statusMessage = MutableStateFlow<String>("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // Update search query state
    fun onQueryChange(query: String) {
        _searchQuery.value = query
        // Optionally clear previous results/message when query changes
        if (query.isEmpty()) {
             _movieResult.value = null
             _statusMessage.value = ""
        }
    }

    // Fetch movie details from OMDB API
    fun retrieveMovie() {
        if (_searchQuery.value.isBlank()) {
            _statusMessage.value = "Please enter a movie title."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Retrieving movie..."
            _movieResult.value = null // Clear previous result
            try {
                val result = repository.fetchMovieFromApi(_searchQuery.value)
                _movieResult.value = result
                if (result == null) {
                    _statusMessage.value = "Movie not found or API error."
                } else {
                     _statusMessage.value = "Movie retrieved successfully."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error retrieving movie: ${e.message}"
                _movieResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Save the currently displayed movie to the local database
    fun saveMovieToDatabase() {
        val movieToSave = _movieResult.value
        if (movieToSave == null) {
            _statusMessage.value = "No movie details to save."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true // Indicate saving process
            _statusMessage.value = "Saving movie..."
            try {
                repository.saveMovieToDb(movieToSave)
                _statusMessage.value = "'${movieToSave.title}' saved successfully!"
            } catch (e: Exception) {
                 _statusMessage.value = "Error saving movie: ${e.message}"
            } finally {
                 _isLoading.value = false
            }
        }
    }
}
