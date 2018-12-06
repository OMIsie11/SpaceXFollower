package io.github.omisie11.spacexfollower.di

import android.app.Application
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin context
        startKoin(this, listOf(appModule))
    }
}