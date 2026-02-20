package com.example.he2bproject.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.AppPrimaryButton
import com.example.he2bproject.R
import com.example.he2bproject.ui.component.AppIllustration
import com.example.he2bproject.ui.component.AppTextField


import androidx.compose.ui.res.stringResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        AppIllustration(
            resId = R.drawable.login,
            height = 180
        )

        AppHeader(
            title = stringResource(R.string.welcome_title),
            subtitle = stringResource(R.string.sign_in_subtitle)
        )

        AppCard {
            AppTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = stringResource(R.string.email_label)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = stringResource(R.string.password_label),
                isPassword = true
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        AppPrimaryButton(
            text = stringResource(R.string.sign_in_button),
            onClick = { viewModel.validateEmail(onLoginSuccess) }
        )
    }
}
