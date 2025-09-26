package org.app.carlos.di

import org.app.carlos.data.AndroidDatabaseDriverFactory
import org.app.carlos.data.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
}