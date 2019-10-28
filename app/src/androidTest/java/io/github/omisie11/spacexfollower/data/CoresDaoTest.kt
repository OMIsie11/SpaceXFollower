package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.core1
import io.github.omisie11.spacexfollower.core2
import io.github.omisie11.spacexfollower.core3
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.utilities.getValue
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoresDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var coresDao: CoresDao

    private val testCoresList = listOf(core2, core3, core1)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        coresDao = database.coresDao()

        coresDao.insertCores(testCoresList)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetAllCores() {
        val coresList = getValue(coresDao.getAllCores())
        assertThat(coresList.size, equalTo(testCoresList.size))
    }

    @Test
    fun testReplaceCoresData() {
        // Perform action double to check if data is properly erased and there is no duplicates
        coresDao.replaceCoresData(testCoresList)
        coresDao.replaceCoresData(testCoresList)

        val coresList = getValue(coresDao.getAllCores())
        assertThat(coresList.size, equalTo(testCoresList.size))
    }

    @Test
    fun testDeleteCores() {
        coresDao.deleteAllCores()
        val coresList = getValue(coresDao.getAllCores())
        assertThat(coresList.size, equalTo(0))
    }
}