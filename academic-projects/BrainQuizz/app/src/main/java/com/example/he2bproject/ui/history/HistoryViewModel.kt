package com.example.he2bproject.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.he2bproject.data.model.HistoryEntity
import com.example.he2bproject.data.repository.QuizRepository
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

enum class HistoryPeriod {
    SHORT,
    ALL,
    TODAY,
    LAST_7_DAYS,
    LAST_30_DAYS
}

/**
 * ViewModel for the History screen.
 * Holds filter state (category + period) and exposes a filtered history list.
 */
class HistoryViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(HistoryPeriod.ALL)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    /**
     * Filtered history stream based on selected category and selected time period.
     */
    val history: StateFlow<List<HistoryEntity>> =
        combine(
            QuizRepository.getAllHistory(),
            _selectedCategory,
            _selectedPeriod
        ) { history, category, period ->
            val now = System.currentTimeMillis()

            history.filter { item ->
                val matchCategory =
                    category == null || item.category == category

                val matchDate = when (period) {
                    HistoryPeriod.SHORT ->
                        item.playedAt >= now - TimeUnit.SECONDS.toMillis(20)
                    HistoryPeriod.ALL -> true
                    HistoryPeriod.TODAY ->
                        item.playedAt >= now - TimeUnit.DAYS.toMillis(1)
                    HistoryPeriod.LAST_7_DAYS ->
                        item.playedAt >= now - TimeUnit.DAYS.toMillis(7)
                    HistoryPeriod.LAST_30_DAYS ->
                        item.playedAt >= now - TimeUnit.DAYS.toMillis(30)
                }

                matchCategory && matchDate
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Updates the selected category filter. Null means "All categories". */
    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    /** Updates the selected time filter. Null means "All". */
    fun setPeriod(period: HistoryPeriod?) {
        _selectedPeriod.value = period ?: HistoryPeriod.ALL
    }
}

