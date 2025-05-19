package com.example.moviebox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moviebox.data.local.MovieEntity
import com.example.moviebox.ui.viewmodel.SearchMovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovieScreen(
    navController: NavController, // Included for potential back navigation, though not used here
    viewModel: SearchMovieViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val movieResult by viewModel.movieResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Make column scrollable
    ) {
        Text("Search Movie from OMDB", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onQueryChange(it) },
            label = { Text("Enter Movie Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.retrieveMovie() },
                enabled = !isLoading && searchQuery.isNotBlank()
            ) {
                Text("Retrieve Movie")
            }
            Button(
                onClick = { viewModel.saveMovieToDatabase() },
                enabled = !isLoading && movieResult != null // Enable only if a movie is displayed
            ) {
                Text("Save to Database")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator and Status Message
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display Movie Details
        movieResult?.let {
            MovieDetailView(movie = it)
        }
    }
}

// Separate Composable to display movie details for clarity
@Composable
fun MovieDetailView(movie: MovieEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MovieDetailRow("Title", movie.title)
            MovieDetailRow("Year", movie.year)
            MovieDetailRow("Rated", movie.rated)
            MovieDetailRow("Released", movie.released)
            MovieDetailRow("Runtime", movie.runtime)
            MovieDetailRow("Genre", movie.genre)
            MovieDetailRow("Director", movie.director)
            MovieDetailRow("Writer", movie.writer)
            MovieDetailRow("Actors", movie.actors)
            MovieDetailRow("Plot", movie.plot)
            // Add any other fields from MovieEntity you want to display
            MovieDetailRow("IMDb ID", movie.imdbID)
            MovieDetailRow("IMDb Rating", movie.imdbRating)
            MovieDetailRow("Awards", movie.awards)
            MovieDetailRow("Country", movie.country)
            MovieDetailRow("Language", movie.language)
        }
    }
}

// Helper composable for displaying a labeled row
@Composable
fun MovieDetailRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            Text("$label: ", fontWeight = FontWeight.Bold)
            Text(value)
        }
    }
}
