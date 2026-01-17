package com.example.he2bproject.ui.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.he2bproject.R
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.AppOutlinedButton
import com.example.he2bproject.ui.component.AppPrimaryButton

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Home screen of the application.
 * Allows the user to choose between local and random quiz modes.
 */
@Composable
fun HomeScreen(
    onLocalClick: () -> Unit,
    onRandomClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // Observe saved game snapshot from the ViewModel
    val savedGameSnapshot by viewModel.savedGame.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Scroll state does not need to be saved across configuration changes
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ){


        AppHeader(
            title = stringResource(R.string.app_name),
            subtitle = stringResource(R.string.app_subtitle_home)
        )

        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Fit
        )
        AppCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.choose_play_mode_title),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = stringResource(R.string.choose_play_mode_desc),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Display a warning if a saved game exists
            savedGameSnapshot?.let { snapshot ->
                AppCard {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.saved_game_alert, snapshot.quizId),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            AppPrimaryButton(
                text = stringResource(R.string.local_quizzes_btn),
                onClick = onLocalClick,
                modifier = Modifier.heightIn(min = 52.dp)
            )

            AppOutlinedButton(
                text = stringResource(R.string.random_quiz_btn),
                onClick = onRandomClick,
                modifier = Modifier.heightIn(min = 52.dp)
            )
        }
    }
}

