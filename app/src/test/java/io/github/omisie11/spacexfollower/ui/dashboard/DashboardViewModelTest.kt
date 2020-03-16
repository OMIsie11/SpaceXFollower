package io.github.omisie11.spacexfollower.ui.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.repository.DashboardRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testCapsulesPieEntriesList
import io.github.omisie11.spacexfollower.test_utils.testCoresPieEntriesList
import io.github.omisie11.spacexfollower.test_utils.testLaunchEntriesList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DashboardViewModelTest {

    private val launchesYearToShow = DashboardRepository.YearInterval.YEAR_2020

    private val testLaunchesEntries: List<Entry> = testLaunchEntriesList
    private val testCapsulesPieEntries: List<PieEntry> = testCapsulesPieEntriesList
    private val testCoresPieEntries: List<PieEntry> = testCoresPieEntriesList

    private val testNumberOfLaunches = 5
    private val testNumberOfCapsules = 20
    private val testNumberOfCores = 16

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var dashboardRepository: DashboardRepository

    // class under test
    private lateinit var dashboardViewModel: DashboardViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    private fun getInitDataFromRepo() {
        Mockito.`when`(dashboardRepository.getEntriesLaunchesStatsFlow(launchesYearToShow))
            .thenAnswer {
                return@thenAnswer flowOf(testLaunchesEntries)
            }
        Mockito.`when`(dashboardRepository.getEntriesCapsulesStatusFlow()).thenAnswer {
            return@thenAnswer flowOf(testCapsulesPieEntries)
        }
        Mockito.`when`(dashboardRepository.getEntriesCoresStatusFlow()).thenAnswer {
            return@thenAnswer flowOf(testCoresPieEntries)
        }
        Mockito.`when`(dashboardRepository.getNumberOfLaunchesFlow()).thenAnswer {
            return@thenAnswer flowOf(testNumberOfLaunches)
        }
        Mockito.`when`(dashboardRepository.getNumberOfCapsulesFlow()).thenAnswer {
            return@thenAnswer flowOf(testNumberOfCapsules)
        }
        Mockito.`when`(dashboardRepository.getNumberOfCoresFlow()).thenAnswer {
            return@thenAnswer flowOf(testNumberOfCores)
        }
    }

    @Test
    fun refreshDataTest_verifyCalls() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }

        dashboardViewModel = DashboardViewModel(dashboardRepository)
        dashboardViewModel.refreshData()

        verify(dashboardRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfDataOldTest_verifyCalls() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }

        dashboardViewModel = DashboardViewModel(dashboardRepository)
        dashboardViewModel.refreshIfDataIsOld()

        verify(dashboardRepository, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun getNumberOfLaunchesTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result: Int = getValue(dashboardViewModel.getNumberOfLaunches())

        verify(dashboardRepository, times(1)).getNumberOfLaunchesFlow()
        assertEquals(testNumberOfLaunches, result)
    }

    @Test
    fun getNumberOfCapsulesTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result: Int = getValue(dashboardViewModel.getNumberOfCapsules())

        verify(dashboardRepository, times(1)).getNumberOfCapsulesFlow()
        assertEquals(testNumberOfCapsules, result)
    }

    @Test
    fun getNumberOfCoresTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result: Int = getValue(dashboardViewModel.getNumberOfCores())

        verify(dashboardRepository, times(1)).getNumberOfCoresFlow()
        assertEquals(testNumberOfCores, result)
    }

    @Test
    fun getLaunchesStatsTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result = getValue(dashboardViewModel.getLaunchesStats())

        assertEquals(testLaunchesEntries, result)
    }

    @Test
    fun getCapsulesStatusStatsTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result = getValue(dashboardViewModel.getCapsulesStatusStats())

        assertEquals(testCapsulesPieEntries, result)
    }

    @Test
    fun getCoresStatusStatsTest() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result = getValue(dashboardViewModel.getCoresStatusStats())

        assertEquals(testCoresPieEntries, result)
    }

    @Test
    fun getLaunchesChartYearTest_initialValue() = runBlocking {
        withContext(Dispatchers.IO) {
            getInitDataFromRepo()
        }
        dashboardViewModel = DashboardViewModel(dashboardRepository)

        val result = getValue(dashboardViewModel.getLaunchesChartYear())

        assertEquals(launchesYearToShow, result)
    }
}
