package io.github.omisie11.spacexfollower.ui.cores

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException


class CoresRepository(
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val sharedPrefs: SharedPreferences
) {

    private var areCoresLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val coresSnackBar = MutableLiveData<String>()

    init {
        areCoresLoading.value = false
    }

    fun getCores(): LiveData<List<Core>> = coresDao.getAllCores()

    suspend fun deleteAllCores() = withContext(Dispatchers.IO) { coresDao.deleteAllCores() }

    fun getCoresLoadingStatus(): LiveData<Boolean> = areCoresLoading

    fun getCoresSnackbar(): MutableLiveData<String> = coresSnackBar

    suspend fun refreshIfCoresDataOld() {
        val isCoresRefreshNeeded = withContext(Dispatchers.IO) { checkIfRefreshIsNeeded(KEY_CORES_LAST_REFRESH) }
        if (isCoresRefreshNeeded) {
            Timber.d("refreshIfCoresDataOld: Refreshing cores")
            refreshCores()
        } else Timber.d("refreshIfCoresDataOld: No refresh needed")
    }

    suspend fun refreshCores() {
        // Start loading process
        areCoresLoading.value = true
        Timber.d("refreshCores called")
        withContext(Dispatchers.IO) {
            try {
                fetchCoresAndSaveToDb()
            } catch (exception: Exception) {
                areCoresLoading.postValue(false)
                when (exception) {
                    is IOException -> coresSnackBar.postValue("Network problem occurred")
                    else -> {
                        coresSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchCoresAndSaveToDb() {
        val response = spaceService.getAllCores()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { coresDao.insertCores(it) }
            // Save new cores last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CORES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Timber.d("Error: ${response.errorBody()}")
        // Cores no longer fetching, hide loading indicator
        areCoresLoading.postValue(false)
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