package io.github.omisie11.spacexfollower.ui.cores

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.repository.CoresRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testCore1
import io.github.omisie11.spacexfollower.test_utils.testCore2
import io.github.omisie11.spacexfollower.test_utils.testCore3
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
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CoresViewModelTest {

    private val testCoresList = listOf(
        testCore2,
        testCore1,
        testCore3
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var coresRepository: CoresRepository

    // class under test
    private lateinit var coresViewModel: CoresViewModel

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
    fun getCoresTest() {
        val coresFlow = flowOf(testCoresList)
        Mockito.`when`(coresRepository.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        coresViewModel = CoresViewModel(coresRepository)

        val result: List<Core>? = getValue(coresViewModel.getCores())

        assertEquals(testCoresList.sortedByDescending { it._id }, result)
    }

    @Test
    fun refreshCoresTest_verifyCalls() = runBlockingTest {
        val coresFlow = flowOf(testCoresList)
        Mockito.`when`(coresRepository.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        coresViewModel = CoresViewModel(coresRepository)

        coresViewModel.refreshCores()

        verify(coresRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfCoresDataOldTest_verifyCalls() = runBlockingTest {
        val coresFlow = flowOf(testCoresList)
        Mockito.`when`(coresRepository.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        coresViewModel = CoresViewModel(coresRepository)

        coresViewModel.refreshIfCoresDataOld()

        verify(coresRepository, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun getCoresSortingOrderTest() = runBlockingTest {
        val coresFlow = flowOf(testCoresList)
        Mockito.`when`(coresRepository.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        coresViewModel = CoresViewModel(coresRepository)

        CoresViewModel.CoresSortingOrder.values().forEach { sortingOrder ->
            coresViewModel.setCoresSortingOrder(sortingOrder)

            val result = getValue(coresViewModel.getCoresSortingOrder())

            assertEquals(sortingOrder, result)
        }
    }

    @Test
    fun deleteCoresTest_verifyCalls() = runBlockingTest {
        val coresFlow = flowOf(testCoresList)
        Mockito.`when`(coresRepository.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        coresViewModel = CoresViewModel(coresRepository)

        coresViewModel.deleteCoresData()

        verify(coresRepository, times(1)).deleteAllCores()
    }
}