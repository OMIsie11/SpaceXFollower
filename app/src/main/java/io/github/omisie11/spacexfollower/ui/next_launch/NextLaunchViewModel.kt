package io.github.omisie11.spacexfollower.ui.next_launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.NextLaunchRepository
import io.github.omisie11.spacexfollower.data.model.NextLaunch

class NextLaunchViewModel(private val repository: NextLaunchRepository) : ViewModel() {

    private val nextLaunch: LiveData<NextLaunch> by lazy { repository.getNextLaunch() }
    private val _isNextLaunchLoading: LiveData<Boolean> by lazy { repository.getNextLaunchLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getNextLaunchSnackbar()

    fun getNextLaunchInfo(): LiveData<NextLaunch> = nextLaunch

    fun getNextLaunchLoadingStatus(): LiveData<Boolean> = _isNextLaunchLoading

    // Wrapper for refreshing next launch data
    fun refreshNextLaunch() = repository.refreshNextLaunchInfo()

    // Wrapper for refreshing old data in onResume
    fun refreshIfNextLaunchDataOld() = repository.refreshIfCapsulesDataOld()

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