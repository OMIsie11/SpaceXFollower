package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.launchPad1
import io.github.omisie11.spacexfollower.launchPad2
import io.github.omisie11.spacexfollower.utilities.getValue
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchPadsDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var launchPadsDao: LaunchPadsDao

    private val testLaunchPadsList = listOf(launchPad2, launchPad1)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        launchPadsDao = database.launchPadsDao()

        launchPadsDao.insertLaunchPads(testLaunchPadsList)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetLaunchPads() {
        val launchPadsList = getValue(launchPadsDao.getLaunchPads())
        assertThat(launchPadsList.size, equalTo(testLaunchPadsList.size))
    }

    @Test
    fun testDeleteLaunchPadsData() {
        launchPadsDao.deleteLaunchPadsData()
        val launchPadsList = getValue(launchPadsDao.getLaunchPads())
        assertThat(launchPadsList.size, equalTo(0))
    }
}