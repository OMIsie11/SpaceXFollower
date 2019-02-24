package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.KEY_COMPANY_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception


class SpaceRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val companyDao: CompanyDao,
    private val sharedPrefs: SharedPreferences
) {

    companion object {
        // Data refresh interval in milliseconds (default: 3h = 10800000 ms)
        //private const val REFRESH_INTERVAL: Long = 10800000
    }

    private val repositoryJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)
    // Variables for showing/hiding loading indicators
    private var areCapsulesLoading: MutableLiveData<Boolean> = MutableLiveData()
    private var areCoresLoading: MutableLiveData<Boolean> = MutableLiveData()
    private var isCompanyInfoLoading: MutableLiveData<Boolean> = MutableLiveData()
    // Set value to message to be shown in snackbar
    private val capsulesSnackBar = MutableLiveData<String>()
    private val coresSnackBar = MutableLiveData<String>()
    private val companyInfoSnackBar = MutableLiveData<String>()

    init {
        areCapsulesLoading.value = false
        areCoresLoading.value = false
        isCompanyInfoLoading.value = false
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

    fun getCompanyInfo(): LiveData<Company> {
        // Check if refresh is needed
        if (checkIfRefreshIsNeeded(KEY_COMPANY_LAST_REFRESH)) {
            refreshCompanyInfo()
            Log.d("refreshCompanyInfo", "Refreshing cores")
        }
        return companyDao.getCompanyInfo()
    }

    fun deleteAllCapsules() = repositoryScope.launch { capsulesDao.deleteAllCapsules() }

    fun deleteAllCores() = repositoryScope.launch { coresDao.deleteAllCores() }

    fun deleteCompanyInfo() = repositoryScope.launch { companyDao.deleteCompanyInfo() }

    // Loading info values
    fun getCapsulesLoadingStatus(): LiveData<Boolean> = areCapsulesLoading

    fun getCoresLoadingStatus(): LiveData<Boolean> = areCoresLoading

    fun getCompanyInfoLoadingStatus(): LiveData<Boolean> = isCompanyInfoLoading

    // Snackbars values
    fun getCapsulesSnackbar(): MutableLiveData<String> = capsulesSnackBar

    fun getCoresSnackbar(): MutableLiveData<String> = coresSnackBar

    fun getCompanyInfoSnackbar(): MutableLiveData<String> = companyInfoSnackBar

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

    fun refreshIfCompanyDataOld() {
        if (checkIfRefreshIsNeeded(KEY_COMPANY_LAST_REFRESH)) {
            refreshCompanyInfo()
            Log.d("refreshCompanyInfo", "Refreshing company info")
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
                    is IOException -> capsulesSnackBar.postValue("Network problem occurred")
                    else -> {
                        capsulesSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
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
                    is IOException -> coresSnackBar.postValue("Network problem occurred")
                    else -> {
                        coresSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
                }
            }
        }
    }

    fun refreshCompanyInfo() {
        // Start loading process
        isCompanyInfoLoading.value = true
        Log.d("Repository", "refreshCompanyInfo called")
        repositoryScope.launch {
            try {
                fetchCompanyInfoAndSaveToDb()
            } catch (exception: Exception) {
                // ToDO: handle exceptions
                isCompanyInfoLoading.postValue(false)
                when (exception) {
                    is IOException -> companyInfoSnackBar.postValue("Network problem occurred")
                    else -> {
                        companyInfoSnackBar.postValue("Unexpected problem occurred")
                        Log.d("Repo", "Exception: $exception")
                    }
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

    private suspend fun fetchCompanyInfoAndSaveToDb() {
        val response = spaceService.getCompanyInfo().await()
        if (response.isSuccessful) {
            response.body()?.let { companyDao.insertCompanyInfo(it) }
            // Save company info last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_COMPANY_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Log.d("Repository", "Error: ${response.errorBody()}")
        // Cores no longer fetching, hide loading indicator
        isCompanyInfoLoading.postValue(false)
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Log.d("Repository", "Current time in millis $currentTimeMillis")
        // Get refresh interval set in app settings (in hours) and multiply to get value in ms
        val refreshIntervalHours = sharedPrefs.getString("prefs_refresh_interval", "3")?.toInt() ?: 3
        val refreshInterval = refreshIntervalHours * 3600000
        Log.d("Repository", "Refresh Interval from settings: $refreshInterval")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > refreshInterval
    }
}