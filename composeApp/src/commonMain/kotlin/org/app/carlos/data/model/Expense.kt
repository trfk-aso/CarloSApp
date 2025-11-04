package org.app.carlos.data.model

data class Expense(
    val id: Long? = null,
    val category: String,
    val title: String? = null,
    val amount: Double,
    val date: String,
    val notes: String? = null,
    val isFavoriteTemplate: Boolean = false,
    val planned: Boolean = false
)