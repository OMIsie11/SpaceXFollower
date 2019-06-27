package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.*
import io.github.omisie11.spacexfollower.data.CapsulesRepository
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.util.CAPSULES_SORT_SERIAL_ASC
import io.github.omisie11.spacexfollower.util.CAPSULES_SORT_SERIAL_DESC
import kotlinx.coroutines.*


class CapsulesViewModel(private val repository: CapsulesRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _areCapsulesLoading: LiveData<Boolean> = repository.getCapsulesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    // LiveData stream from database
    private val _capsulesBySerialDesc: LiveData<List<Capsule>> by lazy { repository.getCapsulesOrderBySerialDesc() }
    private val _capsulesBySerialAsc: LiveData<List<Capsule>> by lazy { repository.getCapsulesOrderBySerialAsc() }
    // MediatorLiveData for sorting books
    private val capsules = MediatorLiveData<List<Capsule>>()

    private var currentSortType: String = CAPSULES_SORT_SERIAL_ASC

    init {
        // Add sources to mediator live data
        capsules.addSource(_capsulesBySerialAsc) { result ->
            if (currentSortType == CAPSULES_SORT_SERIAL_ASC) result.let { capsules.value = it }
        }
        capsules.addSource(_capsulesBySerialDesc) { result ->
            if (currentSortType == CAPSULES_SORT_SERIAL_DESC) result.let { capsules.value = it }
        }
    }

    fun getCapsules(): LiveData<List<Capsule>> = capsules

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = _areCapsulesLoading

    fun changeCapsulesSorting(sortType: String) = when (sortType) {
        CAPSULES_SORT_SERIAL_ASC -> _capsulesBySerialAsc.value.let { capsules.value = it }
        CAPSULES_SORT_SERIAL_DESC -> _capsulesBySerialDesc.value.let { capsules.value = it }
        else -> _capsulesBySerialAsc.value.let { capsules.value = it }
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
}