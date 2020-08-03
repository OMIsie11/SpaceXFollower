package io.github.omisie11.spacexfollower.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.navigation.NavigationView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.util.PREFS_KEY_DARK_MODE
import io.github.omisie11.spacexfollower.util.PREFS_KEY_NOTIFICATIONS_UPCOMING_LAUNCHES
import io.github.omisie11.spacexfollower.workers.LaunchNotificationWorker
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val sharedPrefs: SharedPreferences by inject()
    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Set default values of preferences for first app launch (third argument set
        // to false ensures that this is won't set user settings to defaults with every call)
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false)

        // Get NavHostFragment and NavController
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = host.navController
        // Setup NavigationView menu
        findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)
        // Setup ActionBar
        setupActionBarWithNavController(navController, drawer_layout)

        sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PREFS_KEY_DARK_MODE -> {
                    AppCompatDelegate.setDefaultNightMode(
                        translateValueToDayNightMode(
                            sharedPrefs.getString(
                                PREFS_KEY_DARK_MODE,
                                "0"
                            )
                        )
                    )
                }
                PREFS_KEY_NOTIFICATIONS_UPCOMING_LAUNCHES -> {
                    val areNotificationsActive = sharedPrefs
                        .getBoolean(PREFS_KEY_NOTIFICATIONS_UPCOMING_LAUNCHES, false)
                    if (areNotificationsActive) {
                        scheduleLaunchNotificationWork()
                    } else disableLaunchNotificationWork()
                }
            }
        }

        when (intent.action) {
            SHORTCUT_CAPSULES -> navController.navigate(R.id.capsules_dest)
            SHORTCUT_CORES -> navController.navigate(R.id.cores_dest)
            SHORTCUT_COMPANY -> navController.navigate(R.id.company_dest)
        }
    }

    // Override to let NavigationUI handle back pressed
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            findNavController(R.id.nav_host_fragment), drawer_layout
        )
    }

    override fun onStart() {
        super.onStart()
        sharedPrefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    override fun onStop() {
        super.onStop()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    private fun translateValueToDayNightMode(value: String?): Int = when (value) {
        "1" -> AppCompatDelegate.MODE_NIGHT_YES
        "2" -> AppCompatDelegate.MODE_NIGHT_NO
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    // Check once a day if there is launch in less than 24h, if yes, show notification
    private fun scheduleLaunchNotificationWork() {
        val launchNotificationWork =
            PeriodicWorkRequestBuilder<LaunchNotificationWorker>(1, TimeUnit.DAYS)
                .addTag(TAG_LAUNCH_NOTIFICATION_WORK)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            TAG_LAUNCH_NOTIFICATION_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            launchNotificationWork
        )
    }

    private fun disableLaunchNotificationWork() {
        WorkManager.getInstance(this).cancelUniqueWork(TAG_LAUNCH_NOTIFICATION_WORK)
    }

    companion object {
        private const val SHORTCUT_CAPSULES: String =
            "io.github.omisie11.spacexfollower.SHORTCUT_CAPSULES"
        private const val SHORTCUT_CORES: String =
            "io.github.omisie11.spacexfollower.SHORTCUT_CORES"
        private const val SHORTCUT_COMPANY: String =
            "io.github.omisie11.spacexfollower.SHORTCUT_COMPANY"
        private const val TAG_LAUNCH_NOTIFICATION_WORK = "launch_notification_work"
    }
}
