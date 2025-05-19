package com.example.moviebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.moviebox.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Optional: Enables drawing behind system bars
        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background // This might now default or error
            ) {
                // Setup Navigation
                val navController = rememberNavController()
                AppNavigation(navController = navController, applicationContext = applicationContext)
            }
        }
    }
}

// Preview for the main entry point (optional)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // In a real preview, you might want to show a specific screen
    // For now, just showing an empty surface
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {}
    // Or, you could try previewing the AppNavigation, but context might be tricky.
    // AppNavigation(navController = rememberNavController(), applicationContext = LocalContext.current)
}
