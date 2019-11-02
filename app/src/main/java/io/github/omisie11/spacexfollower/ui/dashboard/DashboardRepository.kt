package io.github.omisie11.spacexfollower.ui.dashboard

import io.github.omisie11.spacexfollower.data.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import kotlinx.coroutines.flow.Flow

class DashboardRepository(
    private val allLaunchesDao: AllLaunchesDao,
    private val capsulesDao: CapsulesDao,
    private val coresDao: CoresDao
) {

    // ToDo: temporary hardcoded values
    private val startOf2019Timestamp: Long = 1546300801
    private val endOf2019Timestamp: Long = 1577836801

//    suspend fun getLaunchesInTimeIntervalFlow(): Flow<List<Launch>> =
//        allLaunchesDao.getLaunchesInTimeInterval(startOf2019Timestamp, endOf2019Timestamp)

    fun getNumberOfLaunchesFlow(): Flow<Int> = allLaunchesDao.getNumberOfLaunchesFlow()

    fun getNumberOfCapsulesFlow(): Flow<Int> = capsulesDao.getNumberOfCapsulesFlow()

    fun getNumberOfCoresFlow(): Flow<Int> = coresDao.getNumberOfCoresFlow()
}