package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.local.SpaceDatabase
import io.github.omisie11.spacexfollower.data.local.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.launchPad1
import io.github.omisie11.spacexfollower.launchPad2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LaunchPadsDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var launchPadsDao: LaunchPadsDao

    private val testDispatcher = TestCoroutineDispatcher()

    private val testLaunchPadsList = listOf(launchPad2, launchPad1)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room
            .inMemoryDatabaseBuilder(context, SpaceDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
        launchPadsDao = database.launchPadsDao()
    }

    @After
    fun cleanup() {
        database.close()

        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testInsertAndGetLaunchPads() = runBlocking {
        launchPadsDao.insertLaunchPads(testLaunchPadsList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchPadsDao.getLaunchPadsFlow().collect { launchPads ->
                assertThat(launchPads.size, equalTo(testLaunchPadsList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testReplaceLaunchPadsData() = runBlocking {
        // Replace 2 times to check if there will be no duplicated data
        launchPadsDao.replaceLaunchPads(testLaunchPadsList)
        launchPadsDao.replaceLaunchPads(testLaunchPadsList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchPadsDao.getLaunchPadsFlow().collect { launchPads ->
                assertThat(launchPads.size, equalTo(testLaunchPadsList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testDeleteLaunchPadsData() = runBlocking {
        launchPadsDao.insertLaunchPads(testLaunchPadsList)
        launchPadsDao.deleteLaunchPadsData()

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchPadsDao.getLaunchPadsFlow().collect { launchPads ->
                assertThat(launchPads.size, equalTo(0))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }
}