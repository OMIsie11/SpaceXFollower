package io.github.omisie11.spacexfollower.ui.upcoming_launches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.UpcomingLaunchesRepository
import io.github.omisie11.spacexfollower.data.model.UpcomingLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UpcomingLaunchesViewModel(
    private val repository: UpcomingLaunchesRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val allUpcomingLaunches: LiveData<List<UpcomingLaunch>> by lazy { repository.getUpcomingLaunches() }
    private val _areLaunchesLoading: LiveData<Boolean> = repository.getLaunchesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getLaunchesSnackbar()

    fun getUpcomingLaunches(): LiveData<List<UpcomingLaunch>> = allUpcomingLaunches

    fun getLaunchesLoadingStatus(): LiveData<Boolean> = _areLaunchesLoading

    // Wrapper for refreshing launches data
    fun refreshUpcomingLaunches() {
        uiScope.launch {
            repository.refreshUpcomingLaunches()
        }
    }

    // Wrapper for refreshing old data in onResume
    fun refreshIfLaunchesDataOld() = uiScope.launch { repository.refreshIfLaunchesDataOld() }

    fun deleteLaunchesData() = uiScope.launch { repository.deleteAllUpcomingLaunches() }

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