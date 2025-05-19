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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moviebox.data.remote.OmdbSearchResultItem
import com.example.moviebox.ui.viewmodel.SearchWebServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWebServiceScreen(
    navController: NavController, // For potential navigation
    viewModel: SearchWebServiceViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Search Movie Titles (OMDB Web Service)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Search Row (TextField and Button)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text("Enter Search Query") },
                modifier = Modifier.weight(1f), // Take available space
                singleLine = true,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.searchTitles() },
                enabled = !isLoading && searchQuery.isNotBlank()
            ) {
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator and Status Message
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Results List
        if (searchResults.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults) { resultItem ->
                    WebServiceResultItem(item = resultItem)
                    Divider()
                }
            }
        } else if (!isLoading && searchQuery.isNotBlank() && statusMessage.startsWith("No movies")) {
            // Show message only if a search was attempted and yielded no results
             Text("No results found.", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

// Composable to display a single search result item from the web service
@Composable
fun WebServiceResultItem(item: OmdbSearchResultItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(item.Title ?: "No Title", style = MaterialTheme.typography.titleMedium)
        Text("Year: ${item.Year ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
        Text("IMDb ID: ${item.imdbID}", style = MaterialTheme.typography.bodySmall)
        // We don't display the poster here, but it's available in item.Poster
    }
}
