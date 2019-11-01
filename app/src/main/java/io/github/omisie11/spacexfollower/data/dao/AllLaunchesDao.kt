package io.github.omisie11.spacexfollower.data.dao

import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import kotlinx.coroutines.flow.Flow

@Dao
interface AllLaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunches(launches: List<Launch>)

    @Transaction
    suspend fun replaceUpcomingLaunches(launches: List<Launch>) {
        deleteUpcomingLaunchesData()
        insertLaunches(launches)
    }

    @Query("SELECT * FROM upcoming_launches_table")
    fun getAllLaunchesFlow(): Flow<List<Launch>>

    @Query("DELETE FROM upcoming_launches_table")
    suspend fun deleteUpcomingLaunchesData()
}