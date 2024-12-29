package org.juseni.daytoday.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.juseni.daytoday.BuildConfig
import org.juseni.daytoday.data.db.getRoomDatabase
import org.juseni.daytoday.data.db.repositories.UserRepositoryImpl
import org.juseni.daytoday.data.network.ApiService
import org.juseni.daytoday.data.network.repositories.NetworkRepositoryImpl
import org.juseni.daytoday.data.sharedpreferences.createDataStore
import org.juseni.daytoday.data.sharedpreferences.repositories.DataStoreRepositoryImpl
import org.juseni.daytoday.domain.repositories.DataStoreRepository
import org.juseni.daytoday.domain.repositories.NetworkRepository
import org.juseni.daytoday.domain.repositories.UserRepository
import org.juseni.daytoday.ui.viewmodels.HomeScreenViewModel
import org.juseni.daytoday.ui.viewmodels.LoginScreenViewModel
import org.juseni.daytoday.ui.viewmodels.ConsolidatedScreenViewModel
import org.juseni.daytoday.ui.viewmodels.NewBillScreenViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val targetModule: Module

val sharedModule = module {

    // Persistence
    single { getRoomDatabase(get()) }
    single { createDataStore(get()) }

    // Repositories
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
    single<NetworkRepository> { NetworkRepositoryImpl(get(), get()) }

    // ViewModels
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ConsolidatedScreenViewModel)
    viewModelOf(::NewBillScreenViewModel)

}

val networkModule = module {

    factoryOf(::ApiService)

    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(DefaultRequest) {
                headers {
                    append("apikey", BuildConfig.API_KEY)
                }

                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.URL_HOST
                }
            }
        }
    }
}

val appModule = module {
    single(named("apiKey")) { BuildConfig.API_KEY }
    single { BuildConfig.URL_HOST }
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(networkModule, targetModule, sharedModule, preferenceModule, appModule)
    }
}