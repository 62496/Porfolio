package com.example.he2bproject

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.he2bproject.ui.login.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the email validation logic inside [LoginViewModel].
 * Uses Robolectric to simulate the Android framework for Patterns.EMAIL_ADDRESS.
 */
@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    @Test
    fun valid_credentials_call_success_callback()  {

        viewModel.onEmailChange("test@he2b.be")
        viewModel.onPasswordChange("12345")


        viewModel.validateEmail { }

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun empty_password_sets_error() {
        viewModel.onEmailChange("student@he2b.be")
        viewModel.onPasswordChange("")
        var callbackInvoked = false

        viewModel.validateEmail { callbackInvoked = true }

        assertFalse(callbackInvoked)
        assertEquals("student@he2b.be", viewModel.email.value)
        assertEquals("Wrong password " ,viewModel.errorMessage.value)
    }

    @Test
    fun invalid_email_sets_error_and_clears_field() {
        viewModel.onEmailChange("invalid_email")
        viewModel.onPasswordChange("dev5!!")

        var callbackInvoked = false

        viewModel.validateEmail { callbackInvoked = true }

        assertFalse(callbackInvoked)
        assertEquals("", viewModel.email.value)
        assertEquals("Error, the email is not valid", viewModel.errorMessage.value)
    }
}
