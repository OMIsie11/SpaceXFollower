package io.github.omisie11.spacexfollower.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.omisie11.spacexfollower.data.model.Capsule

@Dao
interface CapsulesDao {

    @Insert
    fun insertCapsules(capsules: List<Capsule>)

    @Query("SELECT * FROM capsules_table")
    fun getAllCapsules(): LiveData<List<Capsule>>

    @Query("DELETE FROM capsules_table")
    fun deleteAllCapsules()
}