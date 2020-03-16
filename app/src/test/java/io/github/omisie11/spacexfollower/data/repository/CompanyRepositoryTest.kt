package io.github.omisie11.spacexfollower.data.repository

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.local.model.Company
import io.github.omisie11.spacexfollower.data.remote.SpaceService
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testCompanyInfo
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

@RunWith(JUnit4::class)
class CompanyRepositoryTest {

    private val testCompanyData = testCompanyInfo

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var spaceService: SpaceService
    @Mock
    private lateinit var companyDao: CompanyDao
    @Mock
    private lateinit var sharedPrefs: SharedPreferences

    // Class under test
    private lateinit var companyRepository: CompanyRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        companyRepository = CompanyRepository(spaceService, companyDao, sharedPrefs)
    }

    @Test
    fun getCompanyInfoTest() {
        val companyLiveData = MutableLiveData<Company>().also {
            it.value = testCompanyData
        }

        Mockito.`when`(companyDao.getCompanyInfo()).thenAnswer {
            return@thenAnswer companyLiveData
        }

        val result = getValue(
            companyRepository.getCompanyInfo()
        )

        assertEquals(result, testCompanyData)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseSuccess_VerifyDataInsertedOnce() = runBlocking {
        val responseSuccess: Response<Company> = Response.success(testCompanyData)

        Mockito.`when`(spaceService.getCompanyInfo()).thenAnswer {
            return@thenAnswer responseSuccess
        }

        companyRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getCompanyInfo()
        verify(companyDao, times(1)).insertCompanyInfo(testCompanyData)
    }

    @Test
    fun refreshDataTest_Force_ApiResponseError_VerifyDataNotInserted() = runBlocking {
        val responseError: Response<Company> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "Bad Request"
            )
        )

        Mockito.`when`(spaceService.getCompanyInfo()).thenAnswer {
            return@thenAnswer responseError
        }

        companyRepository.refreshData(forceRefresh = true)

        verify(spaceService, times(1)).getCompanyInfo()
        verify(companyDao, times(0)).insertCompanyInfo(testCompanyData)
    }

    @Test
    fun deleteCompanyInfoTest_verifyCalls() = runBlocking {

        companyRepository.deleteCompanyInfo()

        verify(companyDao, times(1)).deleteCompanyInfo()
    }
}
