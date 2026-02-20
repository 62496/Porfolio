package com.example.he2bproject.ui.random

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.AppPrimaryButton
import com.example.he2bproject.ui.component.DropdownSelector
import com.example.he2bproject.ui.He2bScreen
import com.example.he2bproject.ui.game.QuizGameViewModel

/**
 * Screen allowing the user to configure a random quiz (category/difficulty) before starting the game.
 *
 * Notes:
 * - This screen stores no local UI state (no remember/rememberSaveable needed).
 * - Filter state and loading/error state are managed by [RandomGameViewModel].
 * - When questions are loaded, we start the game using the shared [QuizGameViewModel]
 *   and navigate to the QuizGame destination.
 */
@Composable
fun RandomConfigScreen(
    viewModel: RandomGameViewModel = viewModel(),
    quizGameViewModel: QuizGameViewModel,
    navController: NavHostController
) {
    // Observe ViewModel state (single source of truth for this screen).
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        AppHeader(
            title = stringResource(com.example.he2bproject.R.string.random_quiz_title),
            subtitle = stringResource(com.example.he2bproject.R.string.choose_pref_subtitle)
        )

        AppCard {
            DropdownSelector(
                label = stringResource(com.example.he2bproject.R.string.filter_category),
                options = TriviaCategory.entries.toList(),
                selected = selectedCategory,
                // Null means "Any" (all categories).
                onSelect = { category ->
                    viewModel.selectCategory(category)
                },
                optionLabel = { it.label },
                allLabel = stringResource(com.example.he2bproject.R.string.any)
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownSelector(
                label = stringResource(com.example.he2bproject.R.string.filter_difficulty),
                options = TriviaDifficulty.entries.toList(),
                selected = selectedDifficulty,
                // Null means "Any" (all difficulties).
                onSelect = { difficulty ->
                   viewModel.selectDifficulty(difficulty)
                },
                optionLabel = {
                    when(it) {
                        TriviaDifficulty.EASY -> stringResource(com.example.he2bproject.R.string.difficulty_easy)
                        TriviaDifficulty.MEDIUM -> stringResource(com.example.he2bproject.R.string.difficulty_medium)
                        TriviaDifficulty.HARD -> stringResource(com.example.he2bproject.R.string.difficulty_hard)
                    }
                },
                allLabel = stringResource(com.example.he2bproject.R.string.any)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AppPrimaryButton(
            text = if (isLoading) stringResource(com.example.he2bproject.R.string.loading) else stringResource(com.example.he2bproject.R.string.start_random_quiz_btn),
            onClick = {
                if (!isLoading) {
                    /**
                     * When questions are ready:
                     * - start a RANDOM game in the shared QuizGameViewModel
                     * - navigate to the QuizGame screen
                     */
                    viewModel.startRandomQuiz { questions ->
                        quizGameViewModel.startRandomGame(questions)
                        navController.navigate(He2bScreen.QuizGame.name)
                    }
                }
            }
        )
        // If an error occurs (network issue or API response code), display it under the button.
        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
