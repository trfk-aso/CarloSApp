package org.app.carlos.di

import org.app.carlos.data.DatabaseDriverFactory
import org.app.carlos.data.IOSDatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
}