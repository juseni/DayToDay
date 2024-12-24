package org.juseni.daytoday.di

import org.juseni.daytoday.data.db.getDatabaseBuilder
import org.koin.dsl.module

actual val targetModule = module {
    single { getDatabaseBuilder(context = get()) }
}