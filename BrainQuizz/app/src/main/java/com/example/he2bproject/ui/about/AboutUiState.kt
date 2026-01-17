package com.example.he2bproject.ui.about

/**
 * UI state for About screen.
 * Percent values are expected to be in the range 0..100.
 * Text fields use "-" when no data is available.
 */
data class AboutUiState(
    val totalPlayed: Int = 0,
    val avgScorePercent: Int = 0,     // 0..100
    val bestScorePercent: Int = 0,    // 0..100
    val favoriteCount: Int = 0,
    val lastScoreText: String = "-",
    val lastPlayedText: String = "-"
)
