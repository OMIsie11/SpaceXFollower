package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.capsule1
import io.github.omisie11.spacexfollower.capsule2
import io.github.omisie11.spacexfollower.capsule3
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.utilities.getValue
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CapsulesDaoTest {

    private lateinit var database: SpaceDatabase
    private lateinit var capsulesDao: CapsulesDao

    // Random order to test if Dao returns sorted data
    private val testCapsulesList = listOf(capsule2, capsule1, capsule3)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        capsulesDao = database.capsulesDao()

        capsulesDao.insertCapsules(testCapsulesList)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetAllCapsulesOrderBySerialDesc() {
        val capsulesList = getValue(capsulesDao.getAllCapsulesOrderBySerialDesc())
        assertThat(capsulesList.size, equalTo(testCapsulesList.size))

        assertThat(capsulesList[0], equalTo(capsule3))
        assertThat(capsulesList[1], equalTo(capsule2))
        assertThat(capsulesList[2], equalTo(capsule1))
    }

    @Test
    fun testGetAllCapsulesOrderBySerialAsc() {
        val capsulesList = getValue(capsulesDao.getAllCapsulesOrderBySerialAsc())
        assertThat(capsulesList.size, equalTo(testCapsulesList.size))

        assertThat(capsulesList[0], equalTo(capsule1))
        assertThat(capsulesList[1], equalTo(capsule2))
        assertThat(capsulesList[2], equalTo(capsule3))
    }

    @Test
    fun testReplaceCapsulesData() {
        // Perform action double to check if data is properly erased and there is no duplicates
        capsulesDao.replaceCapsulesData(testCapsulesList)
        capsulesDao.replaceCapsulesData(testCapsulesList)

        val capsulesList = getValue(capsulesDao.getAllCapsulesOrderBySerialAsc())
        assertThat(capsulesList.size, equalTo(testCapsulesList.size))
    }

    @Test
    fun testDeleteCapsules() {
        capsulesDao.deleteAllCapsules()
        val coresList = getValue(capsulesDao.getAllCapsulesOrderBySerialDesc())
        assertThat(coresList.size, equalTo(0))
    }
}