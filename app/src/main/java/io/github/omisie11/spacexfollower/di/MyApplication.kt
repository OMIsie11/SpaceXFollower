package io.github.omisie11.spacexfollower.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
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

        // Initialize ThreeTenABP
        AndroidThreeTen.init(this)

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
                    coresModule, companyModule, launchesModule,
                    launchPadsModule, dashboardModule, aboutModule
                )
            )
        }

        // Logging in Debug build, in release log only crashes
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree()) else
            Timber.plant(CrashReportingTree())

        val sharedPrefs: SharedPreferences = get()
        AppCompatDelegate.setDefaultNightMode(
            translateValueToDayNightMode(
                sharedPrefs.getString(PREFS_KEY_DARK_MODE, "0")
            )
        )
    }

    private fun translateValueToDayNightMode(value: String?): Int = when (value) {
        "1" -> AppCompatDelegate.MODE_NIGHT_YES
        "2" -> AppCompatDelegate.MODE_NIGHT_NO
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}
