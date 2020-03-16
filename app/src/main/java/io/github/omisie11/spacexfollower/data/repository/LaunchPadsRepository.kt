package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.local.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_LAUNCH_PADS_LAST_REFRESH
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class LaunchPadsRepository(
    private val spaceService: SpaceService,
    private val launchPadsDao: LaunchPadsDao,
    sharedPrefs: SharedPreferences
) : BaseRepository(sharedPrefs) {

    override val lastRefreshDataKey: String = KEY_LAUNCH_PADS_LAST_REFRESH

    private val areLaunchPadsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val launchPadsSnackBar = MutableLiveData<String>()

    fun getLaunchPadsFlow(): Flow<List<LaunchPad>> = launchPadsDao.getLaunchPadsFlow()

    suspend fun deleteLaunchPadsData() =
        withContext(Dispatchers.IO) { launchPadsDao.deleteLaunchPadsData() }

    fun getDataLoadingStatus(): LiveData<Boolean> = areLaunchPadsLoading

    fun getLaunchPadsSnackbar(): MutableLiveData<String> = launchPadsSnackBar

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
        areLaunchPadsLoading.postValue(true)
        Timber.d("refreshLaunchPads called")
        withContext(Dispatchers.IO) {
            try {
                fetchLaunchPadsAndSaveToDb()
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> launchPadsSnackBar.postValue("Network problem occurred")
                    else -> {
                        launchPadsSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
            areLaunchPadsLoading.postValue(false)
        }
    }

    private suspend fun fetchLaunchPadsAndSaveToDb() {
        val response = spaceService.getLaunchPads()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { launchPadsDao.replaceLaunchPads(it) }
            // Save data last refresh time
            saveRefreshTime(lastRefreshDataKey)
        } else Timber.d("Error: ${response.errorBody()}")
    }
}
