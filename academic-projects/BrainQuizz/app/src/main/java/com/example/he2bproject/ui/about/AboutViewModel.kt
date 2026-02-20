package com.example.he2bproject.ui.about


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.HistoryEntity
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * ViewModel for the About screen.
 *
 * It combines multiple data sources:
 * - played games history (Room)
 * - favorite quizzes count (Room)
 *
 * Then it maps them into a single AboutUiState used by the UI.
 */
class AboutViewModel : ViewModel() {

    /**
     * Aggregated state exposed to the UI.
     *
     * WhileSubscribed(5000) keeps the upstream flows active for a short time after
     * the UI stops collecting, avoiding unnecessary reloads during quick navigation.
     */
    val uiState: StateFlow<AboutUiState> =
        combine(
            QuizRepository.getAllHistory(),
            QuizRepository.getFavoriteQuizzes()
        ) { history, favorites ->
            buildState(history, favorites.size)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AboutUiState()
        )


    /**
     * Builds the AboutUiState based on the current history list and favorite count.
     *
     * Note: We assume the history list is already ordered by date DESC (most recent first).
     * This assumption depends on the DAO query (ORDER BY playedAt DESC).
     */
    private fun buildState(history: List<HistoryEntity>, favoriteCount: Int): AboutUiState {
        if (history.isEmpty()) {
            // No games played yet: show default values and keep favorite count.
            return AboutUiState(
                totalPlayed = 0,
                avgScorePercent = 0,
                bestScorePercent = 0,
                favoriteCount = favoriteCount,
                lastScoreText = "-",
                lastPlayedText = "-"
            )
        }

        val totalPlayed = history.size

        // Compute each game score as a percentage (0..100).
        val percents = history.map { percent(it.score, it.totalQuestions) }

        // Average and best performance.
        val avg = percents.average().roundToInt().coerceIn(0, 100)
        val best = percents.maxOrNull()?.coerceIn(0, 100) ?: 0

        // Most recent game (requires history sorted DESC by playedAt).
        val last = history.first()
        val lastScoreText = "${last.score}/${last.totalQuestions}"
        val lastPlayedText = formatDate(last.playedAt)

        return AboutUiState(
            totalPlayed = totalPlayed,
            avgScorePercent = avg,
            bestScorePercent = best,
            favoriteCount = favoriteCount,
            lastScoreText = lastScoreText,
            lastPlayedText = lastPlayedText
        )
    }

    /**
     * Returns the score as a percent (0..100).
     * Handles edge cases to avoid division by zero.
     */
    private fun percent(score: Int, total: Int): Int {
        if (total <= 0) return 0
        return ((score.toDouble() / total.toDouble()) * 100.0).roundToInt()
    }

    /**
     * Formats an epoch timestamp into a readable date string.
     * Uses the device locale for better user experience.
     */
    private fun formatDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }
}
