package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.utilities.getValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest {

    private val companyInfo = Company(
        1, "SpaceX", "Elon Musk", 2002, 7000, 3,
        3, 1, "Elon Musk", "Elon Musk", "Gwynne Shotwell",
        "Tom Mueller", 15000000000, Company.Headquarters(
            "Rocket Road", "Hawthorne",
            "California"
        ), "SpaceX designs, manufactures and launches advanced rockets and " +
                "spacecraft. The company was founded in 2002 to revolutionize space technology, with the ultimate " +
                "goal of enabling people to live on other planets."
    )

    private lateinit var database: SpaceDatabase
    private lateinit var companyDao: CompanyDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        companyDao = database.companyDao()

        companyDao.insertCompanyInfo(companyInfo)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetCores() {
        val data = getValue(companyDao.getCompanyInfo())
        assert(data == companyInfo)
    }
}