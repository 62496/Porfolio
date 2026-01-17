package com.example.he2bproject

import android.os.SystemClock
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class He2bAppTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun invalidEmail_showsErrorMessage() {
        composeRule.onNodeWithTag("emailField").performTextInput("invalid_email")
        composeRule.onNodeWithTag("passwordField").performTextInput("dev5!!")

        composeRule.onNodeWithText("Se connecter").performClick()

        composeRule.onNodeWithText("Error, the email is not valid").assertIsDisplayed()

    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun validEmail_navigatesToMainScreenAndShowsLogo() {
        composeRule.onNodeWithTag("emailField").performTextInput("test@he2b.be")
        composeRule.onNodeWithTag("passwordField").performTextInput("dev5!!")

        composeRule.onNodeWithText("Se connecter").performClick()

        SystemClock.sleep(5000)

        composeRule.waitUntilExactlyOneExists(hasContentDescription("Logo HE2B/ESI"), 5_000)
        composeRule.onNodeWithContentDescription("Logo HE2B/ESI").assertIsDisplayed()
    }
}
