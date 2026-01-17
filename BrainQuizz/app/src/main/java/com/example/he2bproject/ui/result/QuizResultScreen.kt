package com.example.he2bproject.ui.result


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.he2bproject.R
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppIllustration
import com.example.he2bproject.ui.component.AppPrimaryButton

/**
 * Result screen displayed at the end of a quiz session.
 * Shows the final score and a feedback message based on performance.
 */
@Composable
fun QuizResultScreen(
    quizTitle: String,
    score: Int,
    totalQuestions: Int,
    onBackToMenu: () -> Unit
) {
    val ratio = if (totalQuestions <= 0) 0.0 else score.toDouble() / totalQuestions.toDouble()

    // Feedback message based on the ratio of correct answers.
    val successMessage = when {
        score == totalQuestions -> stringResource(R.string.perfect_score_msg)
        score >= totalQuestions * 0.7 -> stringResource(R.string.great_job_msg)
        score >= totalQuestions * 0.4 -> stringResource(R.string.good_effort_msg)
        else -> stringResource(R.string.try_again_msg)
    }
    // Image based on score ratio (tu remplaceras plus tard les images)
    val resultImage = when {
        ratio == 1.0 -> R.drawable.perfect
        ratio >= 0.7 -> R.drawable.great
        ratio >= 0.4 -> R.drawable.great
        else -> R.drawable.bad
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppIllustration(
            resId = resultImage,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(0.55f)
        ) {

            Text(
                text = stringResource(R.string.quiz_finished_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = quizTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            AppCard(
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.your_score_label),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = stringResource(R.string.score_format, score, totalQuestions),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AppPrimaryButton(
                text = stringResource(R.string.back_to_menu_btn),
                onClick = onBackToMenu
            )
        }
    }

}