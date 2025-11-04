package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.SettingsRepository

data class SearchUiState(
    val results: List<Expense> = emptyList(),
    val isEmpty: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DateDesc,
    val showDeleteDialog: Boolean = false,
    val expenseToDelete: Expense? = null,
    val title: String = "Expenses"
)

enum class SortOrder(val label: String) {
    DateDesc("Date ↓ (newest)"),
    DateAsc("Date ↑ (oldest)"),
    AmountDesc("Amount ↓ (highest)"),
    AmountAsc("Amount ↑ (lowest)")
}

class SearchViewModel(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        viewModelScope.launch {
            val saved = settingsRepository.getRecentSearches()
            _uiState.value = _uiState.value.copy(recentSearches = saved)
        }
    }

    fun search(
        query: String? = null,
        categories: Set<String> = emptySet(),
        dateFrom: LocalDate? = null,
        dateTo: LocalDate? = null,
        amountMin: String = "",
        amountMax: String = ""
    ) {
        viewModelScope.launch {
            val results = repository.searchExpenses(
                query = query.takeIf { !it.isNullOrBlank() },
                categories = categories,
                dateFrom = dateFrom?.toString(),
                dateTo = dateTo?.toString(),
                amountMin = amountMin.toDoubleOrNull(),
                amountMax = amountMax.toDoubleOrNull()
            )

            if (!query.isNullOrBlank()) {
                settingsRepository.addRecentSearch(query)
                val updated = settingsRepository.getRecentSearches()
                _uiState.value = _uiState.value.copy(recentSearches = updated)
            }

            _uiState.value = _uiState.value.copy(
                results = sort(results, _uiState.value.sortOrder),
                isEmpty = results.isEmpty(),
                title = if (categories.size == 1) "${categories.first()} Expenses" else "Expenses"
            )
        }
    }

    private fun updateRecent(query: String?): List<String> {
        if (query.isNullOrBlank()) return _uiState.value.recentSearches
        val updated = listOf(query) + _uiState.value.recentSearches
        return updated.distinct().take(5)
    }

    fun changeSort(order: SortOrder) {
        val sorted = sort(_uiState.value.results, order)
        _uiState.value = _uiState.value.copy(results = sorted, sortOrder = order)
    }

    private fun sort(list: List<Expense>, order: SortOrder): List<Expense> =
        when (order) {
            SortOrder.DateDesc -> list.sortedByDescending { it.date }
            SortOrder.DateAsc -> list.sortedBy { it.date }
            SortOrder.AmountDesc -> list.sortedByDescending { it.amount }
            SortOrder.AmountAsc -> list.sortedBy { it.amount }
        }

    fun clearRecentSearches() {
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }

    fun confirmDelete(expense: Expense) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            expenseToDelete = expense
        )
    }

    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            expenseToDelete = null
        )
    }

    fun deleteConfirmed() {
        val expense = _uiState.value.expenseToDelete ?: return
        viewModelScope.launch {
            repository.deleteById(expense.id!!)
            val updated = _uiState.value.results.filter { it.id != expense.id }
            _uiState.value = _uiState.value.copy(
                results = updated,
                isEmpty = updated.isEmpty(),
                showDeleteDialog = false,
                expenseToDelete = null
            )
        }
    }
}
