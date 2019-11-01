package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.UpcomingLaunchesDao
import io.github.omisie11.spacexfollower.launch1
import io.github.omisie11.spacexfollower.launch2
import io.github.omisie11.spacexfollower.utilities.getValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchesDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var upcomingLaunchesDao: UpcomingLaunchesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testLaunchesData = listOf(launch1, launch2)

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        upcomingLaunchesDao = database.upcomingLaunchesDao()

        upcomingLaunchesDao.insertUpcomingLaunches(testLaunchesData)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetUpcomingLaunches() {
        val launchesList = getValue(upcomingLaunchesDao.getUpcomingLaunches())
        Assert.assertThat(launchesList.size, Matchers.equalTo(testLaunchesData.size))
    }

    @Test
    fun testReplaceLaunches() {
        // Perform action double to check if data is properly erased and there is no duplicates
        upcomingLaunchesDao.replaceUpcomingLaunches(testLaunchesData)
        upcomingLaunchesDao.replaceUpcomingLaunches(testLaunchesData)

        val launchesList = getValue(upcomingLaunchesDao.getUpcomingLaunches())
        Assert.assertThat(launchesList.size, Matchers.equalTo(testLaunchesData.size))
    }

    @Test
    fun testDeleteUpcomingLaunches() {
        upcomingLaunchesDao.deleteUpcomingLaunchesData()
        val coresList = getValue(upcomingLaunchesDao.getUpcomingLaunches())
        assertThat(coresList.size, Matchers.equalTo(0))
    }
}