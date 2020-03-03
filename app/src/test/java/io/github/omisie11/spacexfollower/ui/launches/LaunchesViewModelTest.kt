package io.github.omisie11.spacexfollower.ui.launches

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.data.repository.LaunchesRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testLaunch1
import io.github.omisie11.spacexfollower.test_utils.testLaunch2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
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
class LaunchesViewModelTest {

    private val testLaunchesList = listOf(testLaunch2, testLaunch1)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var launchesRepository: LaunchesRepository

    // class under test
    private lateinit var launchesViewModel: LaunchesViewModel

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

    @Test
    fun getLaunchesTest() {
        val launchesFlow = flowOf(testLaunchesList)
        Mockito.`when`(launchesRepository.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        launchesViewModel = LaunchesViewModel(launchesRepository)

        val result: List<Launch> = getValue(launchesViewModel.getAllLaunches())

        assertEquals(testLaunchesList.sortedBy { it.flightNumber }, result)
    }

    @Test
    fun refreshLaunchesTest_verifyCalls() = runBlockingTest {
        val launchesFlow = flowOf(testLaunchesList)
        Mockito.`when`(launchesRepository.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        launchesViewModel = LaunchesViewModel(launchesRepository)

        launchesViewModel.refreshAllLaunches()

        verify(launchesRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfLaunchesDataOldTest_verifyCalls() = runBlockingTest {
        val launchesFlow = flowOf(testLaunchesList)
        Mockito.`when`(launchesRepository.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        launchesViewModel = LaunchesViewModel(launchesRepository)

        launchesViewModel.refreshIfLaunchesDataOld()

        verify(launchesRepository, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun getLaunchesSortingOrderTest() = runBlockingTest {
        val launchesFlow = flowOf(testLaunchesList)
        Mockito.`when`(launchesRepository.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        launchesViewModel = LaunchesViewModel(launchesRepository)

        LaunchesViewModel.LaunchesSortingOrder.values().forEach { sortingOrder ->
            launchesViewModel.setLaunchesSortingOrder(sortingOrder)

            val result = getValue(launchesViewModel.getLaunchesSortingOrder())

            assertEquals(sortingOrder, result)
        }
    }

    @Test
    fun deleteLaunchesTest_verifyCalls() = runBlockingTest {
        val launchesFlow = flowOf(testLaunchesList)
        Mockito.`when`(launchesRepository.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        launchesViewModel = LaunchesViewModel(launchesRepository)

        launchesViewModel.deleteLaunchesData()

        verify(launchesRepository, times(1)).deleteAllLaunches()
    }
}