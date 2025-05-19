package com.example.moviebox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebox.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MovieRepository) : ViewModel() {

    // StateFlow to hold status messages for the UI
    private val _statusMessage = MutableStateFlow<String>("")
    val statusMessage: StateFlow<String> = _statusMessage

    // Function called when the 'Add Movies to DB' button is clicked
    fun addInitialMoviesToDb() {
        viewModelScope.launch {
            _statusMessage.value = "Adding movies..." // Update status
            try {
                repository.addInitialMovies()
                _statusMessage.value = "Initial movies added successfully!" // Success message
            } catch (e: Exception) {
                 _statusMessage.value = "Error adding movies: ${e.message}" // Error message
            }
        }
    }
}
