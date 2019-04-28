package io.github.omisie11.spacexfollower.ui.cores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.CoresRepository
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.coroutines.*

class CoresViewModel(private val repository: CoresRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val mAllCores: LiveData<List<Core>> by lazy { repository.getCores() }
    private val _areCoresLoading: LiveData<Boolean> by lazy { repository.getCoresLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCoresSnackbar()

    fun getCores(): LiveData<List<Core>> = mAllCores

    fun getCoresLoadingStatus(): LiveData<Boolean> = _areCoresLoading

    // Wrapper for refreshing cores data
    fun refreshCores() = uiScope.launch { repository.refreshCores() }

    // Wrapper for refreshing old data in onResume
    fun refreshIfCoresDataOld() = uiScope.launch { repository.refreshIfCoresDataOld() }

    fun deleteCoresData() = uiScope.launch { repository.deleteAllCores() }

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
        uiScope.cancel()
    }
}