package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.testCompanyInfo
import io.github.omisie11.spacexfollower.utilities.getValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var companyDao: CompanyDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room
            .inMemoryDatabaseBuilder(context, SpaceDatabase::class.java)
            .allowMainThreadQueries()
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
        companyDao = database.companyDao()

        companyDao.insertCompanyInfo(testCompanyInfo)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetCompanyInfo() {
        val companyData = getValue(companyDao.getCompanyInfo())
        assert(companyData == testCompanyInfo)
    }
}