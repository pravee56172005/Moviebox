package com.example.moviebox.ui.screens

// Placeholder - Implement in next steps
import androidx.compose.foundation.layout.* 
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moviebox.ui.navigation.AppDestinations
import com.example.moviebox.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel) {
    // Basic layout for the main screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { 
            // Call ViewModel function to add movies
            viewModel.addInitialMoviesToDb()
         }) {
            Text("Add Movies to DB")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(AppDestinations.SEARCH_MOVIE_SCREEN) }) {
            Text("Search for Movies")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(AppDestinations.SEARCH_ACTOR_SCREEN) }) {
            Text("Search for Actors")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(AppDestinations.SEARCH_WEB_SERVICE_SCREEN) }) {
            Text("Search Titles (Web)")
        }

        // Optional: Display a message or status from ViewModel
        val message by viewModel.statusMessage.collectAsState()
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message)
        }
    }
}
