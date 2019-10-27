package io.github.omisie11.spacexfollower.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.omisie11.spacexfollower.data.model.Core

@Dao
interface CoresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCores(cores: List<Core>)

    @Transaction
    fun replaceCoresData(cores: List<Core>) {
        deleteAllCores()
        insertCores(cores)
    }

    @Query("SELECT * FROM cores_table")
    fun getAllCores(): LiveData<List<Core>>

    @Query("DELETE FROM cores_table")
    fun deleteAllCores()
}