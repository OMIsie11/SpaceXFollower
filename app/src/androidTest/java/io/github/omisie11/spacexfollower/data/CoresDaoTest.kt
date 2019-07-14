package io.github.omisie11.spacexfollower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Core
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

    private val core1 = Core(
        "Merlin1A", null, "expended", "2006-03-24T22:30:00.000Z",
        1143239400, mutableListOf(Capsule.Mission("FalconSat", 1)), 0,
        0,
        0, 0, 0, false,
        "Engine failure at T+33 seconds resulted in loss of vehicle"
    )
    private val core2 = Core(
        "Merlin2A", null, "expended", "2007-03-21T01:10:00.000Z",
        1174439400, mutableListOf(Capsule.Mission("DemoSat", 2)), 0,
        0,
        0, 0, 0, false,
        "Successful first-stage burn and transition to second stage, maximal altitude 289 km. Harmonic" +
                " oscillation at T+5 minutes Premature engine shutdown at T+7 min 30 s. Failed to reach orbit."
    )
    private val core3 = Core(
        "Merlin1C", null, "expended", "2008-08-02T03:34:00.000Z",
        1217648040, mutableListOf(Capsule.Mission("Trailblazer", 3)), 0,
        0,
        0, 0, 0, false,
        "Residual stage-1 thrust led to collision between stage 1 and stage 2."
    )

    private lateinit var database: SpaceDatabase
    private lateinit var coresDao: CoresDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SpaceDatabase::class.java).build()
        coresDao = database.coresDao()

        coresDao.insertCores(listOf(core2, core3, core1))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetCores() {
        val coresList = getValue(coresDao.getAllCores())
        assertThat(coresList.size, equalTo(3))
    }

}