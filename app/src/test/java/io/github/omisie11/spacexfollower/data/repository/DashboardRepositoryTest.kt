package io.github.omisie11.spacexfollower.data.repository

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.local.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.local.dao.CoresDao
import io.github.omisie11.spacexfollower.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class DashboardRepositoryTest {

    @Mock
    private lateinit var launchesDao: AllLaunchesDao
    @Mock
    private lateinit var capsulesDao: CapsulesDao
    @Mock
    private lateinit var coresDao: CoresDao
    @Mock
    private lateinit var launchesRepo: LaunchesRepository
    @Mock
    private lateinit var capsulesRepo: CapsulesRepository
    @Mock
    private lateinit var coresRepo: CoresRepository

    // Class under test
    private lateinit var dashboardRepository: DashboardRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        dashboardRepository = DashboardRepository(
            launchesDao,
            capsulesDao,
            coresDao,
            launchesRepo,
            capsulesRepo,
            coresRepo
        )
    }

    @Test
    fun refreshDataTest_forceRefresh_verifyMethodsCallsAndArgs() = runBlocking {

        dashboardRepository.refreshData(forceRefresh = true)

        verify(launchesRepo, times(1)).refreshData(Mockito.eq(true))
        verify(capsulesRepo, times(1)).refreshData(Mockito.eq(true))
        verify(coresRepo, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshDataTest_notForceRefresh_verifyMethodsCallsAndArgs() = runBlocking {

        dashboardRepository.refreshData(forceRefresh = false)

        verify(launchesRepo, times(1)).refreshData(Mockito.eq(false))
        verify(capsulesRepo, times(1)).refreshData(Mockito.eq(false))
        verify(coresRepo, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun getNumberOfLaunchesFlowTest_VerifyCallsAndGettingData() = runBlocking {
        val testLaunchesList = listOf(testLaunch1, testLaunch2)
        val numberOfLaunchesFlow = flowOf(testLaunchesList.size)

        Mockito.`when`(launchesDao.getNumberOfLaunchesFlow()).thenAnswer {
            return@thenAnswer numberOfLaunchesFlow
        }

        var result = 0
        dashboardRepository.getNumberOfLaunchesFlow().collect {
            result = it
        }

        verify(launchesDao, times(1)).getNumberOfLaunchesFlow()
        assertEquals(result, testLaunchesList.size)
    }

    @Test
    fun getNumberOfCapsulesFlowTest_VerifyCallsAndGettingData() = runBlocking {
        val testCapsulesList = listOf(testCapsule1, testCapsule2, testCapsule3)
        val numberOfCapsulesFlow = flowOf(testCapsulesList.size)

        Mockito.`when`(capsulesDao.getNumberOfCapsulesFlow()).thenAnswer {
            return@thenAnswer numberOfCapsulesFlow
        }

        var result = 0
        dashboardRepository.getNumberOfCapsulesFlow().collect {
            result = it
        }

        verify(capsulesDao, times(1)).getNumberOfCapsulesFlow()
        assertEquals(result, testCapsulesList.size)
    }

    @Test
    fun getNumberOfCoresFlowTest_VerifyCallsAndGettingData() = runBlocking {
        val testCoresList = listOf(testCore2, testCore1, testCore3)
        val numberOfCoresFlow = flowOf(testCoresList.size)

        Mockito.`when`(coresDao.getNumberOfCoresFlow()).thenAnswer {
            return@thenAnswer numberOfCoresFlow
        }

        var result = 0
        dashboardRepository.getNumberOfCoresFlow().collect {
            result = it
        }

        verify(coresDao, times(1)).getNumberOfCoresFlow()
        assertEquals(result, testCoresList.size)
    }

    @Test
    fun getEntriesLaunchesStatsFlowTest() = runBlocking {
        val testLaunchesList = listOf(testLaunch1, testLaunch2)
        val launchesFlow = flowOf(testLaunchesList)
        val year2019 = DashboardRepository.YearInterval.YEAR_2019

        val entriesList: List<Entry> = testLaunchEntriesList

        Mockito.`when`(
            launchesDao.getLaunchesBetweenDatesFlow(
                year2019.startUnix,
                year2019.endUnix
            )
        ).thenAnswer {
            return@thenAnswer launchesFlow
        }

        var result = listOf<Entry>()
        dashboardRepository.getEntriesLaunchesStatsFlow(year2019).collect {
            result = it
        }

        verify(launchesDao, times(1)).getLaunchesBetweenDatesFlow(
            year2019.startUnix,
            year2019.endUnix
        )

        assertEquals(result.size, entriesList.size)
        assertEquals(result.toString(), entriesList.toString())
    }

    @Test
    fun getEntriesCapsulesStatsFlowTest() = runBlocking {
        val testCapsulesList = listOf(testCapsule2, testCapsule3, testCapsule1)
        val capsulesFlow = flowOf(testCapsulesList)

        val entriesList: List<PieEntry> = testCapsulesPieEntriesList

        Mockito.`when`(
            capsulesDao.getAllCapsulesFlow()
        ).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        var result = listOf<PieEntry>()
        dashboardRepository.getEntriesCapsulesStatusFlow().collect {
            result = it
        }

        verify(capsulesDao, times(1)).getAllCapsulesFlow()

        assertEquals(result.size, entriesList.size)
        assertEquals(result.toString(), entriesList.toString())
    }

    @Test
    fun getEntriesCoresStatsFlowTest() = runBlocking {
        val testCoresList = listOf(testCore2, testCore3, testCore1)
        val coresFlow = flowOf(testCoresList)

        val entriesList: List<PieEntry> = testCoresPieEntriesList

        Mockito.`when`(
            coresDao.getAllCoresFlow()
        ).thenAnswer {
            return@thenAnswer coresFlow
        }

        var result = listOf<PieEntry>()
        dashboardRepository.getEntriesCoresStatusFlow().collect {
            result = it
        }

        verify(coresDao, times(1)).getAllCoresFlow()

        assertEquals(result.size, entriesList.size)
        assertEquals(result.toString(), entriesList.toString())
    }
}