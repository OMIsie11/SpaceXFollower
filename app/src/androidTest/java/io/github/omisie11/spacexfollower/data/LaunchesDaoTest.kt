package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.launch1
import io.github.omisie11.spacexfollower.launch2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LaunchesDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var launchesDao: AllLaunchesDao

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testLaunchesData = listOf(launch1, launch2)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room
            .inMemoryDatabaseBuilder(context, SpaceDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
        launchesDao = database.upcomingLaunchesDao()
    }

    @After
    fun cleanup() {
        database.close()

        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testInsertAndGetUpcomingLaunches() = runBlocking {
        launchesDao.insertLaunches(testLaunchesData)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchesDao.getAllLaunchesFlow().collect { launches ->
                assertThat(launches.size, equalTo(testLaunchesData.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testReplaceLaunchesData() = runBlocking {
        // Perform action double to check if data is properly erased and there is no duplicates
        launchesDao.replaceUpcomingLaunches(testLaunchesData)
        launchesDao.replaceUpcomingLaunches(testLaunchesData)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchesDao.getAllLaunchesFlow().collect { launches ->
                assertThat(launches.size, equalTo(testLaunchesData.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testDeleteUpcomingLaunches() = runBlocking {
        launchesDao.insertLaunches(testLaunchesData)
        launchesDao.deleteLaunchesData()

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            launchesDao.getAllLaunchesFlow().collect { launches ->
                assertThat(launches.size, equalTo(0))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }
}