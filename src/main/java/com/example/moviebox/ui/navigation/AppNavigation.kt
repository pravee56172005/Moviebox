package com.example.moviebox.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviebox.data.local.MovieDatabase
import com.example.moviebox.data.MovieRepository
import com.example.moviebox.ui.screens.* // Import all screens
import com.example.moviebox.ui.viewmodel.* // Import all viewmodels

// Define navigation routes
object AppDestinations {
    const val MAIN_SCREEN = "main"
    const val ADD_MOVIES_SCREEN = "add_movies" // Although this is just an action now
    const val SEARCH_MOVIE_SCREEN = "search_movie"
    const val SEARCH_ACTOR_SCREEN = "search_actor"
    const val SEARCH_WEB_SERVICE_SCREEN = "search_web_service"
}

@Composable
fun AppNavigation(navController: NavHostController, applicationContext: Context) {
    // Initialize Database and Repository (Consider Dependency Injection for larger apps)
    val movieDao = MovieDatabase.getDatabase(applicationContext).movieDao()
    val repository = MovieRepository(movieDao = movieDao, context = applicationContext)

    // Initialize ViewModels with factory if needed, or use hiltViewModel
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
    val searchMovieViewModel: SearchMovieViewModel = viewModel(factory = SearchMovieViewModelFactory(repository))
    val searchActorViewModel: SearchActorViewModel = viewModel(factory = SearchActorViewModelFactory(repository))
    val searchWebServiceViewModel: SearchWebServiceViewModel = viewModel(factory = SearchWebServiceViewModelFactory(repository))

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        composable(AppDestinations.MAIN_SCREEN) {
            MainScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
        composable(AppDestinations.SEARCH_MOVIE_SCREEN) {
            SearchMovieScreen(
                navController = navController,
                viewModel = searchMovieViewModel
            )
        }
        composable(AppDestinations.SEARCH_ACTOR_SCREEN) {
             SearchActorScreen(
                navController = navController,
                viewModel = searchActorViewModel
            )
        }
        composable(AppDestinations.SEARCH_WEB_SERVICE_SCREEN) {
            SearchWebServiceScreen(
                navController = navController,
                viewModel = searchWebServiceViewModel
            )
        }
        // Note: Add Movies is an action within MainScreen, not a separate destination
    }
}
