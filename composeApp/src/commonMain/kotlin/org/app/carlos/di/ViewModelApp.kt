package org.app.carlos.di

import androidx.lifecycle.SavedStateHandle
import com.russhwolf.settings.Settings
import org.app.carlos.viewModel.AddEditExpenseUiState
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.ExpenseDetailsViewModel
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.ListViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.SplashViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModule = module {
    single { Settings() }
    single { SplashViewModel(get(), get()) }
    single { HomeViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        AddEditExpenseViewModel(get(), savedStateHandle)
    }
    single { SearchViewModel(get(), get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        ExpenseDetailsViewModel(get(), savedStateHandle)
    }
    single { ListViewModel(get()) }
    single { FavoritesViewModel(get()) }
    single { HistoryViewModel(get(), get()) }
    single { StatisticsViewModel(get()) }
    single { SettingsViewModel(get(), get(), get(), get()) }
}