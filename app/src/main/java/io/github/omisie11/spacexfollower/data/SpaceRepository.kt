package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception


class SpaceRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val sharedPrefs: SharedPreferences
) {

    companion object {
        // Data refresh interval in milliseconds (default: 3h = 10800000 ms)
        private const val REFRESH_INTERVAL: Long = 10800000
    }

    private val repositoryJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    // Wrapper for getting all capsules from Db
    fun getCapsules(): LiveData<List<Capsule>> {
        if (checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH)) {
            refreshCapsules()
            Log.d("refreshCapsules", "Refreshing capsules")
        }
        return capsulesDao.getAllCapsules()
    }

    // Wrapper for getting all cores from Db
    fun getCores(): LiveData<List<Core>> {
        // Check if refresh is needed
        if (checkIfRefreshIsNeeded(KEY_CORES_LAST_REFRESH)) {
            refreshCores()
            Log.d("refreshCores", "Refreshing cores")
        }
        return coresDao.getAllCores()
    }

    fun deleteAllCapsules() {
        repositoryScope.launch { capsulesDao.deleteAllCapsules() }
    }

    fun deleteAllCores() {
        repositoryScope.launch { coresDao.deleteAllCores() }
    }

    // ToDo make one function for refreshing
    fun refreshCapsules() {
        Log.d("Repository", "refreshCapsules called")
        repositoryScope.launch {
            try {
                fetchCapsulesAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
            }
        }
    }

    fun refreshCores() {
        Log.d("Repository", "refreshCores called")
        repositoryScope.launch {
            try {
                fetchCoresAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
            }
        }
    }

    private suspend fun fetchCapsulesAndSaveToDb() {
        val result = spaceService.getAllCapsules().await()
        if (result.isSuccessful) {
            result.body()?.let { capsulesDao.insertCapsules(it) }
            // Save new capsules last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CAPSULES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${result.errorBody()}")
    }

    // Cores
    private suspend fun fetchCoresAndSaveToDb() {
        val result = spaceService.getAllCores().await()
        if (result.isSuccessful) {
            result.body()?.let { coresDao.insertCores(it) }
            // Save new cores last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CORES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${result.errorBody()}")
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Log.d("Repository", "Current time in millis $currentTimeMillis")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > REFRESH_INTERVAL
    }
}