package com.example.he2bproject.ui.random

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.network.trivia.TriviaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel used to configure and load a random quiz from the Trivia API.
 *
 * Responsibilities:
 * - Store selected filters (category / difficulty)
 * - Load questions from the API
 * - Expose loading and error state to the UI
 */
class RandomGameViewModel : ViewModel() {


    private companion object {
        // Default number of questions for a random quiz session.
        private const val DEFAULT_QUESTION_AMOUNT = 10
    }
    private val _selectedCategory = MutableStateFlow<TriviaCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<TriviaDifficulty?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    /**
     * Updates the selected category filter. Null means "Any category".
     */
    fun selectCategory(category: TriviaCategory?) {
        _selectedCategory.value = category
    }


    /**
     * Updates the selected difficulty filter. Null means "Any difficulty".
     */
    fun selectDifficulty(difficulty: TriviaDifficulty?) {
        _selectedDifficulty.value = difficulty
    }

    /**
     * Loads random questions from the API using the selected filters.
     * When successful, returns a list of QuestionEntity formatted for the game screen.
     */
    fun startRandomQuiz(onReady: (List<QuestionEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = TriviaService.triviaClient.getQuestions(
                    amount = DEFAULT_QUESTION_AMOUNT,
                    category = selectedCategory.value?.id,
                    difficulty = selectedDifficulty.value?.apiValue
                )

                when (response.responseCode) {
                    0 -> {
                        if (response.results.isEmpty()) {
                            _error.value = "No questions available."
                            return@launch
                        }

                        val questions = response.results.map { dto ->
                            val answers = (dto.incorrectAnswers + dto.correctAnswer)
                                .map { decodeHtml(it) }
                                .shuffled()

                            QuestionEntity(
                                quizId = -1L,
                                questionText = decodeHtml(dto.question),
                                correctAnswer = decodeHtml(dto.correctAnswer),
                                answers = answers
                            )
                        }

                        onReady(questions)
                    }

                    1 -> _error.value =
                        "No questions found for these filters. Try different options."

                    2 -> _error.value =
                        "Invalid request. Please change your filters."

                    3, 4 -> _error.value =
                        "Session expired. Please try again."

                    5 -> _error.value =
                        "Too many requests. Please wait a few seconds and try again."

                    else -> _error.value =
                        "Unexpected error while loading questions."
                }

            }catch (e: Exception) {
                    _error.value = "Unable to load questions. Check your internet connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Trivia API responses may contain HTML entities (e.g., &quot;).
     * This helper decodes them to display readable text.
     */
    fun decodeHtml(input: String): String =
        android.text.Html.fromHtml(input, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
}
