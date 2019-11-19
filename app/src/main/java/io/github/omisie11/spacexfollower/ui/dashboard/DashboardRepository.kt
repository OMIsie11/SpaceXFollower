package io.github.omisie11.spacexfollower.ui.dashboard

import com.github.mikephil.charting.data.Entry
import io.github.omisie11.spacexfollower.data.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesRepository
import io.github.omisie11.spacexfollower.ui.cores.CoresRepository
import io.github.omisie11.spacexfollower.ui.launches.LaunchesRepository
import io.github.omisie11.spacexfollower.util.getMonthValueFromUnixTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DashboardRepository(
    private val allLaunchesDao: AllLaunchesDao,
    private val capsulesDao: CapsulesDao,
    private val coresDao: CoresDao,
    private val launchesRepository: LaunchesRepository,
    private val capsulesRepository: CapsulesRepository,
    private val coresRepository: CoresRepository
) {

    // ToDo: temporary hardcoded values for start and end of 2019, support more options in future
    private val startOf2019Timestamp: Long = 1546300801
    private val endOf2019Timestamp: Long = 1577836801

    fun getLaunchesInTimeIntervalFlow(): Flow<List<Entry>> =
        allLaunchesDao.getLaunchesBetweenDatesFlow(
            YearInterval.YEAR_2019.startUnix,
            YearInterval.YEAR_2019.endUnix
        )
            .map { launches ->
                mapLaunchesToEntriesForMonthsStats(launches)
            }

    fun getNumberOfLaunchesFlow(): Flow<Int> = allLaunchesDao.getNumberOfLaunchesFlow()

    fun getNumberOfCapsulesFlow(): Flow<Int> = capsulesDao.getNumberOfCapsulesFlow()

    fun getNumberOfCoresFlow(): Flow<Int> = coresDao.getNumberOfCoresFlow()

    suspend fun refreshData() {
        launchesRepository.refreshUpcomingLaunches()
        capsulesRepository.refreshCapsules()
        coresRepository.refreshCores()
    }

    suspend fun refreshIfDataIsOld() {
        launchesRepository.refreshIfLaunchesDataOld()
        capsulesRepository.refreshIfCapsulesDataOld()
        coresRepository.refreshIfCoresDataOld()
    }

    // Map list of launches to list of Entries that shows number of launches in particular months
    private fun mapLaunchesToEntriesForMonthsStats(launches: List<Launch>): List<Entry> {
        // Initialize list for entries (12 for each month of year)
        val entriesList = MutableList(12) { Entry(it.toFloat(), 0f) }
        launches.forEach { launch ->
            if (launch.launchDateUnix != null) {
                // Translate month id to index at entriesList
                val monthIndex = getMonthValueFromUnixTime(launch.launchDateUnix) - 1
                // Increment value of Entry corresponding with month of current launch
                entriesList[monthIndex] = Entry(
                    monthIndex.toFloat(),
                    entriesList[monthIndex].y + 1
                )
            }
        }
        return entriesList
    }

    // Representation year, Pair.first is start of the year in Unix Time, second is the end date
    enum class YearInterval(val startUnix: Long, val endUnix: Long) {
        YEAR_2019(1546300801, 1577836801), YEAR_2018(1514764800, 1546214400)
    }
}