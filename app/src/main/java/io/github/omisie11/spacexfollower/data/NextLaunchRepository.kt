package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.NextLaunchDao
import io.github.omisie11.spacexfollower.data.model.NextLaunch
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_NEXT_LAUNCH_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.*
import java.io.IOException

class NextLaunchRepository(
    private val nextLaunchDao: NextLaunchDao,
    private val spaceService: SpaceService,
    private val sharedPrefs: SharedPreferences
) {

    // Variables for showing/hiding loading indicators
    private var isNextLaunchLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val nextLaunchSnackBar = MutableLiveData<String>()

    init {
        isNextLaunchLoading.value = false
    }

    // Wrapper for getting all capsules from Db
    fun getNextLaunch(): LiveData<NextLaunch> {
       // if (checkIfRefreshIsNeeded(KEY_NEXT_LAUNCH_LAST_REFRESH)) {
       //     refreshNextLaunchInfo()
       //     Log.d("refreshNextLaunch", "Refreshing next launch data")
       // }
        return nextLaunchDao.getNextLaunch()
    }

    suspend fun deleteNextLaunchData() = withContext(Dispatchers.IO) { nextLaunchDao.deleteNextLaunchInfo() }

    fun getNextLaunchLoadingStatus(): LiveData<Boolean> = isNextLaunchLoading

    fun getNextLaunchSnackbar(): MutableLiveData<String> = nextLaunchSnackBar

    suspend fun refreshIfCapsulesDataOld() {
        if (checkIfRefreshIsNeeded(KEY_NEXT_LAUNCH_LAST_REFRESH)) {
            refreshNextLaunchInfo()
            Log.d("refreshCapsules", "Refreshing capsules")
        }
    }

    suspend fun refreshNextLaunchInfo() {
        // Start loading process
        isNextLaunchLoading.value = true
        Log.d("Repository", "refreshNextLaunch called")
        withContext(Dispatchers.IO) {
            try {
                fetchNextLaunchInfoAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
                isNextLaunchLoading.postValue(false)
                when (exception) {
                    is IOException -> nextLaunchSnackBar.postValue("Network problem occurred")
                    else -> {
                        nextLaunchSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchNextLaunchInfoAndSaveToDb() {
        val response = spaceService.getNextLaunch().await()
        if (response.isSuccessful) {
            response.body()?.let { nextLaunchDao.insertNextLaunch(it) }
            // Save new capsules last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_NEXT_LAUNCH_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${response.errorBody()}")
        // Capsules no longer fetching, hide loading indicator
        isNextLaunchLoading.postValue(false)
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