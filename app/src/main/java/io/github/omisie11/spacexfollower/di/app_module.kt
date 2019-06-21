package io.github.omisie11.spacexfollower.di

import androidx.preference.PreferenceManager
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.github.omisie11.spacexfollower.data.*
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesAdapter
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesViewModel
import io.github.omisie11.spacexfollower.ui.company.CompanyViewModel
import io.github.omisie11.spacexfollower.ui.cores.CoresAdapter
import io.github.omisie11.spacexfollower.ui.cores.CoresViewModel
import io.github.omisie11.spacexfollower.ui.next_launch.NextLaunchViewModel
import io.github.omisie11.spacexfollower.ui.upcoming_launches.UpcomingLaunchesAdapter
import io.github.omisie11.spacexfollower.ui.upcoming_launches.UpcomingLaunchesViewModel
import io.github.omisie11.spacexfollower.util.SPACE_X_BASE_URL
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
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
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
}

// Module for networking elements
val remoteDataSourceModule = module {

    // Create Retrofit instance
    single {
        Retrofit.Builder()
            .baseUrl(SPACE_X_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    // Create retrofit Service
    single { get<Retrofit>().create(SpaceService::class.java) }
}

// Capsules module
val capsulesModule = module {

    // Capsules DAO instance
    single { get<SpaceDatabase>().capsulesDao() }

    // Single instance of CapsulesRepository
    single { CapsulesRepository(get(), get(), get()) }

    // ViewModel instance of CapsulesViewModel
    viewModel { CapsulesViewModel(get()) }

    // Adapter for capsules recyclerView
    factory { CapsulesAdapter() }

}

val coresModule = module {

    // Cores DAO instance
    single { get<SpaceDatabase>().coresDao() }

    // Single instance of CoresRepository
    single { CoresRepository(get(), get(), get()) }

    // ViewModel instance of CoresViewModel
    viewModel { CoresViewModel(get()) }

    // Adapter for cores recyclerView
    factory { CoresAdapter() }

}

val companyModule = module {

    // Company DAO instance
    single { get<SpaceDatabase>().companyDao() }

    // Single instance of CompanyRepository
    single { CompanyRepository(get(), get(), get()) }

    // ViewModel instance for CompanyInfo
    viewModel { CompanyViewModel(get()) }

}

val nextLaunchModule = module {

    // NextLaunch DAO instance
    single { get<SpaceDatabase>().nextLaunchDao() }

    // Single instance of NextLaunchRepository
    single { NextLaunchRepository(get(), get(), get()) }

    viewModel { NextLaunchViewModel(get()) }
}

val upcomingLaunchesModule = module {

    single { get<SpaceDatabase>().upcomingLaunchesDao() }

    single { UpcomingLaunchesRepository(get(), get(), get()) }

    viewModel { UpcomingLaunchesViewModel(get()) }

    factory { UpcomingLaunchesAdapter() }
}