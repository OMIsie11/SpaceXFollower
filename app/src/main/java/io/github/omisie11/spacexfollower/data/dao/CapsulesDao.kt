package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.omisie11.spacexfollower.data.model.Capsule

@Dao
interface CapsulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCapsules(capsules: List<Capsule>)

    @Query("SELECT * FROM capsules_table ORDER BY capsule_serial DESC")
    fun getAllCapsulesOrderBySerialDesc(): LiveData<List<Capsule>>

    @Query("SELECT * FROM capsules_table ORDER BY capsule_serial ASC")
    fun getAllCapsulesOrderBySerialAsc(): LiveData<List<Capsule>>

    @Query("DELETE FROM capsules_table")
    fun deleteAllCapsules()
}