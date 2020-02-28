package io.github.omisie11.spacexfollower.data.local.dao

import androidx.room.*
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import kotlinx.coroutines.flow.Flow

@Dao
interface AllLaunchesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunches(launches: List<Launch>)

    @Transaction
    suspend fun replaceAllLaunches(launches: List<Launch>) {
        deleteLaunchesData()
        insertLaunches(launches)
    }

    @Query("SELECT * FROM upcoming_launches_table")
    fun getAllLaunchesFlow(): Flow<List<Launch>>

    @Query("SELECT COUNT(flight_number) FROM upcoming_launches_table")
    fun getNumberOfLaunchesFlow(): Flow<Int>

    @Query("SELECT * FROM upcoming_launches_table WHERE launch_date_unix " +
                "BETWEEN :startDateUnix AND :endDateUnix")
    fun getLaunchesBetweenDatesFlow(startDateUnix: Long, endDateUnix: Long): Flow<List<Launch>>

    @Query("SELECT * FROM upcoming_launches_table WHERE launch_date_unix > :startDateUnix")
    suspend fun getLaunchesLaterThanDate(startDateUnix: Long): List<Launch>

    @Query("DELETE FROM upcoming_launches_table")
    suspend fun deleteLaunchesData()
}