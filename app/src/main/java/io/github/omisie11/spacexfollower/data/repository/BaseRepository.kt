package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import timber.log.Timber

abstract class BaseRepository(
    private val sharedPrefs: SharedPreferences
) {

    abstract val lastRefreshDataKey: String

    /**
     * Function checks if last refresh was longer that interval that user selected
     */
    fun checkIfDataRefreshNeeded(lastRefreshKey: String): Boolean {
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(lastRefreshKey, 0)
        Timber.d("Last refresh time: $lastRefreshTime")
        // Get refresh interval set in app settings (in hours) and multiply to get value in ms
        val refreshIntervalHours =
            sharedPrefs.getString(PREFS_KEY_REFRESH_INTERVAL, "3")?.toInt() ?: 3
        val refreshIntervalMillis = refreshIntervalHours * NUMBER_OF_MILLISECONDS_IN_HOUR
        Timber.d("Refresh Interval: $refreshIntervalMillis")

        return currentTimeMillis - lastRefreshTime > refreshIntervalMillis
    }

    fun saveRefreshTime(lastRefreshDataKey: String) {
        with(sharedPrefs.edit()) {
            putLong(lastRefreshDataKey, System.currentTimeMillis())
            apply()
        }
    }

    companion object {
        const val NUMBER_OF_MILLISECONDS_IN_HOUR = 360000
    }
}