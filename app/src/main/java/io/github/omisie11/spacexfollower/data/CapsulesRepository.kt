package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.*
import java.io.IOException


class CapsulesRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val sharedPrefs: SharedPreferences
) {

    // Variables for showing/hiding loading indicators
    private var areCapsulesLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val capsulesSnackBar = MutableLiveData<String>()

    init {
        areCapsulesLoading.value = false
    }

    // Wrapper for getting all capsules from Db
    fun getCapsules(): LiveData<List<Capsule>> {
        //if (checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH)) {
        //    refreshCapsules()
        //    Log.d("refreshCapsules", "Refreshing capsules")
        //}
        return capsulesDao.getAllCapsules()
    }

    suspend fun deleteAllCapsules() = withContext(Dispatchers.IO) { capsulesDao.deleteAllCapsules() }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = areCapsulesLoading

    fun getCapsulesSnackbar(): MutableLiveData<String> = capsulesSnackBar

    suspend fun refreshIfCapsulesDataOld() {
        if (checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH)) {
            refreshCapsules()
            Log.d("refreshCapsules", "Refreshing capsules")
        }
    }

    suspend fun refreshCapsules() {
        // Start loading process
        areCapsulesLoading.value = true
        Log.d("Repository", "refreshCapsules called")
        withContext(Dispatchers.IO) {
            try {
                fetchCapsulesAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
                areCapsulesLoading.postValue(false)
                when (exception) {
                    is IOException -> capsulesSnackBar.postValue("Network problem occurred")
                    else -> {
                        capsulesSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchCapsulesAndSaveToDb() {
        Log.d("Repo", "fetchCapsulesAndSaveToDb called")
        val response = spaceService.getAllCapsules().await()
        if (response.isSuccessful) {
            Log.d("Repo", "Response SUCCESSFUL")
            response.body()?.let { capsulesDao.insertCapsules(it) }
            // Save new capsules last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CAPSULES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${response.errorBody()}")
        // Capsules no longer fetching, hide loading indicator
        areCapsulesLoading.postValue(false)
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Log.d("Repository", "Current time in millis $currentTimeMillis")
        // Get refresh interval set in app settings (in hours) and multiply to get value in ms
        val refreshIntervalHours = sharedPrefs.getString(PREFS_KEY_REFRESH_INTERVAL, "3")?.toInt() ?: 3
        val refreshInterval = refreshIntervalHours * 3600000
        Log.d("Repository", "Refresh Interval from settings: $refreshInterval")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > refreshInterval
    }
}