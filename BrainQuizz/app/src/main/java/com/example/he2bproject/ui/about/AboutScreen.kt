package com.example.he2bproject.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.he2bproject.R
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.AppIllustration
import com.example.he2bproject.ui.component.InfoRow

/**
 * About screen showing basic statistics computed from local data:
 * - total games played
 * - average and best score (in percent)
 * - favorite quizzes count
 * - last played result and date
 *
 * This screen does not hold local UI state; it only observes the ViewModel.
 */
@Composable
fun AboutScreen(
    vm: AboutViewModel = viewModel()
) {
    // Observe the aggregated state (computed from Room via the repository).
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AppHeader(
            title = stringResource(R.string.about_title),
            subtitle = stringResource(R.string.about_subtitle)
        )

        AppIllustration(
            resId = R.drawable.information,
            height = 140
        )
        // Statistics based on played games history.
        AppCard {
            InfoRow(
                label = stringResource(R.string.about_total_played),
                value = state.totalPlayed.toString()
            )
            Divider()
            InfoRow(
                label = stringResource(R.string.about_avg_score),
                value = "${state.avgScorePercent}%"
            )
            Divider()
            InfoRow(
                label = stringResource(R.string.about_best_score),
                value = "${state.bestScorePercent}%"
            )
        }
        // Extra information (favorites + last played game).
        AppCard {
            InfoRow(
                label = stringResource(R.string.about_favorites),
                value = state.favoriteCount.toString()
            )
            Divider()
            InfoRow(
                label = stringResource(R.string.about_last_score),
                value = state.lastScoreText
            )
            Divider()
            InfoRow(
                label = stringResource(R.string.about_last_played),
                value = state.lastPlayedText
            )
        }

    }
}
