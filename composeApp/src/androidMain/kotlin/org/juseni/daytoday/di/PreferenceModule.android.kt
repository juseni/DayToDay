package org.juseni.daytoday.di

import org.juseni.daytoday.data.sharedpreferences.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val preferenceModule: Module = module {
    single { createDataStore(androidContext()) }
}