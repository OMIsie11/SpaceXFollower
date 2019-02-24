package io.github.omisie11.spacexfollower.di

import androidx.preference.PreferenceManager
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesAdapter
import io.github.omisie11.spacexfollower.ui.cores.CoresAdapter
import io.github.omisie11.spacexfollower.data.SpaceDatabase
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.SPACE_X_BASE_URL
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesViewModel
import io.github.omisie11.spacexfollower.ui.cores.CoresViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Main module for Koin DI
val appModule = module {

    // Space database instance
    single {
        Room.databaseBuilder<SpaceDatabase>(
            androidApplication(),
            SpaceDatabase::class.java,
            "space_data.db"
        )
            .build()
    }

    // SharedPrefs
    single { PreferenceManager.getDefaultSharedPreferences(get())}

    // Capsules DAO instance
    single { get<SpaceDatabase>().capsulesDao() }

    // Cores DAO instance
    single { get<SpaceDatabase>().coresDao() }

    // Company DAO instance
    single { get<SpaceDatabase>().companyDao() }

    // Single instance of SpaceRepository
    single { SpaceRepository(get(), get(), get(), get()) }

    // ViewModel instance of CapsulesViewModel
    // get() will resolve Repository instance
    viewModel { CapsulesViewModel(get()) }

    // ViewModel instance of CoresViewModel
    viewModel { CoresViewModel(get()) }

    // Adapter for capsules recyclerView
    factory { CapsulesAdapter() }

    // Adapter for cores recyclerView
    factory { CoresAdapter() }
}

// Module for networking elements
val remoteDataSourceModule = module {

    // Create Retrofit instance
    single { buildRetrofit(SPACE_X_BASE_URL) }

    // Create retrofit Service
    single { get<Retrofit>().create(SpaceService::class.java) }
}

internal fun buildRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}