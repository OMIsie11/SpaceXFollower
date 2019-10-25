package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.launch.Launch

@Dao
interface UpcomingLaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpcomingLaunches(launches: List<Launch>)

    @Transaction
    fun replaceUpcomingLaunches(launches: List<Launch>) {
        deleteUpcomingLaunchesData()
        insertUpcomingLaunches(launches)
    }

    @Query("SELECT * FROM upcoming_launches_table")
    fun getUpcomingLaunches(): LiveData<List<Launch>>

    @Query("DELETE FROM upcoming_launches_table")
    fun deleteUpcomingLaunchesData()
}