package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.model.Capsule
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

    private val capsule1 = Capsule(
        "C101", "dragon1", "retired",
        "2010-12-08T15:43:00.000Z", 1291822980,
        mutableListOf(Capsule.Mission("COTS 1", 7)), 0, "Dragon 1.0",
        "Reentered after three weeks in orbit", 0
    )

    private val capsule2 = Capsule(
        "C102", "dragon1", "retired",
        "2012-05-02T07:44:00.000Z", 1335944640,
        mutableListOf(Capsule.Mission("COTS 2", 8)), 1, "Dragon 1.0",
        "First Dragon spacecraft", 0
    )

    private val capsule3 = Capsule(
        "C103", "dragon1", "unknown",
        "2012-10-08T00:35:00.000Z", 1349656500,
        mutableListOf(Capsule.Mission("CRS-1", 9)), 1, "Dragon 1.0",
        "First of twenty missions flown under the CRS1 contract", 0
    )

    private val testCapsulesList = listOf(capsule2, capsule1, capsule3)

    private lateinit var database: SpaceDatabase
    private lateinit var capsulesDao: CapsulesDao

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
    fun testDeleteCapsules() {
        capsulesDao.deleteAllCapsules()
        val coresList = getValue(capsulesDao.getAllCapsulesOrderBySerialDesc())
        assertThat(coresList.size, equalTo(0))
    }
}