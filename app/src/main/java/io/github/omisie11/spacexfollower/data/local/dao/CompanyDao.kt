package io.github.omisie11.spacexfollower.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.omisie11.spacexfollower.data.local.model.Company

@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyInfo(companyInfo: Company)

    @Query("SELECT * FROM company_table")
    fun getCompanyInfo(): LiveData<Company>

    @Query("DELETE FROM company_table")
    fun deleteCompanyInfo()
}