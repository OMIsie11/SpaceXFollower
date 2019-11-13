package io.github.omisie11.spacexfollower.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // List of entries for chart with stats of launches by months
    private val launchesStats = MutableLiveData<List<Entry>>()

    private val numberOfLaunches = MutableLiveData<Int>()
    private val numberOfCapsules = MutableLiveData<Int>()
    private val numberOfCores = MutableLiveData<Int>()

    init {
        uiScope.launch(Dispatchers.Default) {
            repository.getLaunchesInTimeIntervalFlow().collect {
                launchesStats.postValue(it)
            }
        }
        uiScope.launch(Dispatchers.Default) {
            repository.getNumberOfLaunchesFlow().collect { numberOfLaunchesInDb ->
                numberOfLaunches.postValue(numberOfLaunchesInDb)
            }
        }
        uiScope.launch(Dispatchers.Default) {
            repository.getNumberOfCapsulesFlow().collect { numberOfCapsulesInDb ->
                numberOfCapsules.postValue(numberOfCapsulesInDb)
            }
        }
        uiScope.launch(Dispatchers.Default) {
            repository.getNumberOfCoresFlow().collect { numberOfCoresInDb ->
                numberOfCores.postValue(numberOfCoresInDb)
            }
        }
    }

    fun getLaunchesStats(): LiveData<List<Entry>> = launchesStats

    fun getNumberOfLaunches(): LiveData<Int> = numberOfLaunches

    fun getNumberOfCapsules(): LiveData<Int> = numberOfCapsules

    fun getNumberOfCores(): LiveData<Int> = numberOfCores

    fun refreshData() = uiScope.launch { repository.refreshData() }

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }
}