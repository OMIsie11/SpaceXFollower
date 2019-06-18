package io.github.omisie11.spacexfollower.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@MyApplication)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(listOf(appModule, remoteDataSourceModule, capsulesModule,
                coresModule, companyModule, nextLaunchModule, upcomingLaunchesModule))
        }
    }
}