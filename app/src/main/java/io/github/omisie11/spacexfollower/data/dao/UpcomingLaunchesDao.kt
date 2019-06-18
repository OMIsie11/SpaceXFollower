package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.UpcomingLaunch

@Dao
interface UpcomingLaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpcomingLaunches(upcomingLaunches: List<UpcomingLaunch>)

    @Query("SELECT * FROM upcoming_launches_table")
    fun getUpcomingLaunches(): LiveData<List<UpcomingLaunch>>

    @Query("DELETE FROM upcoming_launches_table")
    fun deleteUpcomingLaunchesData()

}