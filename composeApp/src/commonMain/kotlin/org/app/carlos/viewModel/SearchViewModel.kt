package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

data class SearchUiState(
    val results: List<Expense> = emptyList(),
    val isEmpty: Boolean = false,
    val recentSearches: List<String> = emptyList()
)

class SearchViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun search(
        query: String?,
        categories: Set<String>,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        amountMin: String,
        amountMax: String
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
            _uiState.value = _uiState.value.copy(
                results = results,
                isEmpty = results.isEmpty(),
                recentSearches = updateRecent(query)
            )
        }
    }

    private fun updateRecent(query: String?): List<String> {
        if (query.isNullOrBlank()) return _uiState.value.recentSearches
        val updated = listOf(query) + _uiState.value.recentSearches
        return updated.distinct().take(5)
    }
}
