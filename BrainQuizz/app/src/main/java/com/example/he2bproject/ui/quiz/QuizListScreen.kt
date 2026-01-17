package com.example.he2bproject.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.he2bproject.data.model.QuizEntity
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.DropdownSelector

/**
 * Displays the list of local quizzes with filters (category, difficulty, favorites).
 * The screen is UI-only: filtering state is stored inside the ViewModel.
 */
@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel = viewModel(),
    onQuizClick: (QuizEntity) -> Unit
) {
    val quizzes by viewModel.quizzes.collectAsState()
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val savedGame by viewModel.savedGame.collectAsState()
    val categories = remember(quizzes) { quizzes.map { it.category }.distinct() }
    val difficulties = remember(quizzes) { quizzes.map { it.difficulty }.distinct() }
    var showOverwriteDialog by remember { mutableStateOf<QuizEntity?>(null) }

    // If the user already has a saved game for a different quiz, ask for confirmation before overwriting it.
    showOverwriteDialog?.let { targetQuiz ->
        AlertDialog(
            onDismissRequest = { showOverwriteDialog = null },
            title = { Text(stringResource(com.example.he2bproject.R.string.overwrite_save_title)) },
            text = { Text(stringResource(com.example.he2bproject.R.string.overwrite_save_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOverwriteDialog = null
                        onQuizClick(targetQuiz)
                    }
                ) {
                    Text(stringResource(com.example.he2bproject.R.string.overwrite_and_start))
                }
            },
            dismissButton = {
                TextButton(onClick = { showOverwriteDialog = null }) {
                    Text(stringResource(com.example.he2bproject.R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AppHeader(
            title = stringResource(com.example.he2bproject.R.string.quizzes_title),
            subtitle = stringResource(com.example.he2bproject.R.string.quizzes_subtitle)
        )

        AppCard {
            FiltersSection(
                // Derived UI lists. Remember them to avoid recomputing on every recomposition.
                categories = categories ,
                difficulties = difficulties,
                selectedCategory = selectedCategory,
                selectedDifficulty = selectedDifficulty,
                onlyFavorites = onlyFavorites,
                onCategorySelected = viewModel::setCategory,
                onDifficultySelected = viewModel::setDifficulty,
                onToggleFavoritesOnly = viewModel::toggleFavoritesOnly
            )
        }

        if (quizzes.isEmpty()) {
            Text(
                text = stringResource(com.example.he2bproject.R.string.no_quizzes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quizzes, key = { it.id }) { quiz ->
                    QuizCard(
                        quiz = quiz,
                        onClick = {
                            /**
                             * Unexpected but intentional behavior:
                             * if a saved game exists for a different quiz, starting this quiz would overwrite it.
                             * We show a confirmation dialog to avoid losing user progress by mistake.
                             */
                            if (savedGame?.let { it.quizId != quiz.id } == true) {
                                showOverwriteDialog = quiz
                            } else {
                                onQuizClick(quiz)
                            }
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(quiz) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    categories: List<String>,
    difficulties: List<String>,
    selectedCategory: String?,
    selectedDifficulty: String?,
    onlyFavorites: Boolean,
    onCategorySelected: (String?) -> Unit,
    onDifficultySelected: (String?) -> Unit,
    onToggleFavoritesOnly: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        DropdownSelector(
            label = stringResource(com.example.he2bproject.R.string.filter_category),
            options = categories,
            selected = selectedCategory,
            onSelect = onCategorySelected,
            optionLabel = { it }
        )

        DropdownSelector(
            label = stringResource(com.example.he2bproject.R.string.filter_difficulty),
            options = difficulties,
            selected = selectedDifficulty,
            onSelect = onDifficultySelected,
            optionLabel = {
                when (it) {
                    "easy" -> stringResource(com.example.he2bproject.R.string.difficulty_easy)
                    "medium" -> stringResource(com.example.he2bproject.R.string.difficulty_medium)
                    "hard" -> stringResource(com.example.he2bproject.R.string.difficulty_hard)
                    else -> it
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(com.example.he2bproject.R.string.filter_favorites),
                style = MaterialTheme.typography.bodyMedium
            )

            Switch(
                checked = onlyFavorites,
                onCheckedChange = { onToggleFavoritesOnly() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
private fun QuizCard(
    quiz: QuizEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${quiz.category} • ${quiz.difficulty}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (quiz.isFavorite)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(com.example.he2bproject.R.string.favorite_icon)
                )
            }
        }
    }
}
