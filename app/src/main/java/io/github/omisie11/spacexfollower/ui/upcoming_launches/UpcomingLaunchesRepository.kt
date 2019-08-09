package io.github.omisie11.spacexfollower.ui.upcoming_launches

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.UpcomingLaunchesDao
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_UPCOMING_LAUNCHES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class UpcomingLaunchesRepository(
    private val upcomingLaunchesDao: UpcomingLaunchesDao,
    private val spaceService: SpaceService,
    private val sharedPrefs: SharedPreferences
) {

    // Variables for showing/hiding loading indicators
    private var areLaunchesLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val launchesSnackBar = MutableLiveData<String>()

    init {
        areLaunchesLoading.value = false
    }

    // Wrapper for getting all capsules from Db
    fun getUpcomingLaunches(): LiveData<List<UpcomingLaunch>> = upcomingLaunchesDao.getUpcomingLaunches()

    suspend fun deleteAllUpcomingLaunches() =
        withContext(Dispatchers.IO) { upcomingLaunchesDao.deleteUpcomingLaunchesData() }

    fun getLaunchesLoadingStatus(): LiveData<Boolean> = areLaunchesLoading

    fun getLaunchesSnackbar(): MutableLiveData<String> = launchesSnackBar

    suspend fun refreshIfLaunchesDataOld() {
        val isLaunchesRefreshNeeded = withContext(Dispatchers.IO) {
            checkIfRefreshIsNeeded(KEY_UPCOMING_LAUNCHES_LAST_REFRESH)
        }
        if (isLaunchesRefreshNeeded) {
            Log.d("UpcomingLRepo", "refreshIfLaunchesDataOld: Refreshing Upcoming launches")
            refreshUpcomingLaunches()
        } else Log.d("CapsulesRepo", "refreshIfLaunchesDataOld: No refresh needed")
    }

    suspend fun refreshUpcomingLaunches() {
        // Start loading process
        areLaunchesLoading.value = true
        Log.d("Repository", "refreshUpcomingLaunches called")
        withContext(Dispatchers.IO) {
            try {
                fetchLaunchesAndSaveToDb()
            } catch (exception: Exception) {
                areLaunchesLoading.postValue(false)
                when (exception) {
                    is IOException -> launchesSnackBar.postValue("Network problem occurred")
                    else -> {
                        launchesSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchLaunchesAndSaveToDb() {
        Log.d("Repo", "fetchLaunchesAndSaveToDb called")
        val response = spaceService.getUpcomingLaunches()
        if (response.isSuccessful) {
            Log.d("UpcomingLRepo", "Response SUCCESSFUL")
            response.body()?.let { upcomingLaunchesDao.insertNewUpcomingLaunches(it) }
            // Save new launches last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_UPCOMING_LAUNCHES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${response.errorBody()}")
        // Launches no longer fetching, hide loading indicator
        areLaunchesLoading.postValue(false)
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