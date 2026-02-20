package com.example.he2bproject.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.QuizEntity
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

import kotlinx.coroutines.flow.combine

/**
 * ViewModel for the Quiz List screen.
 * Holds filter state and exposes a filtered list of quizzes.
 */
class QuizListViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites = _onlyFavorites.asStateFlow()

    val savedGame = QuizRepository.getGameSnapshotFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /**
     * Quizzes filtered according to the currently selected filters.
     * Uses flatMapLatest to react immediately when a filter changes.
     */
    val quizzes: StateFlow<List<QuizEntity>> =
        combine(
            _selectedCategory,
            _selectedDifficulty,
            _onlyFavorites
        ) { category, difficulty, onlyFav ->
            Triple(category, difficulty, onlyFav)
        }.flatMapLatest { (category, difficulty, onlyFav) ->
            QuizRepository.getFilteredQuizzes(
                category = category,
                difficulty = difficulty,
                onlyFavorites = onlyFav
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }

    fun toggleFavoritesOnly() {
        _onlyFavorites.update { !it }
    }

    fun toggleFavorite(quiz: QuizEntity) {
        viewModelScope.launch {
            QuizRepository.updateQuiz(
                quiz.copy(isFavorite = !quiz.isFavorite)
            )
        }
    }
}
