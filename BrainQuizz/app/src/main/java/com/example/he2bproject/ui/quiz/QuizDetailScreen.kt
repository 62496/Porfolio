package com.example.he2bproject.ui.quiz


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.AppPrimaryButton

/**
 * Quiz detail screen.
 *
 * Shows quiz metadata (title/category/difficulty) and a list of questions.
 * Starting the quiz is delegated via [onStartQuiz].
 */
@Composable
fun QuizDetailScreen(
    quizId: Long,
    viewModel: QuizDetailViewModel = viewModel(),
    onStartQuiz: (Long) -> Unit
) {
    val quiz by viewModel.quiz.collectAsState()
    val questions by viewModel.questions.collectAsState()

    // Loads data when the screen is first displayed or when quizId changes.
    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    quiz?.let { currentQuiz ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            AppHeader(
                title = currentQuiz.title,
                subtitle = "${currentQuiz.category} • ${currentQuiz.difficulty.replaceFirstChar { it.uppercase() }} • ${stringResource(com.example.he2bproject.R.string.questions_count_format, questions.size)}"
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = questions.take(questions.size),
                    key = { _, q -> q.id }
                ) { index, question ->

                    Text(
                        text = "${index + 1}. ${question.questionText}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppPrimaryButton(
                text = stringResource(com.example.he2bproject.R.string.start_quiz_btn),
                onClick = { onStartQuiz(quizId) },
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
        }
    }
}
