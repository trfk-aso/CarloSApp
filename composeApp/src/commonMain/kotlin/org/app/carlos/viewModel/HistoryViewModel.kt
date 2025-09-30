package org.app.carlos.viewModel

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.exporter.FileExporter
import kotlin.collections.LinkedHashMap

data class HistoryUiState(
    val groups: Map<YearMonth, List<Expense>> = emptyMap(),
    val totals: Map<YearMonth, Double> = emptyMap(),
    val isEmpty: Boolean = true,
    val filterQuery: String? = null,
    val filterCategories: Set<String> = emptySet(),
    val filterDateFrom: LocalDate? = null,
    val filterDateTo: LocalDate? = null,
    val filterAmountMin: Double? = null,
    val filterAmountMax: Double? = null
)

class HistoryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    var onExportResult: (Boolean) -> Unit = {}

    init {
        viewModelScope.launch { loadHistory() }
    }

    suspend fun loadHistory() {
        val state = _uiState.value
        val all = repository.searchExpenses(
            query = state.filterQuery,
            categories = state.filterCategories,
            dateFrom = state.filterDateFrom?.toString(),
            dateTo = state.filterDateTo?.toString(),
            amountMin = state.filterAmountMin,
            amountMax = state.filterAmountMax
        )

        val groupsRaw: Map<YearMonth, List<Expense>> = all.groupBy { exp ->
            val localDate = LocalDate.parse(exp.date)
            YearMonth(localDate.year, localDate.monthNumber)
        }

        val sortedKeys = groupsRaw.keys.sortedWith(compareByDescending<YearMonth> { it.year }.thenByDescending { it.month })

        val sortedMap = LinkedHashMap<YearMonth, List<Expense>>()
        for (k in sortedKeys) {
            groupsRaw[k]?.let { sortedMap[k] = it }
        }

        val totals = sortedMap.mapValues { it.value.sumOf { exp -> exp.amount } }

        _uiState.value = state.copy(
            groups = sortedMap,
            totals = totals,
            isEmpty = all.isEmpty()
        )
    }

    fun applyFilters(
        query: String? = null,
        categories: Set<String> = emptySet(),
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
        amountMin: Double? = null,
        amountMax: Double? = null
    ) {
        _uiState.value = _uiState.value.copy(
            filterQuery = query,
            filterCategories = categories,
            filterDateFrom = dateFrom,
            filterDateTo = dateTo,
            filterAmountMin = amountMin,
            filterAmountMax = amountMax
        )
        viewModelScope.launch { loadHistory() }
    }

    fun refresh() {
        viewModelScope.launch { loadHistory() }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            loadHistory()
        }
    }

    fun exportHistory(fileExporter: FileExporter) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isEmpty) return@launch

            val content = buildString {
                state.groups.forEach { (ym, expenses) ->
                    appendLine("${ym}:")
                    expenses.forEach { exp ->
                        appendLine("${exp.date} | ${exp.category} | ${exp.title ?: ""} | ${exp.amount} | ${exp.notes ?: ""}")
                    }
                    appendLine("Total: ${state.totals[ym] ?: 0.0}")
                    appendLine()
                }
            }

            val success = fileExporter.exportTextFile(
                "History_${Clock.System.now().toEpochMilliseconds()}.txt",
                content
            )
            onExportResult(success)
        }
    }

}

data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    override fun compareTo(other: YearMonth): Int {
        val yComp = year.compareTo(other.year)
        return if (yComp != 0) yComp else month.compareTo(other.month)
    }

    override fun toString(): String {
        val monthNames = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val idx = (month - 1).coerceIn(0, 11)
        return "${monthNames[idx]} $year"
    }
}