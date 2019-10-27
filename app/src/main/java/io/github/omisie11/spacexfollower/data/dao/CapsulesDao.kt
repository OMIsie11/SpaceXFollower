package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.Capsule

@Dao
interface CapsulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCapsules(capsules: List<Capsule>)

    @Transaction
    fun replaceCapsulesData(capsules: List<Capsule>) {
        deleteAllCapsules()
        insertCapsules(capsules)
    }

    @Query("SELECT * FROM capsules_table ORDER BY capsule_serial DESC")
    fun getAllCapsulesOrderBySerialDesc(): LiveData<List<Capsule>>

    @Query("SELECT * FROM capsules_table ORDER BY capsule_serial ASC")
    fun getAllCapsulesOrderBySerialAsc(): LiveData<List<Capsule>>

    @Query("DELETE FROM capsules_table")
    fun deleteAllCapsules()
}