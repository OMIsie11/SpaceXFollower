package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.NextLaunch

@Dao
interface NextLaunchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNextLaunch(nextLaunch: NextLaunch)

    @Query("SELECT * FROM next_launch_table")
    fun getNextLaunch(): LiveData<NextLaunch>

    @Query("DELETE FROM next_launch_table")
    fun deleteNextLaunchInfo()
}