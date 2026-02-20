package com.example.he2bproject

import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.ui.game.GameMode
import com.example.he2bproject.ui.game.QuizGameViewModel
import com.example.he2bproject.ui.random.RandomGameViewModel
import com.example.he2bproject.ui.random.TriviaCategory
import com.example.he2bproject.ui.random.TriviaDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Small unit tests for UI logic that does not hit the API.
 * - RandomGameViewModel filter state
 * - QuizGameViewModel basic scoring in RANDOM mode (no repository usage)
 *
 * Uses Robolectric to provide an Android main looper so viewModelScope/Dispatchers.Main work.
 */
@RunWith(RobolectricTestRunner::class)
class RandomGameViewModelTest {

    private lateinit var randomViewModel: RandomGameViewModel
    private lateinit var gameViewModel: QuizGameViewModel

    @Before
    fun setUp() {
        randomViewModel = RandomGameViewModel()
        gameViewModel = QuizGameViewModel()
    }

    @Test
    fun selectingFilters_updatesState() {
        assertNull(randomViewModel.selectedCategory.value)
        assertNull(randomViewModel.selectedDifficulty.value)

        randomViewModel.selectCategory(TriviaCategory.SCIENCE)
        randomViewModel.selectDifficulty(TriviaDifficulty.HARD)

        assertEquals(TriviaCategory.SCIENCE, randomViewModel.selectedCategory.value)
        assertEquals(TriviaDifficulty.HARD, randomViewModel.selectedDifficulty.value)
    }

    @Test
    fun startRandomGame_correctAnswer_incrementsScoreAndMovesForward() {
        val questions = listOf(
            QuestionEntity(
                id = 1L,
                quizId = -1L,
                questionText = "Capital of Belgium?",
                answers = listOf("Brussels", "Paris", "Berlin", "Madrid"),
                correctAnswer = "Brussels"
            ),
            QuestionEntity(
                id = 2L,
                quizId = -1L,
                questionText = "2 + 2 = ?",
                answers = listOf("4", "3", "5", "6"),
                correctAnswer = "4"
            )
        )

        gameViewModel.startRandomGame(questions)

        // Initial state
        assertEquals(GameMode.RANDOM, gameViewModel.gameMode.value)
        assertEquals(0, gameViewModel.score.value)
        assertEquals(0, gameViewModel.currentIndex.value)

        // Answer first question correctly
        gameViewModel.submitAnswer("Brussels")

        assertEquals(1, gameViewModel.score.value)
        assertEquals(1, gameViewModel.currentIndex.value)
        assertFalse(gameViewModel.isGameFinished.value)

        gameViewModel.stopGame() // cancel timer job
    }
}
