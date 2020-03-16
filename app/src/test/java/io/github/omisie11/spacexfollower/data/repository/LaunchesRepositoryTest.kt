package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.test_utils.testLaunch1
import io.github.omisie11.spacexfollower.test_utils.testLaunch2
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
class LaunchesRepositoryTest {

    private val testLaunchesList = listOf(
        testLaunch1,
        testLaunch2
    )

    @Mock
    private lateinit var spaceService: SpaceService
    @Mock
    private lateinit var launchesDao: AllLaunchesDao
    @Mock
    private lateinit var sharedPrefs: SharedPreferences

    // Class under test
    private lateinit var launchesRepository: LaunchesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        launchesRepository = LaunchesRepository(launchesDao, spaceService, sharedPrefs)
    }

    @Test
    fun getAllLaunchesFlowTest() = runBlocking {
        val launchesFlow = flowOf(testLaunchesList)

        Mockito.`when`(launchesDao.getAllLaunchesFlow()).thenAnswer {
            return@thenAnswer launchesFlow
        }

        var result = listOf<Launch>()
        launchesRepository.getAllLaunchesFlow().collect {
            result = it
        }

        verify(launchesDao, times(1)).getAllLaunchesFlow()
        assertEquals(result, testLaunchesList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseSuccess_VerifyDataInsertedOnce() = runBlocking {
        val responseSuccess: Response<List<Launch>> = Response.success(testLaunchesList)

        Mockito.`when`(spaceService.getAllLaunches()).thenAnswer {
            return@thenAnswer responseSuccess
        }

        launchesRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllLaunches()
        verify(launchesDao, times(1)).replaceAllLaunches(testLaunchesList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseError_VerifyDataNotInserted() = runBlocking {
        val responseError: Response<List<Launch>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "Bad Request"
            )
        )

        Mockito.`when`(spaceService.getAllLaunches()).thenAnswer {
            return@thenAnswer responseError
        }

        launchesRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getAllLaunches()
        verify(launchesDao, times(0)).replaceAllLaunches(testLaunchesList)
    }

    @Test
    fun deleteAllLaunchesTest_verifyCalls() = runBlocking {

        launchesRepository.deleteAllLaunches()

        verify(launchesDao, times(1)).deleteLaunchesData()
    }
}
