package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CapsulesViewModel(private val repository: CapsulesRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val allCapsules: LiveData<List<Capsule>> by lazy { repository.getCapsules() }
    private val _areCapsulesLoading: LiveData<Boolean> = repository.getCapsulesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    fun getCapsules(): LiveData<List<Capsule>> = allCapsules

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = _areCapsulesLoading

    // Wrapper for refreshing capsules data
    fun refreshCapsules() {
        uiScope.launch {
            repository.refreshCapsules()
        }
    }

    // Wrapper for refreshing old data in onResume
    fun refreshIfCapsulesDataOld() = uiScope.launch { repository.refreshIfCapsulesDataOld() }

    fun deleteCapsulesData() = uiScope.launch { repository.deleteAllCapsules() }

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