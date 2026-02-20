package com.example.he2bproject.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Home screen.
 * Exposes information about a potential saved game.
 */
class HomeViewModel : ViewModel() {

    /**
     * Flow emitting a snapshot of a saved game if one exists.
     * This allows the Home screen to notify the user that a game can be resumed.
     */
    val savedGame: StateFlow<QuizRepository.GameSnapshot?> = QuizRepository.getGameSnapshotFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
