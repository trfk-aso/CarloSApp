package org.app.carlos.di

import androidx.lifecycle.SavedStateHandle
import com.russhwolf.settings.Settings
import org.app.carlos.viewModel.AddEditExpenseUiState
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.ExpenseDetailsViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModule = module {
    single { Settings() }
    viewModelOf(::SplashViewModel)
    single { HomeViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        AddEditExpenseViewModel(get(), savedStateHandle)
    }
    single { SearchViewModel(get()) }
    viewModel { (savedStateHandle: SavedStateHandle) ->
        ExpenseDetailsViewModel(get(), savedStateHandle)
    }
}