package org.app.carlos.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

data class ExpenseDetailsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val id: Long? = null,
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "",
    val title: String = "",
    val notes: String = "",
    val isTemplate: Boolean = false,
    val planned: Boolean = false
)

class ExpenseDetailsViewModel(
    private val expenseRepository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseDetailsUiState())
    val uiState: StateFlow<ExpenseDetailsUiState> = _uiState.asStateFlow()

    init {
        val expenseId = savedStateHandle.get<Long>("expenseId")?.takeIf { it != -1L }
        loadExpense(expenseId)
    }

    fun loadExpense(expenseId: Long?) {
        if (expenseId == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val expense = expenseRepository.getById(expenseId)
                println("Loaded expense: $expense")
                if (expense != null) {
                    _uiState.value = ExpenseDetailsUiState(
                        isLoading = false,
                        id = expense.id,
                        amount = expense.amount,
                        category = expense.category,
                        date = expense.date,
                        title = expense.title.orEmpty(),
                        notes = expense.notes.orEmpty(),
                        isTemplate = expense.isFavoriteTemplate,
                        planned = expense.planned
                    )
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Expense not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleTemplate(value: Boolean) {
        _uiState.update { it.copy(isTemplate = value) }
    }

    fun saveTemplate() {
        val state = _uiState.value
        if (state.id != null) {
            viewModelScope.launch {
                try {
                    val updated = Expense(
                        id = state.id,
                        category = state.category,
                        title = state.title,
                        amount = state.amount,
                        date = state.date,
                        notes = state.notes,
                        isFavoriteTemplate = state.isTemplate,
                        planned = state.planned
                    )
                    expenseRepository.update(updated)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to save template") }
                }
            }
        }
    }

    fun deleteExpense() {
        val id = _uiState.value.id ?: return
        viewModelScope.launch {
            try {
                expenseRepository.deleteById(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete") }
            }
        }
    }
}
