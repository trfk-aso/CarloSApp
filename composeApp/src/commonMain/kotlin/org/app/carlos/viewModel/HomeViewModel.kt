package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.data.model.CategoryTotal
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

data class HomeUiState(
    val totalThisMonth: Double = 0.0,
    val totalLastMonth: Double = 0.0,
    val byCategory: Map<String, Double> = emptyMap(),
    val recent: List<Expense> = emptyList(),
    val planned: List<Expense> = emptyList(),
    val isEmpty: Boolean = true,
    val period: PeriodType = PeriodType.MONTH
)

enum class PeriodType {
    MONTH, YEAR, ALL
}

class HomeViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch { loadData(PeriodType.MONTH) }
    }

    fun changePeriod(period: PeriodType) {
        viewModelScope.launch { loadData(period) }
    }

    private suspend fun loadData(period: PeriodType) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val currentYearMonth = "${today.year}-${today.monthNumber.toString().padStart(2, '0')}"
        val lastMonthDate = today.minus(DatePeriod(months = 1))
        val lastMonth = "${lastMonthDate.year}-${lastMonthDate.monthNumber.toString().padStart(2, '0')}"
        val currentYear = today.year.toString()
        val lastYear = (today.year - 1).toString()

        val (totalThis, totalLast, byCategory) = when (period) {
            PeriodType.MONTH -> Triple(
                repository.getTotalForMonth(currentYearMonth),
                repository.getTotalForMonth(lastMonth),
                repository.getByCategoryForMonth(currentYearMonth)
            )
            PeriodType.YEAR -> Triple(
                repository.getTotalForYear(currentYear),
                repository.getTotalForYear(lastYear),
                repository.getByCategoryForYear(currentYear)
            )
            PeriodType.ALL -> Triple(
                repository.getTotalAll(),
                0.0,
                repository.getByCategoryAll()
            )
        }

        val recent = repository.getRecent()
        val planned = repository.getPlanned()

        _uiState.value = HomeUiState(
            totalThisMonth = totalThis,
            totalLastMonth = totalLast,
            byCategory = prepareCategories(byCategory),
            recent = recent,
            planned = planned,
            isEmpty = recent.isEmpty() && planned.isEmpty(),
            period = period
        )
    }

    fun refreshData() {
        viewModelScope.launch { loadData(uiState.value.period) }
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
        allCategories.forEach { cat ->
            if (!topCategories.containsKey(cat)) topCategories[cat] = 0.0
        }

        return topCategories
    }
}