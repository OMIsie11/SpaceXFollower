package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.local.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_LAUNCHES_LAST_REFRESH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class LaunchesRepository(
    private val allLaunchesDao: AllLaunchesDao,
    private val spaceService: SpaceService,
    sharedPrefs: SharedPreferences
) : BaseRepository(sharedPrefs) {

    override val lastRefreshDataKey: String = KEY_LAUNCHES_LAST_REFRESH

    // Variables for showing/hiding loading indicators
    private val areLaunchesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    // Set value to message to be shown in snackbar
    private val launchesSnackBar = MutableLiveData<String>()

    // Wrapper for getting all capsules from Db
    fun getAllLaunchesFlow(): Flow<List<Launch>> = allLaunchesDao.getAllLaunchesFlow()

    suspend fun deleteAllLaunches() =
        withContext(Dispatchers.IO) { allLaunchesDao.deleteLaunchesData() }

    fun getLaunchesLoadingStatus(): LiveData<Boolean> = areLaunchesLoading

    fun getLaunchesSnackbar(): MutableLiveData<String> = launchesSnackBar

    suspend fun refreshData(forceRefresh: Boolean = false) {
        if (!forceRefresh) {
            // check if refresh is needed
            val isRefreshNeeded = withContext(Dispatchers.IO) {
                checkIfDataRefreshNeeded(lastRefreshDataKey)
            }
            if (!isRefreshNeeded) {
                Timber.d("No data refresh needed")
                return
            }
        }
        Timber.d("Refreshing data")
        performDataRefresh()
    }

    private suspend fun performDataRefresh() {
        // Start loading process
        areLaunchesLoading.postValue(true)
        Timber.d("refreshAllLaunches called")
        withContext(Dispatchers.IO) {
            try {
                fetchLaunchesAndSaveToDb()
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> launchesSnackBar.postValue("Network problem occurred")
                    else -> {
                        launchesSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
            areLaunchesLoading.postValue(false)
        }
    }

    private suspend fun fetchLaunchesAndSaveToDb() {
        Timber.d("fetchLaunchesAndSaveToDb called")
        val response = spaceService.getAllLaunches()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { allLaunchesDao.replaceAllLaunches(it) }
            // Save new launches last refresh time
            saveRefreshTime(lastRefreshDataKey)
        } else Timber.d("Error: ${response.errorBody()}")
    }
}