package io.github.omisie11.spacexfollower.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // List of entries for chart with stats of launches by months
    private val launchesStats = MutableLiveData<List<Entry>>()
    private var launchesChartYear = MutableLiveData<DashboardRepository.YearInterval>()

    private val numberOfLaunches = MutableLiveData<Int>()
    private val numberOfCapsules = MutableLiveData<Int>()
    private val numberOfCores = MutableLiveData<Int>()

    private val capsulesStatusStats = MutableLiveData<List<PieEntry>>()

    init {
        launchesChartYear.value = DashboardRepository.YearInterval.YEAR_2019

        uiScope.launch(Dispatchers.Default) { fetchLaunchesStatsFromDb() }

        uiScope.launch(Dispatchers.Default) { fetchCapsulesStatusStatsFromDb() }

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

    fun getCapsulesStatusStats(): LiveData<List<PieEntry>> = capsulesStatusStats

    fun getLaunchesChartYear(): LiveData<DashboardRepository.YearInterval> = launchesChartYear

    fun setLaunchesChartYear(yearToShowInChart: DashboardRepository.YearInterval) {
        launchesChartYear.value = yearToShowInChart
        uiScope.launch(Dispatchers.Default) {
            fetchLaunchesStatsFromDb()
        }
    }

    fun refreshData() = uiScope.launch { repository.refreshData() }

    fun refreshIfDataIsOld() = uiScope.launch { repository.refreshIfDataIsOld() }

    private suspend fun fetchLaunchesStatsFromDb() {
        repository.getEntriesLaunchesStatsFlow(
            launchesChartYear.value ?: DashboardRepository.YearInterval.YEAR_2019
        ).collect {
            Timber.d("launches: $it")
            launchesStats.postValue(it)
        }
    }

    private suspend fun fetchCapsulesStatusStatsFromDb() {
        repository.getEntriesCapsulesStatusFlow().collect {
            capsulesStatusStats.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel running coroutines in repository
        viewModelJob.cancel()
    }
}