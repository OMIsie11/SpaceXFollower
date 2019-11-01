package io.github.omisie11.spacexfollower.ui.launch_pads

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.data.model.LaunchPad
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_LAUNCH_PADS_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class LaunchPadsRepository(
    private val spaceService: SpaceService,
    private val launchPadsDao: LaunchPadsDao,
    private val sharedPrefs: SharedPreferences
) {

    private val areLaunchPadsLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val launchPadsSnackBar = MutableLiveData<String>()

    init {
        areLaunchPadsLoading.value = false
    }

    fun getLaunchPadsFlow(): Flow<List<LaunchPad>> = launchPadsDao.getLaunchPadsFlow()

    suspend fun deleteLaunchPadsData() =
        withContext(Dispatchers.IO) { launchPadsDao.deleteLaunchPadsData() }

    fun getDataLoadingStatus(): LiveData<Boolean> = areLaunchPadsLoading

    fun getLaunchPadsSnackbar(): MutableLiveData<String> = launchPadsSnackBar

    suspend fun refreshIfLaunchPadsDataOld() {
        val isLaunchPadsRefreshNeeded =
            withContext(Dispatchers.IO) { checkIfRefreshIsNeeded(KEY_LAUNCH_PADS_LAST_REFRESH) }
        if (isLaunchPadsRefreshNeeded) {
            Timber.d("refreshIfLaunchPadsDataOld: Refreshing launch pads")
            refreshLaunchPads()
        } else Timber.d("refreshIfLaunchPadsDataOld: No refresh needed")
    }

    suspend fun refreshLaunchPads() {
        // Start loading process
        areLaunchPadsLoading.value = true
        Timber.d("refreshLaunchPads called")
        withContext(Dispatchers.IO) {
            try {
                fetchLaunchPadsAndSaveToDb()
            } catch (exception: Exception) {
                areLaunchPadsLoading.postValue(false)
                when (exception) {
                    is IOException -> launchPadsSnackBar.postValue("Network problem occurred")
                    else -> {
                        launchPadsSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchLaunchPadsAndSaveToDb() {
        val response = spaceService.getLaunchPads()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { launchPadsDao.replaceLaunchPads(it) }
            // Save data last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_LAUNCH_PADS_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Timber.d("Error: ${response.errorBody()}")
        // Data no longer fetching, hide loading indicator
        areLaunchPadsLoading.postValue(false)
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