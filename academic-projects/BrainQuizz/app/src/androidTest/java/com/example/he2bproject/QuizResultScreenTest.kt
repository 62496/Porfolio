package com.example.he2bproject

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.example.he2bproject.ui.result.QuizResultScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the result screen (pure UI).
 * Careful use a devise who used API 34 or API 35
 */
@RunWith(AndroidJUnit4::class)
class QuizResultScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun resultScreen_displaysScoreAndTitle() {
        composeRule.setContent {
            QuizResultScreen(
                quizTitle = "World Capitals",
                score = 7,
                totalQuestions = 10,
                onBackToMenu = {}
            )
        }

        composeRule.onNodeWithText("Quiz finished").assertIsDisplayed()
        composeRule.onNodeWithText("World Capitals").assertIsDisplayed()
        composeRule.onNodeWithText("Your score").assertIsDisplayed()
        composeRule.onNodeWithText("7 / 10").assertIsDisplayed()
        composeRule.onNodeWithText("Back to menu").assertIsDisplayed()
    }
}
