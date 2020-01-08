package io.github.omisie11.spacexfollower.ui.launches

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_UPCOMING_LAUNCHES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class LaunchesRepository(
    private val allLaunchesDao: AllLaunchesDao,
    private val spaceService: SpaceService,
    private val sharedPrefs: SharedPreferences
) {

    // Variables for showing/hiding loading indicators
    private val areLaunchesLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val launchesSnackBar = MutableLiveData<String>()

    init {
        areLaunchesLoading.value = false
    }

    // Wrapper for getting all capsules from Db
    fun getAllLaunchesFlow(): Flow<List<Launch>> = allLaunchesDao.getAllLaunchesFlow()

    suspend fun deleteAllLaunches() =
        withContext(Dispatchers.IO) { allLaunchesDao.deleteLaunchesData() }

    fun getLaunchesLoadingStatus(): LiveData<Boolean> = areLaunchesLoading

    fun getLaunchesSnackbar(): MutableLiveData<String> = launchesSnackBar

    suspend fun refreshIfLaunchesDataOld() {
        val isLaunchesRefreshNeeded = withContext(Dispatchers.IO) {
            checkIfRefreshIsNeeded(KEY_UPCOMING_LAUNCHES_LAST_REFRESH)
        }
        if (isLaunchesRefreshNeeded) {
            Timber.d("refreshIfLaunchesDataOld: Refreshing Upcoming launches")
            refreshLaunches()
        } else Timber.d("refreshIfLaunchesDataOld: No refresh needed")
    }

    suspend fun refreshLaunches() {
        // Start loading process
        areLaunchesLoading.value = true
        Timber.d("refreshAllLaunches called")
        withContext(Dispatchers.IO) {
            try {
                fetchLaunchesAndSaveToDb()
            } catch (exception: Exception) {
                areLaunchesLoading.postValue(false)
                when (exception) {
                    is IOException -> launchesSnackBar.postValue("Network problem occurred")
                    else -> {
                        launchesSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchLaunchesAndSaveToDb() {
        Timber.d("fetchLaunchesAndSaveToDb called")
        val response = spaceService.getUpcomingLaunches()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { allLaunchesDao.replaceUpcomingLaunches(it) }
            // Save new launches last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_UPCOMING_LAUNCHES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Timber.d("Error: ${response.errorBody()}")
        // Launches no longer fetching, hide loading indicator
        areLaunchesLoading.postValue(false)
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Timber.d("Current time in millis $currentTimeMillis")
        // Get refresh interval set in app settings (in hours) and multiply to get value in ms
        val refreshIntervalHours =
            sharedPrefs.getString(PREFS_KEY_REFRESH_INTERVAL, "3")?.toInt() ?: 3
        val refreshInterval = refreshIntervalHours * 3600000
        Timber.d("Refresh Interval from settings: $refreshInterval")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > refreshInterval
    }
}