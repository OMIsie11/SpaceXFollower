package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.util.testLaunchPad1
import io.github.omisie11.spacexfollower.util.testLaunchPad2
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
class LaunchPadsRepositoryTest {

    private val testLaunchPadsList = listOf(testLaunchPad1, testLaunchPad2)

    @Mock
    private lateinit var spaceService: SpaceService
    @Mock
    private lateinit var launchPadsDao: LaunchPadsDao
    @Mock
    private lateinit var sharedPrefs: SharedPreferences

    // Class under test
    private lateinit var launchPadsRepository: LaunchPadsRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        launchPadsRepository = LaunchPadsRepository(spaceService, launchPadsDao, sharedPrefs)
    }

    @Test
    fun getAllCapsulesFlowTest() = runBlocking {
        val launchPadsFlow = flowOf(testLaunchPadsList)

        Mockito.`when`(launchPadsDao.getLaunchPadsFlow()).thenAnswer {
            return@thenAnswer launchPadsFlow
        }

        var result = listOf<LaunchPad>()
        launchPadsRepository.getLaunchPadsFlow().collect {
            result = it
        }

        verify(launchPadsDao, times(1)).getLaunchPadsFlow()
        assertEquals(result, testLaunchPadsList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseSuccess_VerifyDataInsertedOnce() = runBlocking {
        val responseSuccess: Response<List<LaunchPad>> = Response.success(testLaunchPadsList)

        Mockito.`when`(spaceService.getLaunchPads()).thenAnswer {
            return@thenAnswer responseSuccess
        }

        launchPadsRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getLaunchPads()
        verify(launchPadsDao, times(1)).replaceLaunchPads(testLaunchPadsList)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseError_VerifyDataNotInserted() = runBlocking {
        val responseError: Response<List<LaunchPad>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "Bad Request"
            )
        )

        Mockito.`when`(spaceService.getLaunchPads()).thenAnswer {
            return@thenAnswer responseError
        }

        launchPadsRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getLaunchPads()
        verify(launchPadsDao, times(0)).replaceLaunchPads(testLaunchPadsList)
    }
}