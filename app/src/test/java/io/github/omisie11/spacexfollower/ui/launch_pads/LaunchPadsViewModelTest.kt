package io.github.omisie11.spacexfollower.ui.launch_pads

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import io.github.omisie11.spacexfollower.data.repository.LaunchPadsRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testLaunchPad1
import io.github.omisie11.spacexfollower.test_utils.testLaunchPad2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
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
class LaunchPadsViewModelTest {

    private val testLaunchPadsList = listOf(testLaunchPad1, testLaunchPad2)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var launchPadsRepository: LaunchPadsRepository

    // class under test
    private lateinit var launchPadsViewModel: LaunchPadsViewModel

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
    fun getLaunchPadsTest() {
        val launchPadsFlow = flowOf(testLaunchPadsList)
        Mockito.`when`(launchPadsRepository.getLaunchPadsFlow()).thenAnswer {
            return@thenAnswer launchPadsFlow
        }

        launchPadsViewModel = LaunchPadsViewModel(launchPadsRepository)

        val result: List<LaunchPad> = getValue(launchPadsViewModel.getLaunchPads())

        assertEquals(testLaunchPadsList, result)
    }

    @Test
    fun refreshLaunchPadsTest_verifyCalls() = runBlockingTest {
        val launchPadsFlow = flowOf(testLaunchPadsList)
        Mockito.`when`(launchPadsRepository.getLaunchPadsFlow()).thenAnswer {
            return@thenAnswer launchPadsFlow
        }

        launchPadsViewModel = LaunchPadsViewModel(launchPadsRepository)

        launchPadsViewModel.refreshLaunchPads()

        verify(launchPadsRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfLaunchPadsDataOldTest_verifyCalls() = runBlockingTest {
        val launchPadsFlow = flowOf(testLaunchPadsList)
        Mockito.`when`(launchPadsRepository.getLaunchPadsFlow()).thenAnswer {
            return@thenAnswer launchPadsFlow
        }

        launchPadsViewModel = LaunchPadsViewModel(launchPadsRepository)

        launchPadsViewModel.refreshIfLaunchPadsDataOld()

        verify(launchPadsRepository, times(1)).refreshData(Mockito.eq(false))
    }
}
