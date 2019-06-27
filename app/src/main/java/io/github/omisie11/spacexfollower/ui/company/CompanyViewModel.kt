package io.github.omisie11.spacexfollower.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Company
import kotlinx.coroutines.*

class CompanyViewModel(private val repository: CompanyRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mCompanyInfo by lazy { repository.getCompanyInfo() }
    private val _isCompanyInfoLoading by lazy { repository.getCompanyInfoLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCompanyInfoSnackbar()

    fun getCompanyInfo(): LiveData<Company> = mCompanyInfo

    fun getCompanyInfoLoadingStatus() = _isCompanyInfoLoading

    fun refreshCompanyInfo() = uiScope.launch { repository.refreshCompanyInfo() }

    fun refreshIfCompanyDataOld() = uiScope.launch { repository.refreshIfCompanyDataOld() }

    fun deleteCompanyInfo() = uiScope.launch { repository.deleteCompanyInfo() }

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String>
        get() = _snackBar

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }
}