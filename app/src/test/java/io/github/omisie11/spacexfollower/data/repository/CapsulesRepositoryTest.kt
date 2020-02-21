package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.testCapsule1
import io.github.omisie11.spacexfollower.util.testCapsule2
import io.github.omisie11.spacexfollower.util.testCapsule3
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

@RunWith(JUnit4::class)
class CapsulesRepositoryTest {

    private val testCapsulesList = listOf(testCapsule2, testCapsule1, testCapsule3)

    @Mock
    private lateinit var spaceService: SpaceService
    @Mock
    private lateinit var capsulesDao: CapsulesDao
    @Mock
    private lateinit var sharedPrefs: SharedPreferences

    // Class under test
    private lateinit var capsulesRepository: CapsulesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        capsulesRepository = CapsulesRepository(capsulesDao, spaceService, sharedPrefs)
    }

    @Test
    fun getAllCapsulesFlowTest() = runBlocking {
        val companyLiveData = flowOf(testCapsulesList)

        Mockito.`when`(capsulesDao.getAllCapsulesFlow()).thenAnswer {
            return@thenAnswer companyLiveData
        }

        var result = listOf<Capsule>()
        capsulesRepository.getAllCapsulesFlow().collect {
            result = it
        }

        verify(capsulesDao, times(1)).getAllCapsulesFlow()
        assertEquals(result, testCapsulesList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseSuccess_VerifyDataInsertedOnce() = runBlocking {
        val responseSuccess: Response<List<Capsule>> = Response.success(testCapsulesList)

        Mockito.`when`(spaceService.getAllCapsules()).thenAnswer {
            return@thenAnswer responseSuccess
        }

        capsulesRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllCapsules()
        verify(capsulesDao, times(1)).replaceCapsulesData(testCapsulesList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseError_VerifyDataNotInserted() = runBlocking {
        val responseError: Response<List<Capsule>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "Bad Request"
            )
        )

        Mockito.`when`(spaceService.getAllCapsules()).thenAnswer {
            return@thenAnswer responseError
        }

        capsulesRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllCapsules()
        verify(capsulesDao, times(0)).replaceCapsulesData(testCapsulesList)
    }
}