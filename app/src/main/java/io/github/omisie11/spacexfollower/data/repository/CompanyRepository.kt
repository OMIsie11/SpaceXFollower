package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.omisie11.spacexfollower.data.local.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.local.model.Company
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.KEY_COMPANY_LAST_REFRESH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class CompanyRepository(
    private val spaceService: SpaceService,
    private val companyDao: CompanyDao,
    sharedPrefs: SharedPreferences
) : BaseRepository(
    sharedPrefs
) {
    override val lastRefreshDataKey: String = KEY_COMPANY_LAST_REFRESH

    private val isCompanyInfoLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val companyInfoSnackBar = MutableLiveData<String>()

    init {
        isCompanyInfoLoading.value = false
    }

    fun getCompanyInfo(): LiveData<Company> = companyDao.getCompanyInfo()

    suspend fun deleteCompanyInfo() = withContext(Dispatchers.IO) { companyDao.deleteCompanyInfo() }

    fun getCompanyInfoLoadingStatus(): LiveData<Boolean> = isCompanyInfoLoading

    fun getCompanyInfoSnackbar(): MutableLiveData<String> = companyInfoSnackBar

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
        isCompanyInfoLoading.value = true
        Timber.d("refreshCompanyInfo called")
        withContext(Dispatchers.IO) {
            try {
                fetchCompanyInfoAndSaveToDb()
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> companyInfoSnackBar.postValue("Network problem occurred")
                    else -> {
                        companyInfoSnackBar.postValue("Unexpected problem occurred")
                        Timber.d("Exception: $exception")
                    }
                }
            }
            isCompanyInfoLoading.postValue(false)
        }
    }

    private suspend fun fetchCompanyInfoAndSaveToDb() {
        Timber.d("fetchCompanyInfoAndSaveToDb called")
        val response = spaceService.getCompanyInfo()
        if (response.isSuccessful) {
            Timber.d("Response SUCCESSFUL")
            response.body()?.let {
                companyDao.insertCompanyInfo(it)
            }
            // Save company info last refresh time
            saveRefreshTime(lastRefreshDataKey)
        } else Timber.d("Error: ${response.errorBody()}")
    }
}