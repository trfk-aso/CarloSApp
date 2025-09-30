package org.app.carlos.viewModel

import androidx.compose.material3.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.data.model.Expense
import org.app.carlos.data.repository.ExpenseRepository

data class AddEditExpenseUiState(
    val id: Long? = null,
    val category: String = "Fuel",
    val amount: Double = 0.0,
    val date: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val title: String? = null,
    val notes: String? = null,
    val isFavoriteTemplate: Boolean = false
)

class AddEditExpenseViewModel(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditExpenseUiState())
    val uiState: StateFlow<AddEditExpenseUiState> = _uiState

    init {
        val expenseId: Long? = savedStateHandle.get<Long>("expenseId")?.takeIf { it != -1L }
        val fromTemplateId: Long? = savedStateHandle.get<Long>("fromTemplate")?.takeIf { it != -1L }

        if (fromTemplateId != null) {
            loadTemplate(fromTemplateId)
        } else {
            loadExpense(expenseId)
        }
    }

    fun loadTemplate(templateId: Long) {
        viewModelScope.launch {
            val template = repository.getById(templateId)
            template?.let { t ->
                _uiState.value = _uiState.value.copy(
                    id = null,
                    category = t.category,
                    amount = 0.0,
                    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    title = t.title,
                    notes = t.notes,
                    isFavoriteTemplate = false
                )
            }
        }
    }


    fun loadExpense(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            val expense = repository.getById(id)
            expense?.let { e ->
                _uiState.value = _uiState.value.copy(
                    id = e.id,
                    category = e.category,
                    amount = e.amount,
                    date = LocalDate.parse(e.date),
                    title = e.title,
                    notes = e.notes,
                    isFavoriteTemplate = e.isFavoriteTemplate
                )
            }
        }
    }

    fun updateCategory(cat: String) { _uiState.value = _uiState.value.copy(category = cat) }
    fun updateAmount(value: String) {
        _uiState.value = _uiState.value.copy(amount = value.toDoubleOrNull() ?: 0.0)
    }
    fun updateTitle(value: String) { _uiState.value = _uiState.value.copy(title = value) }
    fun updateNotes(value: String) { _uiState.value = _uiState.value.copy(notes = value) }
    fun updateTemplate(value: Boolean) { _uiState.value = _uiState.value.copy(isFavoriteTemplate = value) }

    fun saveExpense() {
        viewModelScope.launch {
            val state = _uiState.value
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val expense = Expense(
                id = state.id,
                category = state.category,
                amount = state.amount,
                date = state.date.toString(),
                title = state.title,
                notes = state.notes,
                isFavoriteTemplate = state.isFavoriteTemplate,
                planned = state.date > today
            )

            if (expense.id == null) repository.insert(expense)
            else repository.update(expense)
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }
}
