package io.github.omisie11.spacexfollower.ui.company

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.network.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_COMPANY_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.PREFS_KEY_REFRESH_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class CompanyRepository(
    private val spaceService: SpaceService,
    private val companyDao: CompanyDao,
    private val sharedPrefs: SharedPreferences
) {

    private val isCompanyInfoLoading: MutableLiveData<Boolean> = MutableLiveData()
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
            Timber.d("refreshIfCompanyDataOld: Refreshing company info")
            refreshCompanyInfo()
        } else Timber.d("refreshIfCompanyDataOld: No refresh needed")
    }

    suspend fun refreshCompanyInfo() {
        // Start loading process
        isCompanyInfoLoading.value = true
        Timber.d("refreshCompanyInfo called")
        withContext(Dispatchers.IO) {
            try {
                fetchCompanyInfoAndSaveToDb()
            } catch (exception: Exception) {
                isCompanyInfoLoading.postValue(false)
                when (exception) {
                    is IOException -> companyInfoSnackBar.postValue("Network problem occurred")
                    else -> {
                        companyInfoSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
        }
    }

    private suspend fun fetchCompanyInfoAndSaveToDb() {
        val response = spaceService.getCompanyInfo()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let { companyDao.insertCompanyInfo(it) }
            // Save company info last refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_COMPANY_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        } else Timber.d("Error: ${response.errorBody()}")
        // Cores no longer fetching, hide loading indicator
        isCompanyInfoLoading.postValue(false)
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