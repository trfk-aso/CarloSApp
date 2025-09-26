package org.app.carlos

import android.app.Application
import org.app.carlos.di.initKoin
import org.koin.android.ext.koin.androidContext

class CarlosApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@CarlosApplication) }
    }
}