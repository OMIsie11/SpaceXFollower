package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.CoresDao
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.test_utils.testCore1
import io.github.omisie11.spacexfollower.test_utils.testCore2
import io.github.omisie11.spacexfollower.test_utils.testCore3
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
class CoresRepositoryTest {

    private val testCoresList = listOf(
        testCore2,
        testCore1,
        testCore3
    )

    @Mock
    private lateinit var spaceService: SpaceService
    @Mock
    private lateinit var coresDao: CoresDao
    @Mock
    private lateinit var sharedPrefs: SharedPreferences

    // Class under test
    private lateinit var coresRepository: CoresRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        coresRepository = CoresRepository(spaceService, coresDao, sharedPrefs)
    }

    @Test
    fun getAllCoresFlowTest() = runBlocking {
        val coresFlow = flowOf(testCoresList)

        Mockito.`when`(coresDao.getAllCoresFlow()).thenAnswer {
            return@thenAnswer coresFlow
        }

        var result = listOf<Core>()
        coresRepository.getAllCoresFlow().collect {
            result = it
        }

        verify(coresDao, times(1)).getAllCoresFlow()
        assertEquals(result, testCoresList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseSuccess_VerifyDataInsertedOnce() = runBlocking {
        val responseSuccess: Response<List<Core>> = Response.success(testCoresList)

        Mockito.`when`(spaceService.getAllCores()).thenAnswer {
            return@thenAnswer responseSuccess
        }

        coresRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllCores()
        verify(coresDao, times(1)).replaceCoresData(testCoresList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseError_VerifyDataNotInserted() = runBlocking {
        val responseError: Response<List<Core>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "Bad Request"
            )
        )

        Mockito.`when`(spaceService.getAllCores()).thenAnswer {
            return@thenAnswer responseError
        }

        coresRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllCores()
        verify(coresDao, times(0)).replaceCoresData(testCoresList)
    }

    @Test
    fun deleteAllCoresTest_verifyCalls() = runBlocking {

        coresRepository.deleteAllCores()

        verify(coresDao, times(1)).deleteAllCores()
    }
}
