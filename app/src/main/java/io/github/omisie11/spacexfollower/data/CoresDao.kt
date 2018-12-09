package io.github.omisie11.spacexfollower.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.omisie11.spacexfollower.data.model.Core

@Dao
interface CoresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCores(cores: List<Core>)

    @Query("SELECT * FROM cores_table")
    fun getAllCores(): LiveData<List<Core>>

    @Query("DELETE FROM cores_table")
    fun deleteAllCores()
}