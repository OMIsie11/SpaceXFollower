package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.core1
import io.github.omisie11.spacexfollower.core2
import io.github.omisie11.spacexfollower.core3
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
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
class CoresDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var coresDao: CoresDao

    private val testDispatcher = TestCoroutineDispatcher()

    private val testCoresList = listOf(core2, core3, core1)

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
        coresDao = database.coresDao()
    }

    @After
    fun cleanup() {
        database.close()

        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testInsertAndGetAllCores() = runBlocking {
        coresDao.insertCores(testCoresList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            coresDao.getAllCoresFlow().collect { cores ->
                assertThat(cores.size, equalTo(testCoresList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testReplaceCoresData() = runBlocking {
        // Replace 2 times to check if there will be no duplicated data
        coresDao.replaceCoresData(testCoresList)
        coresDao.replaceCoresData(testCoresList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            coresDao.getAllCoresFlow().collect { cores ->
                assertThat(cores.size, equalTo(testCoresList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testDeleteCores() = runBlocking {
        coresDao.insertCores(testCoresList)
        coresDao.deleteAllCores()

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            coresDao.getAllCoresFlow().collect { cores ->
                assertThat(cores.size, equalTo(0))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testInsertAndGetNumberOfCores() = runBlocking {
        coresDao.insertCores(testCoresList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            coresDao.getNumberOfCoresFlow().collect { numberOfCores ->
                assertThat(numberOfCores, equalTo(testCoresList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }
}