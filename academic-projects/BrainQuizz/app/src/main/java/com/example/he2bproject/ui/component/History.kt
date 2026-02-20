package com.example.he2bproject.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.he2bproject.data.model.HistoryEntity

@Composable
fun History(item: HistoryEntity) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(
                text = item.quizTitle,
                style = MaterialTheme.typography.titleMedium
            )

            InfoRow(label = "Category", value = item.category)
            InfoRow(label = "Difficulty", value = item.difficulty)
            InfoRow(
                label = "Score",
                value = "${item.score} / ${item.totalQuestions}"
            )
        }
    }
}