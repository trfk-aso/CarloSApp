package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.data.model.CategoryTotal
import org.app.carlos.data.repository.ExpenseRepository

data class StatisticsUiState(
    val period: PeriodType = PeriodType.MONTH,
    val selectedYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val selectedMonth: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
    val byCategory: Map<String, Double> = emptyMap(),
    val monthlyTrend: Map<Int, Double> = emptyMap(),
    val averagePerMonth: Double = 0.0,
    val averageByCategory: Map<String, Double> = emptyMap(),
    val hasData: Boolean = true
)

class StatisticsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState

    init {
        viewModelScope.launch {
            loadStatistics(PeriodType.MONTH)
        }
    }

    fun changePeriod(period: PeriodType) {
        viewModelScope.launch { loadStatistics(period) }
    }

    fun changeMonth(year: Int, month: Int) {
        viewModelScope.launch { loadStatistics(PeriodType.MONTH, year, month) }
    }

    fun changeYear(year: Int) {
        viewModelScope.launch { loadStatistics(PeriodType.YEAR, year) }
    }

    private suspend fun loadStatistics(period: PeriodType, year: Int? = null, month: Int? = null) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentYear = year ?: today.year
        val currentMonth = month ?: today.monthNumber

        val byCategory: Map<String, Double>
        val monthlyTrend: Map<Int, Double>
        val avgPerMonth: Double
        val avgByCategory: Map<String, Double>
        var hasData = true

        when (period) {
            PeriodType.MONTH -> {
                val ym = "$currentYear-${currentMonth.toString().padStart(2, '0')}"
                byCategory = prepareCategories(repository.getByCategoryForMonth(ym))
                val total = repository.getTotalForMonth(ym)
                avgPerMonth = total
                avgByCategory = byCategory
                monthlyTrend = mapOf(currentMonth to total)
                hasData = total > 0
            }
            PeriodType.YEAR -> {
                byCategory = prepareCategories(repository.getByCategoryForYear(currentYear.toString()))
                monthlyTrend = (1..12).associateWith { m ->
                    val ym = "$currentYear-${m.toString().padStart(2, '0')}"
                    repository.getTotalForMonth(ym)
                }
                avgPerMonth = monthlyTrend.values.average()
                avgByCategory = byCategory
                hasData = monthlyTrend.values.sum() > 0
            }
            PeriodType.ALL -> {
                byCategory = prepareCategories(repository.getByCategoryAll())
                avgPerMonth = repository.getTotalAll()
                monthlyTrend = mapOf(0 to avgPerMonth)
                avgByCategory = byCategory
                hasData = avgPerMonth > 0
            }
        }

        _uiState.value = StatisticsUiState(
            period = period,
            selectedYear = currentYear,
            selectedMonth = currentMonth,
            byCategory = byCategory,
            monthlyTrend = monthlyTrend,
            averagePerMonth = avgPerMonth,
            averageByCategory = avgByCategory,
            hasData = hasData
        )
    }

    fun refreshData() {
        viewModelScope.launch { loadStatistics(uiState.value.period) }
    }

    private fun prepareCategories(byCategoryList: List<CategoryTotal>): Map<String, Double> {
        val topCategories = mutableMapOf<String, Double>()
        val sorted = byCategoryList.sortedByDescending { it.total }

        sorted.take(3).forEach { topCategories[it.category] = it.total }
        if (sorted.size > 3) {
            val otherTotal = sorted.drop(3).sumOf { it.total }
            topCategories["Other"] = otherTotal
        }

        val allCategories = listOf("Fuel", "Insurance", "Repair", "Other")
        allCategories.forEach { cat -> if (!topCategories.containsKey(cat)) topCategories[cat] = 0.0 }

        return topCategories
    }
}
