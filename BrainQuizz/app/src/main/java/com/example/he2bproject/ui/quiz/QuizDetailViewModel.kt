package com.example.he2bproject.ui.quiz


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.data.model.QuizEntity
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Quiz Detail screen.
 * Loads the selected quiz and its questions from the local database.
 */
class QuizDetailViewModel : ViewModel() {

    private val _quiz = MutableStateFlow<QuizEntity?>(null)
    val quiz = _quiz.asStateFlow()

    private val _questions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val questions = _questions.asStateFlow()

    /**
     * Loads quiz information and its questions from Room.
     */
    fun loadQuiz(quizId: Long) {
        viewModelScope.launch {
            _quiz.value = QuizRepository.getQuizById(quizId)
            _questions.value = QuizRepository.getQuestionsForQuiz(quizId)
        }
    }
}
