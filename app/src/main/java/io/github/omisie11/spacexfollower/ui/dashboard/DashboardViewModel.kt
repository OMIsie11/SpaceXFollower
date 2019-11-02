package io.github.omisie11.spacexfollower.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel(dashboardRepository: DashboardRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val numberOfLaunches = MutableLiveData<Int>()
    private val numberOfCapsules = MutableLiveData<Int>()
    private val numberOfCores = MutableLiveData<Int>()

    init {
        uiScope.launch(Dispatchers.Default) {
            dashboardRepository.getNumberOfLaunchesFlow().collect { numberOfLaunchesInDb ->
                numberOfLaunches.postValue(numberOfLaunchesInDb)
            }
        }

        uiScope.launch(Dispatchers.Default) {
            dashboardRepository.getNumberOfCapsulesFlow().collect { numberOfCapsulesInDb ->
                numberOfCapsules.postValue(numberOfCapsulesInDb)
            }
        }
        uiScope.launch(Dispatchers.Default) {
            dashboardRepository.getNumberOfCoresFlow().collect { numberOfCoresInDb ->
                numberOfCores.postValue(numberOfCoresInDb)
            }
        }
    }

    fun getNumberOfLaunches(): LiveData<Int> = numberOfLaunches

    fun getNumberOfCapsules(): LiveData<Int> = numberOfCapsules

    fun getNumberOfCores(): LiveData<Int> = numberOfCores

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }
}