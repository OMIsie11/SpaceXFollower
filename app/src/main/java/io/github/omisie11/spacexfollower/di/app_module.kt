package io.github.omisie11.spacexfollower.di

import androidx.preference.PreferenceManager
import androidx.room.Room
import io.github.omisie11.spacexfollower.data.local.SpaceDatabase
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.ui.about.used_libraries.UsedLibrariesViewModel
import io.github.omisie11.spacexfollower.data.repository.CapsulesRepository
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesViewModel
import io.github.omisie11.spacexfollower.data.repository.CompanyRepository
import io.github.omisie11.spacexfollower.ui.company.CompanyViewModel
import io.github.omisie11.spacexfollower.data.repository.CoresRepository
import io.github.omisie11.spacexfollower.ui.cores.CoresViewModel
import io.github.omisie11.spacexfollower.data.repository.DashboardRepository
import io.github.omisie11.spacexfollower.ui.dashboard.DashboardViewModel
import io.github.omisie11.spacexfollower.data.repository.LaunchPadsRepository
import io.github.omisie11.spacexfollower.ui.launch_pads.LaunchPadsViewModel
import io.github.omisie11.spacexfollower.data.repository.LaunchesRepository
import io.github.omisie11.spacexfollower.ui.launches.LaunchesViewModel
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
        Room.databaseBuilder(
            androidApplication(),
            SpaceDatabase::class.java,
            "space_data.db"
        )
            .fallbackToDestructiveMigration()
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
    single {
        CapsulesRepository(
            get(),
            get(),
            get()
        )
    }

    // ViewModel instance of CapsulesViewModel
    viewModel { CapsulesViewModel(get()) }
}

val coresModule = module {

    // Cores DAO instance
    single { get<SpaceDatabase>().coresDao() }

    // Single instance of CoresRepository
    single {
        CoresRepository(
            get(),
            get(),
            get()
        )
    }

    // ViewModel instance of CoresViewModel
    viewModel { CoresViewModel(get()) }
}

val companyModule = module {

    // Company DAO instance
    single { get<SpaceDatabase>().companyDao() }

    // Single instance of CompanyRepository
    single {
        CompanyRepository(
            get(),
            get(),
            get()
        )
    }

    // ViewModel instance for CompanyInfo
    viewModel { CompanyViewModel(get()) }
}

val launchesModule = module {

    single { get<SpaceDatabase>().upcomingLaunchesDao() }

    single {
        LaunchesRepository(
            get(),
            get(),
            get()
        )
    }

    viewModel { LaunchesViewModel(get()) }
}

val launchPadsModule = module {

    single { get<SpaceDatabase>().launchPadsDao() }

    single {
        LaunchPadsRepository(
            get(),
            get(),
            get()
        )
    }

    viewModel { LaunchPadsViewModel(get()) }
}

val dashboardModule = module {

    single {
        DashboardRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel { DashboardViewModel(get()) }
}

val aboutModule = module {

    viewModel { UsedLibrariesViewModel() }
}