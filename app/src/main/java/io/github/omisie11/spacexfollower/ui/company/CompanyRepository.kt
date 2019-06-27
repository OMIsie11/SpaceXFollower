package io.github.omisie11.spacexfollower.ui.company

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_COMPANY_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.*
import java.io.IOException


class CompanyRepository(
    private val spaceService: SpaceService,
    private val companyDao: CompanyDao,
    private val sharedPrefs: SharedPreferences
) {

    private var isCompanyInfoLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val companyInfoSnackBar = MutableLiveData<String>()

    init {
        isCompanyInfoLoading.value = false
    }

    fun getCompanyInfo(): LiveData<Company> = companyDao.getCompanyInfo()

    suspend fun deleteCompanyInfo() = withContext(Dispatchers.IO) { companyDao.deleteCompanyInfo() }

    fun getCompanyInfoLoadingStatus(): LiveData<Boolean> = isCompanyInfoLoading

    fun getCompanyInfoSnackbar(): MutableLiveData<String> = companyInfoSnackBar

    suspend fun refreshIfCompanyDataOld() {
        val isCompanyRefreshNeeded = withContext(Dispatchers.IO) { checkIfRefreshIsNeeded(KEY_COMPANY_LAST_REFRESH) }
        if (isCompanyRefreshNeeded) {
            Log.d("CompanyRepo", "refreshIfCompanyDataOld: Refreshing company info")
            refreshCompanyInfo()
        } else Log.d("CompanyRepo", "refreshIfCompanyDataOld: No refresh needed")
    }

    suspend fun refreshCompanyInfo() {
        // Start loading process
        isCompanyInfoLoading.value = true
        Log.d("Repository", "refreshCompanyInfo called")
        withContext(Dispatchers.IO) {
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


    private suspend fun fetchCompanyInfoAndSaveToDb() {
        val response = spaceService.getCompanyInfo()
        if (response.isSuccessful) {
            Log.d("CompanyRepo", "Response SUCCESSFUL")
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
        val refreshIntervalHours = sharedPrefs.getString(PREFS_KEY_REFRESH_INTERVAL, "3")?.toInt() ?: 3
        val refreshInterval = refreshIntervalHours * 3600000
        Log.d("Repository", "Refresh Interval from settings: $refreshInterval")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > refreshInterval
    }
}