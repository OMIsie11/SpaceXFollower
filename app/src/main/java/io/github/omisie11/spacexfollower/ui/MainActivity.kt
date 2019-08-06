package io.github.omisie11.spacexfollower.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import io.github.omisie11.spacexfollower.BuildConfig
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.util.CrashReportingTree
import io.github.omisie11.spacexfollower.util.PREFS_KEY_DARK_MODE
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val sharedPrefs: SharedPreferences by inject()
    private lateinit var sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        AppCompatDelegate.setDefaultNightMode(
            translateValueToDayNightMode(
                sharedPrefs.getBoolean(PREFS_KEY_DARK_MODE, false)
            )
        )
        // Logging in Debug build, in release log only crashesf
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree()) else Timber.plant(CrashReportingTree())

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
                            sharedPrefs.getBoolean(
                                PREFS_KEY_DARK_MODE,
                                false
                            )
                        )
                    ); recreate()
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

    override fun onPause() {
        super.onPause()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    private fun translateValueToDayNightMode(value: Boolean): Int = when (value) {
        true -> AppCompatDelegate.MODE_NIGHT_YES
        false -> AppCompatDelegate.MODE_NIGHT_NO
    }

    companion object {
        private const val SHORTCUT_CAPSULES: String = "io.github.omisie11.spacexfollower.SHORTCUT_CAPSULES"
        private const val SHORTCUT_CORES: String = "io.github.omisie11.spacexfollower.SHORTCUT_CORES"
        private const val SHORTCUT_COMPANY: String = "io.github.omisie11.spacexfollower.SHORTCUT_COMPANY"
    }
}
