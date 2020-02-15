package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import io.github.omisie11.spacexfollower.capsule1
import io.github.omisie11.spacexfollower.capsule2
import io.github.omisie11.spacexfollower.capsule3
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
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
class CapsulesDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var capsulesDao: CapsulesDao

    private val testDispatcher = TestCoroutineDispatcher()

    // Random order to test if Dao returns sorted data
    private val testCapsulesList = listOf(capsule2, capsule1, capsule3)

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
        capsulesDao = database.capsulesDao()
    }

    @After
    fun cleanup() {
        database.close()

        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testInsertAndGetAllCapsules() = runBlocking {
        capsulesDao.insertCapsules(testCapsulesList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            capsulesDao.getAllCapsulesFlow().collect { capsules ->
                assertThat(capsules.size, equalTo(testCapsulesList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testReplaceCapsulesData() = runBlocking {
        // Replace 2 times to check if there will be no duplicated data
        capsulesDao.replaceCapsulesData(testCapsulesList)
        capsulesDao.replaceCapsulesData(testCapsulesList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            capsulesDao.getAllCapsulesFlow().collect { capsules ->
                assertThat(capsules.size, equalTo(testCapsulesList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testDeleteCapsules() = runBlocking {
        capsulesDao.insertCapsules(testCapsulesList)
        capsulesDao.deleteAllCapsules()

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            capsulesDao.getAllCapsulesFlow().collect { capsules ->
                assertThat(capsules.size, equalTo(0))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }

    @Test
    fun testInsertAndGetNumberOfCapsules() = runBlocking {
        capsulesDao.insertCapsules(testCapsulesList)

        val latch = CountDownLatch(1)
        val job = launch(Dispatchers.IO) {
            capsulesDao.getNumberOfCapsulesFlow().collect { numberOfCapsules ->
                assertThat(numberOfCapsules, equalTo(testCapsulesList.size))
                latch.countDown()
            }
        }

        latch.await()
        job.cancel()
    }
}