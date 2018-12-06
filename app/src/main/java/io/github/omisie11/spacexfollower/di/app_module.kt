package io.github.omisie11.spacexfollower.di

import androidx.room.Room
import io.github.omisie11.spacexfollower.CapsulesViewModel
import io.github.omisie11.spacexfollower.data.SpaceDatabase
import io.github.omisie11.spacexfollower.data.SpaceRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    // Space database instance
    single {
        Room.databaseBuilder<SpaceDatabase>(
            androidApplication(),
            SpaceDatabase::class.java,
            "space_data.db")
            .build()
    }

    // Capsules DAO instance
    single { get<SpaceDatabase>().capsulesDao() }

    // Single instance of SpaceRepository
    single { SpaceRepository(get()) }

    // ViewModel instance of CapsulesViewModel
    // get() will resolve Repository instance
    viewModel { CapsulesViewModel(get()) }

}