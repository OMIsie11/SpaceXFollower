package io.github.omisie11.spacexfollower.data.dao

import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.coroutines.flow.Flow

@Dao
interface CapsulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsules(capsules: List<Capsule>)

    @Transaction
    suspend fun replaceCapsulesData(capsules: List<Capsule>) {
        deleteAllCapsules()
        insertCapsules(capsules)
    }

    @Query("SELECT * FROM capsules_table")
    fun getAllCapsulesFlow(): Flow<List<Capsule>>

    @Query("DELETE FROM capsules_table")
    suspend fun deleteAllCapsules()
}