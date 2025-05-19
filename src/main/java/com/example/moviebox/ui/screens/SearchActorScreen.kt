package com.example.moviebox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.moviebox.ui.viewmodel.SearchActorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActorScreen(
    navController: NavController, // Included for potential back navigation
    viewModel: SearchActorViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Search Movies by Actor (Local DB)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onQueryChange(it) },
            label = { Text("Enter Actor Name (partial match ok)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Status Message
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error // Indicate errors clearly
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Note: No explicit Search button needed due to reactive flow in ViewModel

        // Results List
        if (searchResults.isEmpty() && searchQuery.length >= 2 && statusMessage.isEmpty()) {
             Text("No movies found matching that actor in the database.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults) { movie ->
                    MovieActorResultItem(movie = movie)
                    Divider() // Add a divider between items
                }
            }
        }
    }
}

// Composable to display a single movie result item in the list
@Composable
fun MovieActorResultItem(movie: MovieEntity) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(movie.title ?: "No Title", style = MaterialTheme.typography.titleMedium)
        Text("Year: ${movie.year ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
        Text("Actors: ${movie.actors ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
        // Optionally add more details like Plot or Director if useful
    }
}
