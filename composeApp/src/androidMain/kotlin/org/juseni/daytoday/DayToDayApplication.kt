package org.juseni.daytoday

import android.app.Application
import org.juseni.daytoday.di.initializeKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class DayToDayApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@DayToDayApplication)
        }
    }
}