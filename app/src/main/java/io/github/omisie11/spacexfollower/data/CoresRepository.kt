package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.*
import java.io.IOException

class CoresRepository(
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val sharedPrefs: SharedPreferences
) {

    private val coresJob = Job()
    private val coresScope = CoroutineScope(Dispatchers.IO + coresJob)
    private var areCoresLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val coresSnackBar = MutableLiveData<String>()

    init {
        areCoresLoading.value = false
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

    fun deleteAllCores() = GlobalScope.launch(Dispatchers.IO) { coresDao.deleteAllCores() }

    fun getCoresLoadingStatus(): LiveData<Boolean> = areCoresLoading

    fun getCoresSnackbar(): MutableLiveData<String> = coresSnackBar

    fun refreshIfCoresDataOld() {
        if (checkIfRefreshIsNeeded(KEY_CORES_LAST_REFRESH)) {
            refreshCores()
            Log.d("refreshCores", "Refreshing cores")
        }
    }

    fun refreshCores() {
        // Start loading process
        areCoresLoading.value = true
        Log.d("Repository", "refreshCores called")
        coresScope.launch(Dispatchers.IO) {
            try {
                fetchCoresAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
                areCoresLoading.postValue(false)
                when (exception) {
                    is IOException -> coresSnackBar.postValue("Network problem occurred")
                    else -> {
                        coresSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
                }
            }
        }
    }

    fun cancelCoroutines() = coresJob.cancel()

    private suspend fun fetchCoresAndSaveToDb() {
        val response = spaceService.getAllCores().await()
        if (response.isSuccessful) {
            response.body()?.let { coresDao.insertCores(it) }
            // Save new cores last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CORES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${response.errorBody()}")
        // Cores no longer fetching, hide loading indicator
        areCoresLoading.postValue(false)
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