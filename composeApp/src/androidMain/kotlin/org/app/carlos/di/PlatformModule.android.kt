package org.app.carlos.di

import org.app.carlos.billing.AndroidBillingRepository
import org.app.carlos.data.AndroidDatabaseDriverFactory
import org.app.carlos.data.DatabaseDriverFactory
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.exporter.FileOpener
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
    single<BillingRepository> { AndroidBillingRepository(androidContext(), get()) }
    single<FileOpener> { FileOpener() }
}