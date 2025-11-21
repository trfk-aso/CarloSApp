package org.app.carlos.screens

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("nav_home")
    object AddEditExpense : Screen("addEdit") {
        fun createRoute(expenseId: Long?) = if (expenseId != null) "addEdit/$expenseId" else "addEdit"
    }

    data object Search : Screen("search")
    data object History : Screen("history")
    data object Statistics : Screen("statistics")
    data object Favorites : Screen("favorites")
    object Details : Screen("details") {
        fun createRoute(expenseId: Long?) = "details/$expenseId"
    }
    data object List : Screen("list")
    data object About : Screen("about")
    data object Settings : Screen("settings")
    object Web : Screen("web")

}