package org.app.carlos.di

import org.app.carlos.billing.IOSBillingRepository
import org.app.carlos.data.DatabaseDriverFactory
import org.app.carlos.data.IOSDatabaseDriverFactory
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.exporter.FileOpener
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<BillingRepository> { IOSBillingRepository(get()) }
    single<FileOpener> { FileOpener() }
}
object KoinStarter {
    fun start() = initKoin()
}