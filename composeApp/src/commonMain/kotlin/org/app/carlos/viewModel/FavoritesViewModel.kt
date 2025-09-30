package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

class FavoritesViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Expense>>(emptyList())
    val uiState: StateFlow<List<Expense>> = _uiState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val all = repository.searchExpenses(
                query = null,
                categories = emptySet(),
                dateFrom = null,
                dateTo = null,
                amountMin = null,
                amountMax = null
            )
            _uiState.value = all.filter { it.isFavoriteTemplate }
        }
    }

    fun deleteFavorite(expense: Expense) {
        val expenseId = requireNotNull(expense.id)
        println("deleteFavorite called with id = $expenseId")

        viewModelScope.launch {
            repository.deleteFavoriteTemplate(expenseId)
            println("deleteFavoriteTemplate executed for id = $expenseId")
            loadFavorites()
        }
    }
}

