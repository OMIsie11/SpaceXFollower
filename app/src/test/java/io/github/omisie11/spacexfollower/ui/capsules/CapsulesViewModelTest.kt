package io.github.omisie11.spacexfollower.ui.capsules

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.repository.CapsulesRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testCapsule1
import io.github.omisie11.spacexfollower.test_utils.testCapsule2
import io.github.omisie11.spacexfollower.test_utils.testCapsule3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
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
class CapsulesViewModelTest {

    private val testCapsulesList = listOf(
        testCapsule2,
        testCapsule1,
        testCapsule3
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var capsulesRepository: CapsulesRepository

    // class under test
    private lateinit var capsulesViewModel: CapsulesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getCapsulesTest() {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        val result: List<Capsule>? = getValue(capsulesViewModel.getCapsules())

        assertEquals(testCapsulesList.sortedByDescending { it._id }, result)
    }

    @Test
    fun getCapsulesTest_checkSortingSerialNewest() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_NEWEST
        )
        val result: List<Capsule>? = getValue(capsulesViewModel.getCapsules())

        assertEquals(testCapsulesList.sortedByDescending { it._id }, result)
    }

    @Test
    fun getCapsulesTest_checkSortingBySerialOldest() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_OLDEST
        )

        val result: List<Capsule>? = getValue(capsulesViewModel.getCapsules())

        assertEquals(testCapsulesList.sortedBy { it._id }, result)
    }

    @Test
    fun getCapsulesTest_checkSortingByStatusActiveFirst() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_FIRST
        )

        val result: List<Capsule>? = getValue(capsulesViewModel.getCapsules())

        assertEquals(testCapsulesList.sortedBy { it.status }, result)
    }

    @Test
    fun getCapsulesTest_checkSortingByStatusActiveLast() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_LAST
        )

        val result: List<Capsule>? = getValue(capsulesViewModel.getCapsules())

        assertEquals(testCapsulesList.sortedByDescending { it.status }, result)
    }

    @Test
    fun refreshCapsulesTest_verifyCalls() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.refreshCapsules()

        verify(capsulesRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfCapsulesDataOldTest_verifyCalls() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        capsulesViewModel.refreshIfCapsulesDataOld()

        verify(capsulesRepository, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun getCapsulesSortingOrderTest() = runBlocking {
        val capsulesFlow = flowOf(testCapsulesList)
        Mockito.`when`(capsulesRepository.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer capsulesFlow
        }

        capsulesViewModel = CapsulesViewModel(capsulesRepository)

        // Check sorting order BY_STATUS_ACTIVE_LAST
        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_LAST
        )
        val result = getValue(capsulesViewModel.getCapsulesSortingOrder())

        assertEquals(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_LAST,
            result
        )

        // Check sorting order BY_STATUS_ACTIVE_FIRST
        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_FIRST
        )
        val result2 = getValue(capsulesViewModel.getCapsulesSortingOrder())

        assertEquals(
            CapsulesViewModel.CapsulesSortingOrder.BY_STATUS_ACTIVE_FIRST,
            result2
        )

        // Check sorting order BY_SERIAL_NEWEST
        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_NEWEST
        )
        val result3 = getValue(capsulesViewModel.getCapsulesSortingOrder())

        assertEquals(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_NEWEST,
            result3
        )

        // Check sorting order BY_SERIAL_OLDEST
        capsulesViewModel.setCapsulesSortingOrder(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_OLDEST
        )
        val result4 = getValue(capsulesViewModel.getCapsulesSortingOrder())

        assertEquals(
            CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_OLDEST,
            result4
        )
    }
}