package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.coroutines.*


class CapsulesViewModel(private val repository: CapsulesRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _areCapsulesLoading: LiveData<Boolean> = repository.getCapsulesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    // LiveData stream from database
    private val _capsulesBySerialDesc: LiveData<List<Capsule>> by lazy {
        repository.getCapsulesOrderBySerialDesc()
    }
    private val _capsulesBySerialAsc: LiveData<List<Capsule>> by lazy {
        repository.getCapsulesOrderBySerialAsc()
    }
    // MediatorLiveData for sorting books
    private val capsules = MediatorLiveData<List<Capsule>>()

    private var currentSortType: CapsulesSortOrder = CapsulesSortOrder.BY_SERIAL_ASC

    init {
        // Add sources to mediator live data
        capsules.addSource(_capsulesBySerialAsc) { result ->
            if (currentSortType == CapsulesSortOrder.BY_SERIAL_ASC) result.let {
                capsules.value = it
            }
        }
        capsules.addSource(_capsulesBySerialDesc) { result ->
            if (currentSortType == CapsulesSortOrder.BY_SERIAL_DESC) result.let {
                capsules.value = it
            }
        }
    }

    fun getCapsules(): LiveData<List<Capsule>> = capsules

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = _areCapsulesLoading

    fun changeCapsulesSorting(sortType: CapsulesSortOrder) = when (sortType) {
        CapsulesSortOrder.BY_SERIAL_ASC -> _capsulesBySerialAsc.value.let { capsules.value = it }
        CapsulesSortOrder.BY_SERIAL_DESC -> _capsulesBySerialDesc.value.let { capsules.value = it }
    }.also { currentSortType = sortType }

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

    // Class representing all possible sort orders of capsules
    enum class CapsulesSortOrder { BY_SERIAL_ASC, BY_SERIAL_DESC }
}

