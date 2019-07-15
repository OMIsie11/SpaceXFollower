package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.UpcomingLaunchesDao
import io.github.omisie11.spacexfollower.data.model.launch.LaunchSite
import io.github.omisie11.spacexfollower.data.model.launch.Rocket
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.utilities.getValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpcomingLaunchesDaoTest {

    private val launch1 = UpcomingLaunch(
        75, "Nusantara Satu (PSN-6) / GTO-1 / Beresheet",
        null, 1550799900, false, LaunchSite(
            "ccafs_slc_40", "ccafs_slc_40", "Cape Canaveral Air Force Station Space " +
                    "Launch Complex 40"
        ), Rocket(
            "falcon9", "Falcon 9", "FT", Rocket.FirstStage(
                listOf(Rocket.Core("B1048", 3, 4, true))
            )
        ), null
    )

    private val launch2 = UpcomingLaunch(
        76, "CCtCap Demo Mission 1",
        mutableListOf("EE86F74"), 1551512700, false, LaunchSite(
            "ksc_lc_39a", "KSC LC 39A", "Kennedy Space Center Historic Launch Complex 39A"
        ), Rocket(
            "falcon9", "Falcon 9", "FT", Rocket.FirstStage(
                listOf(Rocket.Core("B1051", 1, 5, false))
            )
        ), "Demonstration mission to ISS for NASA with an uncrewed Dragon 2 capsule."
    )

    private val testLaunchesData = listOf(launch1, launch2)

    private lateinit var database: SpaceDatabase
    private lateinit var upcomingLaunchesDao: UpcomingLaunchesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun testDeleteUpcomingLaunches() {
        upcomingLaunchesDao.deleteUpcomingLaunchesData()
        val coresList = getValue(upcomingLaunchesDao.getUpcomingLaunches())
        assertThat(coresList.size, Matchers.equalTo(0))
    }
}