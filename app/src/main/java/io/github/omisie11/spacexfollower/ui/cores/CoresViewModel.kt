package io.github.omisie11.spacexfollower.ui.cores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.repository.CoresRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CoresViewModel(private val repository: CoresRepository) : ViewModel() {

    private val allCores: MutableLiveData<List<Core>> = MutableLiveData()
    private val _areCoresLoading: LiveData<Boolean> by lazy { repository.getCoresLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCoresSnackbar()

    private val _sortingOrder =
        MutableLiveData<CoresSortingOrder>(CoresSortingOrder.BY_SERIAL_NEWEST)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllCoresFlow()
                .collect { cores -> sortAndSetCores(cores) }
        }
    }

    fun getCores(): LiveData<List<Core>> = allCores

    fun getCoresSortingOrder(): LiveData<CoresSortingOrder> = _sortingOrder

    fun setCoresSortingOrder(sortingOrder: CoresSortingOrder) {
        _sortingOrder.value = sortingOrder
        viewModelScope.launch(Dispatchers.IO) {
            if (allCores.value != null) sortAndSetCores(allCores.value!!)
        }
    }

    fun getCoresLoadingStatus(): LiveData<Boolean> = _areCoresLoading

    // Wrapper for refreshing cores data
    fun refreshCores() = viewModelScope.launch { repository.refreshData(forceRefresh = true) }

    // Wrapper for refreshing old data in onResume
    fun refreshIfCoresDataOld() =
        viewModelScope.launch { repository.refreshData(forceRefresh = false) }

    fun deleteCoresData() = viewModelScope.launch { repository.deleteAllCores() }

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
        allCores.postValue(when (_sortingOrder.value) {
            CoresSortingOrder.BY_SERIAL_NEWEST -> {
                cores.sortedByDescending { it._id }
            }
            CoresSortingOrder.BY_SERIAL_OLDEST -> {
                cores.sortedBy { it._id }
            }
            CoresSortingOrder.BY_BLOCK_ASCENDING -> {
                cores.sortedBy { it.block }
            }
            CoresSortingOrder.BY_BLOCK_DESCENDING -> {
                cores.sortedByDescending { it.block }
            }
            CoresSortingOrder.BY_STATUS_ACTIVE_FIRST -> {
                cores.sortedBy { it.status }
            }
            CoresSortingOrder.BY_STATUS_ACTIVE_LAST -> {
                cores.sortedByDescending { it.status }
            }
            else -> cores.sortedByDescending { it._id }
        })
    }

    enum class CoresSortingOrder {
        BY_SERIAL_NEWEST, BY_SERIAL_OLDEST, BY_BLOCK_ASCENDING, BY_BLOCK_DESCENDING,
        BY_STATUS_ACTIVE_FIRST, BY_STATUS_ACTIVE_LAST
    }
}
