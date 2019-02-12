package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCapsules: LiveData<List<Capsule>> by lazy { repository.getCapsules() }
    private val mAreCapsulesLoading: LiveData<Boolean> by lazy { repository.getCapsulesLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    fun getCapsules(): LiveData<List<Capsule>> = mAllCapsules

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = mAreCapsulesLoading

    // Wrapper for refreshing capsules data
    fun refreshCapsules() = repository.refreshCapsules()

    // Wrapper for refreshing old data in onResume
    fun refreshIfCapsulesDataOld() = repository.refreshIfCapsulesDataOld()

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