package io.github.omisie11.spacexfollower.data

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import io.github.omisie11.spacexfollower.data.local.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.local.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.local.dao.CoresDao
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
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

    fun getEntriesLaunchesStatsFlow(yearToShow: YearInterval): Flow<List<Entry>> =
        allLaunchesDao.getLaunchesBetweenDatesFlow(
            yearToShow.startUnix,
            yearToShow.endUnix
        ).map { launches -> mapLaunchesToEntriesForMonthsStats(launches) }

    fun getNumberOfLaunchesFlow(): Flow<Int> = allLaunchesDao.getNumberOfLaunchesFlow()

    fun getNumberOfCapsulesFlow(): Flow<Int> = capsulesDao.getNumberOfCapsulesFlow()

    fun getNumberOfCoresFlow(): Flow<Int> = coresDao.getNumberOfCoresFlow()

    fun getEntriesCapsulesStatusFlow(): Flow<List<PieEntry>> = capsulesDao.getAllCapsulesFlow()
        .map { capsules -> mapCapsulesToEntriesWithStatus(capsules) }

    fun getEntriesCoresStatusFlow(): Flow<List<PieEntry>> = coresDao.getAllCoresFlow()
        .map { cores -> mapCoresToStatusEntries(cores) }

    suspend fun refreshData() {
        launchesRepository.refreshLaunches()
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

    private fun mapCapsulesToEntriesWithStatus(capsules: List<Capsule>): List<PieEntry> {
        val statusesMap = mutableMapOf<String, Int>()
        capsules.forEach { capsule ->
            val status = capsule.status
            if (status.isNotBlank() || status != "null") {
                if (statusesMap.contains(status)) {
                    statusesMap[status] = statusesMap[status]!!.plus(1)
                } else statusesMap[status] = 1
            }
        }

        val entries = mutableListOf<PieEntry>()
        for (item in statusesMap) {
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }
        return entries
    }

    private fun mapCoresToStatusEntries(cores: List<Core>): List<PieEntry> {
        val statusesMap = mutableMapOf<String, Int>()
        cores.forEach { core ->
            val status = core.status
            if (status.isNotBlank() || status != "null") {
                if (statusesMap.contains(status)) {
                    statusesMap[status] = statusesMap[status]!!.plus(1)
                } else statusesMap[status] = 1
            }
        }

        val entries = mutableListOf<PieEntry>()
        statusesMap.forEach { entries.add(PieEntry(it.value.toFloat(), it.key)) }
        return entries
    }

    // Representation year, Pair.first is start of the year in Unix Time, second is the end date
    enum class YearInterval(val startUnix: Long, val endUnix: Long) {
        YEAR_2020(1577836800, 1609372800), YEAR_2019(1546300801, 1577836801),
        YEAR_2018(1514764800, 1546214400), YEAR_2017(1483228800, 1514678400),
        YEAR_2016(1451606400, 1483142400), YEAR_2015(1420070400, 1451520000),
        YEAR_2014(1388534400, 1419984000)
    }
}