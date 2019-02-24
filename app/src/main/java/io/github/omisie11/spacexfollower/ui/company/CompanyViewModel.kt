package io.github.omisie11.spacexfollower.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Company

class CompanyViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mCompanyInfo by lazy { repository.getCompanyInfo() }
    private val isCompanyInfoLoading by lazy { repository.getCompanyInfoLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCompanyInfoSnackbar()

    fun getCompanyInfo(): LiveData<Company> = mCompanyInfo

    fun getCompanyInfoLoadingStatus() = isCompanyInfoLoading

    fun refreshCompanyInfo() = repository.refreshCompanyInfo()

    fun refreshIfCompanyDataOld() = repository.refreshIfCompanyDataOld()

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
}