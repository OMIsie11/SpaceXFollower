package io.github.omisie11.spacexfollower.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import io.github.omisie11.spacexfollower.BuildConfig
import io.github.omisie11.spacexfollower.util.CrashReportingTree
import io.github.omisie11.spacexfollower.util.PREFS_KEY_DARK_MODE
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

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
            modules(
                listOf(
                    appModule, remoteDataSourceModule, capsulesModule,
                    coresModule, companyModule, upcomingLaunchesModule,
                    launchPadsModule, aboutModule
                )
            )
        }

        // Logging in Debug build, in release log only crashes
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree()) else
            Timber.plant(CrashReportingTree())

        val sharedPrefs: SharedPreferences = get()
        AppCompatDelegate.setDefaultNightMode(
            translateValueToDayNightMode(
                sharedPrefs.getBoolean(PREFS_KEY_DARK_MODE, false)
            )
        )
    }

    private fun translateValueToDayNightMode(value: Boolean): Int = when (value) {
        true -> AppCompatDelegate.MODE_NIGHT_YES
        false -> AppCompatDelegate.MODE_NIGHT_NO
    }
}