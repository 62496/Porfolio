package com.example.he2bproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.he2bproject.data.repository.QuizRepository
import com.example.he2bproject.ui.He2bApp
import com.example.he2bproject.ui.theme.He2bProjectTheme
import kotlinx.coroutines.launch

/**
 * Main entry point of the application.
 * Initializes the local database and sets the Compose content.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Initialize Room database (local quizzes and history)
        QuizRepository.initDatabase(this)
        // Pre-populate the database only once (local quizzes)
        lifecycleScope.launch {
            QuizRepository.populateDatabaseIfNeeded()
        }
        setContent {
            He2bProjectTheme {
                He2bApp()
            }
        }
    }
}

