package io.github.omisie11.spacexfollower.ui.launch_pads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.LaunchPad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LaunchPadsViewModel(private val repository: LaunchPadsRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val allLaunchPads: MutableLiveData<List<LaunchPad>> = MutableLiveData()
    private val _isDataLoading: LiveData<Boolean> by lazy { repository.getDataLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getLaunchPadsSnackbar()

    init {
        uiScope.launch(Dispatchers.Default) {
            repository.getLaunchPadsFlow().collect { launchPads ->
                allLaunchPads.postValue(launchPads)
            }
        }
    }

    fun getLaunchPads(): LiveData<List<LaunchPad>> = allLaunchPads

    fun getLaunchPadsLoadingStatus(): LiveData<Boolean> = _isDataLoading

    fun refreshLaunchPads() = uiScope.launch { repository.refreshLaunchPads() }

    // Wrapper for refreshing old data in onResume
    fun refreshIfLaunchPadsDataOld() = uiScope.launch { repository.refreshIfLaunchPadsDataOld() }

    fun deleteLaunchPadsData() = uiScope.launch { repository.deleteLaunchPadsData() }

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