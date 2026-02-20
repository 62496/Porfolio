package com.example.he2bproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.navigation.compose.rememberNavController
import com.example.he2bproject.ui.game.QuizGameViewModel
import com.example.he2bproject.ui.random.RandomConfigScreen
import com.example.he2bproject.ui.random.RandomGameViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the Random configuration screen (UI only, no API call).
 * Careful use a devise who used API 34 or API 35
 */
@RunWith(AndroidJUnit4::class)
class RandomConfigScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun defaultUi_elementsAreVisible() {
        composeRule.setContent {
            RandomConfigScreen(
                viewModel = RandomGameViewModel(),
                quizGameViewModel = QuizGameViewModel(),
                navController = rememberNavController()
            )
        }

        composeRule.onNodeWithText("Random Quiz").assertIsDisplayed()
        composeRule.onNodeWithText("Choose your preferences").assertIsDisplayed()
        composeRule.onNodeWithText("Category").assertIsDisplayed()
        composeRule.onNodeWithText("Difficulty").assertIsDisplayed()
        composeRule.onNodeWithText("Start random quiz").assertIsDisplayed()
    }
}
