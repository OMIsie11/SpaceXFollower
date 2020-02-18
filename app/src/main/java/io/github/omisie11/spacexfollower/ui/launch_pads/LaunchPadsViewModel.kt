package io.github.omisie11.spacexfollower.ui.launch_pads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.omisie11.spacexfollower.data.repository.LaunchPadsRepository
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LaunchPadsViewModel(private val repository: LaunchPadsRepository) : ViewModel() {

    private val allLaunchPads: MutableLiveData<List<LaunchPad>> = MutableLiveData()
    private val _isDataLoading: LiveData<Boolean> by lazy { repository.getDataLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getLaunchPadsSnackbar()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLaunchPadsFlow().collect { launchPads ->
                allLaunchPads.postValue(launchPads)
            }
        }
    }

    fun getLaunchPads(): LiveData<List<LaunchPad>> = allLaunchPads

    fun getLaunchPadsLoadingStatus(): LiveData<Boolean> = _isDataLoading

    fun refreshLaunchPads() = viewModelScope.launch { repository.refreshData(forceRefresh = true) }

    // Wrapper for refreshing old data in onResume
    fun refreshIfLaunchPadsDataOld() =
        viewModelScope.launch { repository.refreshData(forceRefresh = false) }

    fun deleteLaunchPadsData() = viewModelScope.launch { repository.deleteLaunchPadsData() }

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