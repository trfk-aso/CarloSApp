package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

data class ListUiState(
    val expenses: List<Expense> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DateDesc,
    val isEmpty: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val expenseToDelete: Expense? = null,
    val title: String = "Expenses"
)

class ListViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState

    fun loadExpenses(
        query: String? = null,
        categories: Set<String> = emptySet(),
        dateFrom: String? = null,
        dateTo: String? = null,
        amountMin: Double? = null,
        amountMax: Double? = null
    ) {
        viewModelScope.launch {
            val results = repository.searchExpenses(query, categories, dateFrom, dateTo, amountMin, amountMax)
            _uiState.value = _uiState.value.copy(
                expenses = sort(results, _uiState.value.sortOrder),
                isEmpty = results.isEmpty(),
                title = if (categories.size == 1) "${categories.first()} Expenses" else "Expenses"
            )
        }
    }

    fun changeSort(order: SortOrder) {
        val sorted = sort(_uiState.value.expenses, order)
        _uiState.value = _uiState.value.copy(expenses = sorted, sortOrder = order)
    }

    private fun sort(list: List<Expense>, order: SortOrder): List<Expense> =
        when (order) {
            SortOrder.DateDesc -> list.sortedByDescending { it.date }
            SortOrder.DateAsc -> list.sortedBy { it.date }
            SortOrder.AmountDesc -> list.sortedByDescending { it.amount }
            SortOrder.AmountAsc -> list.sortedBy { it.amount }
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
            val updated = _uiState.value.expenses.filter { it.id != expense.id }
            _uiState.value = _uiState.value.copy(
                expenses = updated,
                isEmpty = updated.isEmpty(),
                showDeleteDialog = false,
                expenseToDelete = null
            )
        }
    }
}