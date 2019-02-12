package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import kotlinx.coroutines.*
import java.io.IOException
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
    // Variables for showing/hiding loading indicators
    private var areCapsulesLoading: MutableLiveData<Boolean> = MutableLiveData()
    private var areCoresLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        areCapsulesLoading.value = false
        areCoresLoading.value = false
    }

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

    fun deleteAllCapsules() = repositoryScope.launch { capsulesDao.deleteAllCapsules() }

    fun deleteAllCores() = repositoryScope.launch { coresDao.deleteAllCores() }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = areCapsulesLoading

    fun getCoresLoadingStatus(): LiveData<Boolean> = areCoresLoading

    fun refreshIfCapsulesDataOld() {
        if (checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH)) {
            refreshCapsules()
            Log.d("refreshCapsules", "Refreshing capsules")
        }
    }

    fun refreshIfCoresDataOld() {
        if (checkIfRefreshIsNeeded(KEY_CORES_LAST_REFRESH)) {
            refreshCores()
            Log.d("refreshCores", "Refreshing cores")
        }
    }

    fun refreshCapsules() {
        // Start loading process
        areCapsulesLoading.value = true
        Log.d("Repository", "refreshCapsules called")
        repositoryScope.launch {
            try {
                fetchCapsulesAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
                areCapsulesLoading.postValue(false)
                when (exception) {
                    is IOException -> Log.d("Repo", "Network problem")
                    else -> Log.d("Repo", "Exception: $exception")
                }
            }
        }
    }

    fun refreshCores() {
        // Start loading process
        areCoresLoading.value = true
        Log.d("Repository", "refreshCores called")
        repositoryScope.launch {
            try {
                fetchCoresAndSaveToDb()
            } catch (exception: Exception) {
                // ToDo: Handle exceptions and no network exception
                areCoresLoading.postValue(false)
                when (exception) {
                    is IOException -> Log.d("Repo", "Network problem")
                    else -> Log.d("Repo", "Exception: $exception")
                }
            }
        }
    }

    private suspend fun fetchCapsulesAndSaveToDb() {
        val response = spaceService.getAllCapsules().await()
        if (response.isSuccessful) {
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
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > REFRESH_INTERVAL
    }
}