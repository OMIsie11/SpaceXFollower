package io.github.omisie11.spacexfollower.ui.cores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class CoresViewModel(private val repository: CoresRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val allCores: MutableLiveData<List<Core>> = MutableLiveData()
    private val _areCoresLoading: LiveData<Boolean> by lazy { repository.getCoresLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCoresSnackbar()

    private val _sortingOrder = MutableLiveData<CoresSortingOrder>()

    init {
        _sortingOrder.value = CoresSortingOrder.BY_SERIAL_NEWEST

        uiScope.launch(Dispatchers.Default) {
            repository.getAllCoresFlow()
                .collect { cores -> sortAndSetCores(cores) }
        }
    }

    fun getCores(): LiveData<List<Core>> = allCores

    fun getCoresSortingOrder(): LiveData<CoresSortingOrder> = _sortingOrder

    fun setCoresSortingOrder(sortingOrder: CoresSortingOrder) {
        _sortingOrder.value = sortingOrder
        uiScope.launch(Dispatchers.Default) {
            if (allCores.value != null) sortAndSetCores(allCores.value!!)
        }
    }

    fun getCoresLoadingStatus(): LiveData<Boolean> = _areCoresLoading

    // Wrapper for refreshing cores data
    fun refreshCores() = uiScope.launch { repository.refreshCores() }

    // Wrapper for refreshing old data in onResume
    fun refreshIfCoresDataOld() = uiScope.launch { repository.refreshIfCoresDataOld() }

    fun deleteCoresData() = uiScope.launch { repository.deleteAllCores() }

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

    private fun sortAndSetCores(cores: List<Core>) {
        allCores.postValue(when {
            _sortingOrder.value == CoresSortingOrder.BY_SERIAL_NEWEST -> {
                cores.sortedByDescending { it._id }
            }
            _sortingOrder.value == CoresSortingOrder.BY_SERIAL_OLDEST -> {
                cores.sortedBy { it._id }
            }
            else -> cores.sortedByDescending { it._id }
        })
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }

    enum class CoresSortingOrder { BY_SERIAL_NEWEST, BY_SERIAL_OLDEST }
}