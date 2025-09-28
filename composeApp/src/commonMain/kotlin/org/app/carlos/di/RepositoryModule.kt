package org.app.carlos.di

import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.ExpenseRepositoryImpl
import org.app.carlos.data.repository.SettingsRepository
import org.app.carlos.data.repository.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
}