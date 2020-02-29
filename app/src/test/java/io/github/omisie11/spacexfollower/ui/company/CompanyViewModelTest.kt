package io.github.omisie11.spacexfollower.ui.company

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.spacexfollower.data.local.model.Company
import io.github.omisie11.spacexfollower.data.repository.CompanyRepository
import io.github.omisie11.spacexfollower.test_utils.getValue
import io.github.omisie11.spacexfollower.test_utils.testCompanyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class CompanyViewModelTest {

    private val testCompanyData = testCompanyInfo

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var companyRepository: CompanyRepository

    // class under test
    private lateinit var companyViewModel: CompanyViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getCompanyInfoTest() {
        val companyData = MutableLiveData<Company>(testCompanyData)

        Mockito.`when`(companyRepository.getCompanyInfo()).thenAnswer {
            return@thenAnswer companyData
        }

        companyViewModel = CompanyViewModel(companyRepository)

        val result: Company = getValue(companyViewModel.getCompanyInfo())

        assertEquals(testCompanyData, result)
    }

    @Test
    fun refreshCompanyDataTest_verifyCalls() = runBlocking {
        val companyData = MutableLiveData<Company>(testCompanyData)

        Mockito.`when`(companyRepository.getCompanyInfo()).thenAnswer {
            return@thenAnswer companyData
        }

        companyViewModel = CompanyViewModel(companyRepository)

        companyViewModel.refreshCompanyInfo()

        verify(companyRepository, times(1)).refreshData(Mockito.eq(true))
    }

    @Test
    fun refreshIfCompanyDataOldTest_verifyCalls() = runBlocking {
        val companyData = MutableLiveData<Company>(testCompanyData)

        Mockito.`when`(companyRepository.getCompanyInfo()).thenAnswer {
            return@thenAnswer companyData
        }

        companyViewModel = CompanyViewModel(companyRepository)

        companyViewModel.refreshIfCompanyDataOld()

        verify(companyRepository, times(1)).refreshData(Mockito.eq(false))
    }

    @Test
    fun deleteCompanyInfosTest_verifyCalls() = runBlocking {
        val companyData = MutableLiveData<Company>(testCompanyData)

        Mockito.`when`(companyRepository.getCompanyInfo()).thenAnswer {
            return@thenAnswer companyData
        }

        companyViewModel = CompanyViewModel(companyRepository)

        companyViewModel.deleteCompanyInfo()

        verify(companyRepository, times(1)).deleteCompanyInfo()
    }
}