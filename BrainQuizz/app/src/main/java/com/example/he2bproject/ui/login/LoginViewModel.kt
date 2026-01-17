package com.example.he2bproject.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.Constants
import com.example.he2bproject.network.login.AuthRequest
import com.example.he2bproject.network.login.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()


    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _errorMessage.value = null
    }

    fun validateEmail(onSuccess: () -> Unit) {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        if (!Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches() ) {
            _errorMessage.value = "Error, the email is not valid"
            _email.value = ""
            return
        } else if (_password.value =="") {
            _errorMessage.value = "Wrong password "
            _password.value = ""
            return
        }

        viewModelScope.launch {
           try {
               val response = AuthService.authClient.login(
                   Constants.AUTH_API_KEY,
                   body = AuthRequest(
                       email = currentEmail,
                       password = currentPassword
                   )
               )
               if (response.isSuccessful && response.body()?.access_token != null) {
                   onSuccess()
               } else {
                   _errorMessage.value = "Wrong login or password"
               }
           }catch (e : Exception){
               _errorMessage.value = "Network error : ${e.message}"
           }
        }
    }
}
