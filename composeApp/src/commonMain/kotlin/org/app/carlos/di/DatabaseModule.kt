package org.app.carlos.di

import org.app.carlos.data.CarloSApp
import org.app.carlos.data.DatabaseDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    single { CarloSApp(get<DatabaseDriverFactory>().createDriver()) }
}