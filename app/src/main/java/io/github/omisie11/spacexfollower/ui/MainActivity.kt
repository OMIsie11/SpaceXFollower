package io.github.omisie11.spacexfollower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set default values of preferences for first app launch (third argument set
        // to false ensures that this is won't set user settings to defaults with every call)
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false)

        // Get NavHostFragment and NavController
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment ?: return
        val navController = host.navController
        // Setup NavigationView menu
        findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)
        // Setup ActionBar
        setupActionBarWithNavController(navController, drawer_layout)

    }

    // Override to let NavigationUI handle back pressed
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            findNavController(R.id.nav_host_fragment), drawer_layout
        )
    }
}
