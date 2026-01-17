package com.example.he2bproject.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.he2bproject.ui.component.AppCard
import com.example.he2bproject.ui.component.AppHeader
import com.example.he2bproject.ui.component.DropdownSelector
import com.example.he2bproject.ui.component.History

import androidx.compose.ui.res.stringResource
import com.example.he2bproject.R

/**
 * Displays the game history stored locally.
 * The user can filter results by category and by a time period.
 */
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val history by viewModel.history.collectAsState()
    // Derived list of categories for the dropdown (computed from the history list).
    val categories = remember(history) { history.map { it.category }.distinct() }
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AppHeader(
            title = stringResource(R.string.history_title),
            subtitle = stringResource(R.string.history_subtitle)
        )

        AppCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                DropdownSelector(
                    label = stringResource(R.string.category_label),
                    options = categories,
                    selected = selectedCategory,
                    onSelect = viewModel::setCategory,
                    optionLabel = { it }
                )

                DropdownSelector(
                    label = stringResource(R.string.date_label),
                    options = HistoryPeriod.entries,
                    selected = selectedPeriod,
                    onSelect = viewModel::setPeriod,
                    optionLabel = { period ->
                        when (period) {
                            HistoryPeriod.SHORT -> stringResource(R.string.last_20_sec)
                            HistoryPeriod.ALL -> stringResource(R.string.all)
                            HistoryPeriod.TODAY -> stringResource(R.string.today)
                            HistoryPeriod.LAST_7_DAYS -> stringResource(R.string.last_7_days)
                            HistoryPeriod.LAST_30_DAYS -> stringResource(R.string.last_30_days)
                        }
                    }
                )
            }
        }

        if (history.isEmpty()) {
            Text(
                text = stringResource(R.string.no_history),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(history, key = { it.id }) { item ->
                  History(item = item)
                }
            }
        }
    }
}
