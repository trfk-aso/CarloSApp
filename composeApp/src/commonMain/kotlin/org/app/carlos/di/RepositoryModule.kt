package org.app.carlos.di

import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.ExpenseRepositoryImpl
import org.app.carlos.data.repository.RemoteConfigRepository
import org.app.carlos.data.repository.RemoteConfigRepositoryImpl
import org.app.carlos.data.repository.SettingsRepository
import org.app.carlos.data.repository.SettingsRepositoryImpl
import org.app.carlos.data.repository.ThemeRepository
import org.app.carlos.data.repository.ThemeRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<RemoteConfigRepository> { RemoteConfigRepositoryImpl(get()) }
}