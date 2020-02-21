package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.local.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class CapsulesRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    sharedPrefs: SharedPreferences
) : BaseRepository(sharedPrefs) {

    override val lastRefreshDataKey: String = KEY_CAPSULES_LAST_REFRESH

    // Variables for showing/hiding loading indicators
    private val areCapsulesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    // Set value to message to be shown in snackbar
    private val capsulesSnackBar = MutableLiveData<String>()

    fun getAllCapsulesFlow(): Flow<List<Capsule>> = capsulesDao.getAllCapsulesFlow()

    suspend fun deleteAllCapsules() =
        withContext(Dispatchers.IO) { capsulesDao.deleteAllCapsules() }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = areCapsulesLoading

    fun getCapsulesSnackbar(): MutableLiveData<String> = capsulesSnackBar

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
        areCapsulesLoading.postValue(true)
        Timber.d("refreshCapsules called")
        withContext(Dispatchers.IO) {
            try {
                fetchCapsulesAndSaveToDb()
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> capsulesSnackBar.postValue("Network problem occurred")
                    else -> {
                        capsulesSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
            areCapsulesLoading.postValue(false)
        }
    }

    private suspend fun fetchCapsulesAndSaveToDb() {
        Timber.d("fetchCapsulesAndSaveToDb called")
        val response = spaceService.getAllCapsules()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let {
                capsulesDao.replaceCapsulesData(it)
            }
            // Save new capsules last refresh time
            saveRefreshTime(lastRefreshDataKey)
        } else Timber.d("Error: ${response.errorBody()}")
    }
}