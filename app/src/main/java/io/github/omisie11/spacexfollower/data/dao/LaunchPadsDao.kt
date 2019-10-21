package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.omisie11.spacexfollower.data.model.LaunchPad

@Dao
interface LaunchPadsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLaunchPads(cores: List<LaunchPad>)

    @Query("SELECT * FROM launch_pads_table")
    fun getLaunchPads(): LiveData<List<LaunchPad>>

    @Query("DELETE FROM launch_pads_table")
    fun deleteLaunchPadsData()
}