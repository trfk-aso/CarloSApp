package org.app.carlos.data.repository

import org.app.carlos.data.CarloSApp
import org.app.carlos.data.model.CategoryTotal
import org.app.carlos.data.model.Expense

interface ExpenseRepository {
    suspend fun getTotalForMonth(yearMonth: String): Double
    suspend fun getByCategoryForMonth(yearMonth: String): List<CategoryTotal>
    suspend fun getRecent(limit: Int = 5): List<Expense>
    suspend fun getPlanned(limit: Int = 5): List<Expense>
    suspend fun insert(expense: Expense)
    suspend fun deleteById(id: Long)
    suspend fun getTotalForYear(year: String): Double
    suspend fun getByCategoryForYear(year: String): List<CategoryTotal>
    suspend fun getTotalAll(): Double
    suspend fun getByCategoryAll(): List<CategoryTotal>
    suspend fun update(expense: Expense)
    suspend fun getById(id: Long): Expense?
    suspend fun searchExpenses(
        query: String?,
        categories: Set<String>,
        dateFrom: String?,
        dateTo: String?,
        amountMin: Double?,
        amountMax: Double?
    ): List<Expense>

}

class ExpenseRepositoryImpl(
    private val db: CarloSApp
) : ExpenseRepository {

    override suspend fun getTotalForMonth(yearMonth: String): Double =
        db.carlosQueries.totalForMonth(yearMonth).executeAsOne()

    override suspend fun getByCategoryForMonth(yearMonth: String): List<CategoryTotal> =
        db.carlosQueries.byCategoryForMonth(yearMonth)
            .executeAsList()
            .map { row -> CategoryTotal(category = row.category, total = row.total_) }

    override suspend fun getRecent(limit: Int): List<Expense> =
        db.carlosQueries.recentExpenses(limit.toLong())
            .executeAsList()
            .map { it.toModel() }

    override suspend fun getPlanned(limit: Int): List<Expense> =
        db.carlosQueries.plannedExpenses(limit.toLong())
            .executeAsList()
            .map { it.toModel() }

    override suspend fun insert(expense: Expense) {
        db.carlosQueries.insertExpense(
            category = expense.category,
            title = expense.title,
            amount = expense.amount,
            date = expense.date,
            notes = expense.notes,
            isFavoriteTemplate = if (expense.isFavoriteTemplate) 1 else 0,
            planned = if (expense.planned) 1 else 0
        )
    }

    override suspend fun deleteById(id: Long) {
        db.carlosQueries.deleteById(id)
    }

    override suspend fun getTotalForYear(year: String): Double =
        db.carlosQueries.totalForYear(year).executeAsOne()

    override suspend fun getByCategoryForYear(year: String): List<CategoryTotal> =
        db.carlosQueries.byCategoryForYear(year)
            .executeAsList()
            .map { row -> CategoryTotal(row.category, row.total_) }

    override suspend fun getTotalAll(): Double =
        db.carlosQueries.totalAll().executeAsOne()

    override suspend fun getByCategoryAll(): List<CategoryTotal> =
        db.carlosQueries.byCategoryAll()
            .executeAsList()
            .map { row -> CategoryTotal(row.category, row.total_) }

    override suspend fun update(expense: Expense) {
        requireNotNull(expense.id) { "Expense id required for update()" }
        db.carlosQueries.updateExpense(
            id = expense.id,
            category = expense.category,
            title = expense.title,
            amount = expense.amount,
            date = expense.date,
            notes = expense.notes,
            isFavoriteTemplate = if (expense.isFavoriteTemplate) 1 else 0,
            planned = if (expense.planned) 1 else 0
        )
    }

    override suspend fun getById(id: Long): Expense? =
        db.carlosQueries.selectById(id).executeAsOneOrNull()?.toModel()

    override suspend fun searchExpenses(
        query: String?,
        categories: Set<String>,
        dateFrom: String?,
        dateTo: String?,
        amountMin: Double?,
        amountMax: Double?
    ): List<Expense> =
        db.carlosQueries.searchExpenses(
            query = query,
            categories = categories.joinToString(","),
            categoriesSize = categories.size.toLong(),
            dateFrom = dateFrom,
            dateTo = dateTo,
            amountMin = amountMin,
            amountMax = amountMax
        ).executeAsList().map { it.toModel() }
}

fun org.app.carlos.data.Expense.toModel(): Expense =
    Expense(
        id = id,
        category = category,
        title = title,
        amount = amount,
        date = date,
        notes = notes,
        isFavoriteTemplate = isFavoriteTemplate == 1L,
        planned = planned == 1L
    )

