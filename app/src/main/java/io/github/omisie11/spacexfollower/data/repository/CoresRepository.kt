package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.local.dao.CoresDao
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class CoresRepository(
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    sharedPrefs: SharedPreferences
) : BaseRepository(sharedPrefs) {

    override val lastRefreshDataKey: String = KEY_CORES_LAST_REFRESH

    private val areCoresLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val coresSnackBar = MutableLiveData<String>()

    fun getAllCoresFlow(): Flow<List<Core>> = coresDao.getAllCoresFlow()

    suspend fun deleteAllCores() = withContext(Dispatchers.IO) { coresDao.deleteAllCores() }

    fun getCoresLoadingStatus(): LiveData<Boolean> = areCoresLoading

    fun getCoresSnackbar(): MutableLiveData<String> = coresSnackBar

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
        areCoresLoading.postValue(true)
        withContext(Dispatchers.IO) {
            try {
                fetchCoresAndSaveToDb()
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> coresSnackBar.postValue("Network problem occurred")
                    else -> {
                        coresSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
            areCoresLoading.postValue(false)
        }
    }

    private suspend fun fetchCoresAndSaveToDb() {
        val response = spaceService.getAllCores()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { coresDao.replaceCoresData(it) }
            // Save new cores last refresh time
            saveRefreshTime(lastRefreshDataKey)
        } else Timber.d("Error: ${response.errorBody()}")
    }
}
