package io.github.omisie11.spacexfollower.ui.capsules

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException


class CapsulesRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val sharedPrefs: SharedPreferences
) {

    // Variables for showing/hiding loading indicators
    private val areCapsulesLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val capsulesSnackBar = MutableLiveData<String>()

    init {
        areCapsulesLoading.value = false
    }

    // Wrapper for getting all capsules from Db
    fun getCapsulesOrderBySerialDesc(): LiveData<List<Capsule>> = capsulesDao.getAllCapsulesOrderBySerialDesc()

    fun getCapsulesOrderBySerialAsc(): LiveData<List<Capsule>> = capsulesDao.getAllCapsulesOrderBySerialAsc()

    suspend fun deleteAllCapsules() = withContext(Dispatchers.IO) { capsulesDao.deleteAllCapsules() }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = areCapsulesLoading

    fun getCapsulesSnackbar(): MutableLiveData<String> = capsulesSnackBar

    suspend fun refreshIfCapsulesDataOld() {
        val isCapsulesRefreshNeeded = withContext(Dispatchers.IO) { checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH) }
        if (isCapsulesRefreshNeeded) {
            Timber.d("refreshIfCapsulesDataOld: Refreshing capsules")
            refreshCapsules()
        } else Timber.d("refreshIfCapsulesDataOld: No refresh needed")
    }

    suspend fun refreshCapsules() {
        // Start loading process
        areCapsulesLoading.value = true
        Timber.d("refreshCapsules called")
        withContext(Dispatchers.IO) {
            try {
                fetchCapsulesAndSaveToDb()
            } catch (exception: Exception) {
                areCapsulesLoading.postValue(false)
                when (exception) {
                    is IOException -> capsulesSnackBar.postValue("Network problem occurred")
                    else -> {
                        capsulesSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchCapsulesAndSaveToDb() {
        Timber.d( "fetchCapsulesAndSaveToDb called")
        val response = spaceService.getAllCapsules()
        if (response.isSuccessful) {
            Timber.d( "Response SUCCESSFUL")
            response.body()?.let { capsulesDao.insertCapsules(it) }
            // Save new capsules last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CAPSULES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Timber.d("Error: ${response.errorBody()}")
        // Capsules no longer fetching, hide loading indicator
        areCapsulesLoading.postValue(false)
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Timber.d("Current time in millis $currentTimeMillis")
        // Get refresh interval set in app settings (in hours) and multiply to get value in ms
        val refreshIntervalHours = sharedPrefs.getString(PREFS_KEY_REFRESH_INTERVAL, "3")?.toInt() ?: 3
        val refreshInterval = refreshIntervalHours * 3600000
        Timber.d("Refresh Interval from settings: $refreshInterval")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > refreshInterval
    }
}