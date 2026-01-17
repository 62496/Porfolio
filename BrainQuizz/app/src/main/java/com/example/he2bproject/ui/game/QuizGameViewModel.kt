package com.example.he2bproject.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.HistoryEntity
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.data.model.QuizEntity
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing a quiz game session.
 *
 * Responsibilities:
 * - Hold the current game state (questions, index, score, remaining time)
 * - Handle answer validation
 * - Run the countdown timer in viewModelScope
 * - Persist / clear a snapshot for LOCAL mode (resume feature)
 * - Save game results in history for LOCAL mode
 */
class QuizGameViewModel : ViewModel() {

    companion object {
        private const val TOTAL_TIME_SECONDS = 60
    }

    private val _questions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _timeLeft = MutableStateFlow(TOTAL_TIME_SECONDS)
    val timeLeft = _timeLeft.asStateFlow()

    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished = _isGameFinished.asStateFlow()

    private val _quiz = MutableStateFlow<QuizEntity?>(null)
    val quiz: StateFlow<QuizEntity?> = _quiz.asStateFlow()

    private var timerJob: Job? = null

    private val _gameMode = MutableStateFlow(GameMode.LOCAL)
    val gameMode = _gameMode.asStateFlow()

    /**
     * Pauses the current game session and saves progress for later resume.
     *
     * Important:
     * - Only LOCAL games can be resumed, so snapshot is saved only in LOCAL mode.
     * - RANDOM games do not create a snapshot.
     */
    fun pauseAndSaveGame() {
        timerJob?.cancel()
        // Only save if it is LOCAL mode currently
        if (_gameMode.value == GameMode.LOCAL) {
            _quiz.value?.id?.let { quizId ->
                viewModelScope.launch {
                    QuizRepository.saveGameSnapshot(
                        quizId = quizId,
                        currentIndex = _currentIndex.value,
                        score = _score.value,
                        timeLeft = _timeLeft.value
                    )
                }
            }
        }
    }

    /**
     * Quits the game and clears any saved snapshot.
     *
     * Note:
     * This is used for both LOCAL and RANDOM mode when the user decides to quit.
     */
    fun abandonGame() {
        stopGame()
        viewModelScope.launch {
            QuizRepository.clearGameSnapshot()
        }
    }

    private fun resetState() {
        _currentIndex.value = 0
        _score.value = 0
        _isGameFinished.value = false
        _timeLeft.value = TOTAL_TIME_SECONDS
        timerJob?.cancel()
    }

    /**
     * Starts a LOCAL game. If a saved snapshot exists for the same quiz, it resumes from it.
     */
    fun startLocalGame(quizId: Long) {
        _gameMode.value = GameMode.LOCAL
        viewModelScope.launch {
            val snapshot = QuizRepository.getGameSnapshot()
            if (snapshot != null && snapshot.quizId == quizId) {
                _quiz.value = QuizRepository.getQuizById(quizId)
                _questions.value = QuizRepository.getQuestionsForQuiz(quizId)

                _currentIndex.value = snapshot.currentIndex
                _score.value = snapshot.score
                _timeLeft.value = snapshot.timeLeft

                startTimer()
            } else {
                resetState()
                _quiz.value = QuizRepository.getQuizById(quizId)
                _questions.value = QuizRepository.getQuestionsForQuiz(quizId)
                startTimer()
            }
        }
    }

    /**
     * Starts a RANDOM game using questions fetched from the API.
     * Random games are not persisted (no snapshot).
     */
    fun startRandomGame(questions: List<QuestionEntity>) {
        _gameMode.value = GameMode.RANDOM
        resetState()
        _quiz.value = null // No local quiz in random mode
        _questions.value = questions
        startTimer()
    }

    /**
     * Handles the user's answer, updates score and advances to the next question.
     */
    fun submitAnswer(selectedAnswer: String) {
        val currentQuestion = _questions.value.getOrNull(_currentIndex.value)
        if (currentQuestion != null && selectedAnswer == currentQuestion.correctAnswer) {
            _score.value += 1
        }
        if (_currentIndex.value < _questions.value.lastIndex) {
            _currentIndex.value += 1
        } else {
            finishGame()
        }
    }

    // Timer runs in viewModelScope to be automatically cancelled when ViewModel is cleared.
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1_000)
                _timeLeft.value -= 1
            }
            finishGame()
        }
    }

    private fun finishGame() {
        timerJob?.cancel()
        _isGameFinished.value = true

        viewModelScope.launch {
            QuizRepository.clearGameSnapshot()
        }
        if (_gameMode.value == GameMode.LOCAL) {
            _quiz.value?.let { q ->
                saveResult(q.title, q.category, q.difficulty)
            }
        }
    }

    fun stopGame() {
        timerJob?.cancel()
    }

    /**
     * Inserts a history entry for the last played LOCAL game.
     *
     * Unexpected behavior note:
     * If called in RANDOM mode, the function returns immediately.
     */
    fun saveResult(
        quizTitle: String,
        category: String,
        difficulty: String,
    ) {
        if(gameMode.value == GameMode.RANDOM){
            return
        }
        viewModelScope.launch {
            QuizRepository.insertHistory(
                HistoryEntity(
                    quizTitle = quizTitle,
                    category = category,
                    difficulty = difficulty,
                    score = _score.value,
                    totalQuestions = questions.value.size,
                    playedAt = System.currentTimeMillis()
                )
            )
        }
    }
}