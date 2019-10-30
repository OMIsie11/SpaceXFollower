package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CapsulesViewModel(private val repository: CapsulesRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val allCapsules: MutableLiveData<List<Capsule>> = MutableLiveData()
    private val _areCapsulesLoading: LiveData<Boolean> = repository.getCapsulesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    private val _sortingOrder = MutableLiveData<CapsulesSortingOrder>()

    init {
        _sortingOrder.value = CapsulesSortingOrder.BY_SERIAL_NEWEST

        uiScope.launch(Dispatchers.Default) {
            repository.getAllCapsulesFlow()
                .collect { capsules -> sortAndSetCapsules(capsules) }
        }
    }

    fun getCapsules(): LiveData<List<Capsule>> = allCapsules

    fun getCapsulesSortingOrder(): LiveData<CapsulesSortingOrder> = _sortingOrder

    fun setCapsulesSortingOrder(sortingOrder: CapsulesSortingOrder) {
        _sortingOrder.value = sortingOrder
        uiScope.launch(Dispatchers.Default) {
            if (allCapsules.value != null) sortAndSetCapsules(allCapsules.value!!)
        }
    }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = _areCapsulesLoading

    // Wrapper for refreshing capsules data
    fun refreshCapsules() = uiScope.launch { repository.refreshCapsules() }

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

    private fun sortAndSetCapsules(capsules: List<Capsule>) {
        allCapsules.postValue(when {
            _sortingOrder.value == CapsulesSortingOrder.BY_SERIAL_NEWEST -> {
                capsules.sortedByDescending { it._id }
            }
            _sortingOrder.value == CapsulesSortingOrder.BY_SERIAL_OLDEST -> {
                capsules.sortedBy { it._id }
            }
            else -> capsules.sortedByDescending { it._id }
        })
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }

    // Class representing all possible sort orders of capsules
    enum class CapsulesSortingOrder { BY_SERIAL_NEWEST, BY_SERIAL_OLDEST }
}
