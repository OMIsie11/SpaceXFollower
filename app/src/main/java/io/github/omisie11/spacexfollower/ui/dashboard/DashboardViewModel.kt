package io.github.omisie11.spacexfollower.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import io.github.omisie11.spacexfollower.data.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    // List of entries for chart with stats of launches by months
    private val launchesStats = MutableLiveData<List<Entry>>()
    private var launchesChartYear = MutableLiveData<DashboardRepository.YearInterval>()

    private val numberOfLaunches = MutableLiveData<Int>()
    private val numberOfCapsules = MutableLiveData<Int>()
    private val numberOfCores = MutableLiveData<Int>()

    private val capsulesStatusStats = MutableLiveData<List<PieEntry>>()
    private val coresStatusStats = MutableLiveData<List<PieEntry>>()

    init {
        launchesChartYear.value = DashboardRepository.YearInterval.YEAR_2020

        viewModelScope.launch(Dispatchers.IO) { fetchLaunchesStatsFromDb() }

        viewModelScope.launch(Dispatchers.IO) { fetchCapsulesStatusStatsFromDb() }

        viewModelScope.launch(Dispatchers.IO) { fetchCoresStatusStatsFromDb() }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getNumberOfLaunchesFlow().collect { numberOfLaunchesInDb ->
                numberOfLaunches.postValue(numberOfLaunchesInDb)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNumberOfCapsulesFlow().collect { numberOfCapsulesInDb ->
                numberOfCapsules.postValue(numberOfCapsulesInDb)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
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

    fun getCoresStatusStats(): LiveData<List<PieEntry>> = coresStatusStats

    fun getLaunchesChartYear(): LiveData<DashboardRepository.YearInterval> = launchesChartYear

    fun setLaunchesChartYear(yearToShowInChart: DashboardRepository.YearInterval) {
        launchesChartYear.value = yearToShowInChart
        viewModelScope.launch(Dispatchers.IO) {
            fetchLaunchesStatsFromDb()
        }
    }

    fun refreshData() = viewModelScope.launch { repository.refreshData() }

    fun refreshIfDataIsOld() =
        viewModelScope.launch { repository.refreshIfDataIsOld() }

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

    private suspend fun fetchCoresStatusStatsFromDb() {
        repository.getEntriesCoresStatusFlow().collect {
            coresStatusStats.postValue(it)
        }
    }
}