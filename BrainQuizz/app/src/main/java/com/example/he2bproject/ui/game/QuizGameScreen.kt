package com.example.he2bproject.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.he2bproject.ui.component.AppOutlinedButton
import com.example.he2bproject.ui.component.AppPrimaryButton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.he2bproject.R

/**
 * Quiz game screen.
 *
 * Responsibilities:
 * - Display current question and possible answers
 * - Display timer and current score
 * - Intercept back navigation to avoid accidental quit
 * - Show different quit dialogs depending on game mode (LOCAL vs RANDOM)
 *
 * Note:
 * This screen is UI-only. It reads game state from the ViewModel and delegates game actions to it.
 */
@Composable
fun QuizGameScreen(
    viewModel: QuizGameViewModel,
    onQuitGame: () -> Unit, 
    onGameFinished: (Int, Int, String) -> Unit,
) {
    // Observe state from the ViewModel (single source of truth for the game session).
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isFinished by viewModel.isGameFinished.collectAsState()
    val quiz by viewModel.quiz.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()  // Observe game mode (Local or Random)
    /**
     * Prevents double navigation to the Result screen.
     * This is a UI-only guard because recomposition can happen multiple times.
     */
    var hasNavigated by remember { mutableStateOf(false) }

    /**
     * UI-only dialog state:
     * It does not need to survive process death / configuration changes,
     * so rememberSaveable is not required here.
     */
    var showQuitDialog by remember { mutableStateOf(false) }

    // Intercept physical back button IF game is not finished
    BackHandler(enabled = !isFinished) {
        showQuitDialog = true
    }

    /**
     * Quit dialog depends on game mode:
     * - LOCAL: offer "save & quit" or "quit without saving"
     * - RANDOM: quitting simply abandons the session (no save available)
     */
    if (showQuitDialog) {
        if (gameMode == GameMode.LOCAL) {
            AlertDialog(
                onDismissRequest = { showQuitDialog = false },
                title = { Text(text = stringResource(R.string.pause_title)) },
                text = { Text(text = stringResource(R.string.local_save_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showQuitDialog = false
                            // Save progress (LOCAL only) and return to Home.
                            viewModel.pauseAndSaveGame()
                            onQuitGame()
                        }
                    ) {
                        Text(stringResource(R.string.save_quit_btn))
                    }
                },
                dismissButton = {
                    Row {
                        TextButton(
                            onClick = {
                                showQuitDialog = false
                                // Clear any saved snapshot and return to Home.
                                viewModel.abandonGame()
                                onQuitGame()
                            }
                        ) {
                            Text(stringResource(R.string.quit_no_save_btn), color = MaterialTheme.colorScheme.error)
                        }
                        TextButton(onClick = { showQuitDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showQuitDialog = false },
                title = { Text(text = stringResource(R.string.quit_game_title)) },
                text = { Text(text = stringResource(R.string.random_quit_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showQuitDialog = false
                            // RANDOM games are not persisted; quitting abandons the session.
                            viewModel.abandonGame()
                            onQuitGame()
                        }
                    ) {
                        Text(stringResource(R.string.quit_btn), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuitDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
    /**
     * Defensive UI: if questions are missing (e.g., load error),
     * show an error message and provide a safe exit to Home.
     */
    if (questions.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.load_error_title), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            AppPrimaryButton(
                text = stringResource(R.string.back_to_menu_btn),
                onClick = {
                    viewModel.stopGame()
                    onQuitGame()
                }
            )
        }
        return
    }

    /**
     * Navigate to Result screen when the game is finished.
     * This is triggered once (guarded by hasNavigated) to avoid double navigation.
     */
    LaunchedEffect(isFinished) {
        if (isFinished && !hasNavigated) {
            hasNavigated = true
            val title = quiz?.title ?: ""
            onGameFinished(score, questions.size, title)
        }
    }
    // After the navigation trigger, we can stop rendering the game UI.

    if (isFinished) return
    // Defensive access: avoid crashes if index is invalid.
    val currentQuestion = questions.getOrNull(currentIndex) ?: return


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.question_progress_format, currentIndex + 1, questions.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

             // Visible button to quit/save without using physical back button
            IconButton(onClick = { showQuitDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.time_left_format, timeLeft),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Divider()

        Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            currentQuestion.answers.forEach { answer ->
                AnswerButton(
                    text = answer,
                    onClick = { viewModel.submitAnswer(answer) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "${stringResource(R.string.your_score_label)}: ${stringResource(R.string.score_format, score, questions.size)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }

}


@Composable
private fun AnswerButton(
    text: String,
    onClick: () -> Unit
) {
    AppOutlinedButton(
        text = text,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    )
}
