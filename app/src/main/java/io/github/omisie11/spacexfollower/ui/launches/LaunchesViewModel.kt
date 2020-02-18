package io.github.omisie11.spacexfollower.ui.launches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.omisie11.spacexfollower.data.repository.LaunchesRepository
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LaunchesViewModel(
    private val repository: LaunchesRepository
) : ViewModel() {

    private val allLaunches: MutableLiveData<List<Launch>> = MutableLiveData()
    private val _areLaunchesLoading: LiveData<Boolean> = repository.getLaunchesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getLaunchesSnackbar()

    private val _sortingOrder = MutableLiveData<LaunchesSortingOrder>()

    init {
        _sortingOrder.value = LaunchesSortingOrder.BY_FLIGHT_NUMBER_OLDEST

        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllLaunchesFlow()
                .collect { launches -> sortAndSetLaunches(launches) }
        }
    }

    fun getAllLaunches(): LiveData<List<Launch>> = allLaunches

    fun getLaunchesSortingOrder(): LiveData<LaunchesSortingOrder> = _sortingOrder

    fun setLaunchesSortingOrder(sortingOrder: LaunchesSortingOrder) {
        _sortingOrder.value = sortingOrder
        viewModelScope.launch(Dispatchers.IO) {
            if (allLaunches.value != null) sortAndSetLaunches(allLaunches.value!!)
        }
    }

    fun getLaunchesLoadingStatus(): LiveData<Boolean> = _areLaunchesLoading

    // Wrapper for refreshing launches data
    fun refreshAllLaunches() = viewModelScope.launch { repository.refreshData(forceRefresh = true) }

    // Wrapper for refreshing old data in onResume
    fun refreshIfLaunchesDataOld() =
        viewModelScope.launch { repository.refreshData(forceRefresh = false) }

    fun deleteLaunchesData() = viewModelScope.launch { repository.deleteAllLaunches() }

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

    private fun sortAndSetLaunches(launches: List<Launch>) {
        allLaunches.postValue(when (_sortingOrder.value) {
            LaunchesSortingOrder.BY_FLIGHT_NUMBER_NEWEST -> {
                launches.sortedByDescending { it.flightNumber }
            }
            LaunchesSortingOrder.BY_FLIGHT_NUMBER_OLDEST -> {
                launches.sortedBy { it.flightNumber }
            }
            else -> launches.sortedBy { it.flightNumber }
        })
    }

    enum class LaunchesSortingOrder { BY_FLIGHT_NUMBER_NEWEST, BY_FLIGHT_NUMBER_OLDEST }
}