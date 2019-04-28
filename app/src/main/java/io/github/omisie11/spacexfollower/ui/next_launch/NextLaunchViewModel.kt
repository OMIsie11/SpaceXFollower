package io.github.omisie11.spacexfollower.ui.next_launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.NextLaunchRepository
import io.github.omisie11.spacexfollower.data.model.NextLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NextLaunchViewModel(private val repository: NextLaunchRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val nextLaunch: LiveData<NextLaunch> by lazy { repository.getNextLaunch() }
    private val _isNextLaunchLoading: LiveData<Boolean> by lazy { repository.getNextLaunchLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getNextLaunchSnackbar()

    fun getNextLaunchInfo(): LiveData<NextLaunch> = nextLaunch

    fun getNextLaunchLoadingStatus(): LiveData<Boolean> = _isNextLaunchLoading

    // Wrapper for refreshing next launch data
    fun refreshNextLaunch() = uiScope.launch { repository.refreshNextLaunchInfo() }

    // Wrapper for refreshing old data in onResume
    fun refreshIfNextLaunchDataOld() = uiScope.launch { repository.refreshIfCapsulesDataOld() }

    fun deleteNextLaunchData() = uiScope.launch { repository.deleteNextLaunchData() }

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