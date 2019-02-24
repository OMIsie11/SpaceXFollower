package io.github.omisie11.spacexfollower.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.converters.HeadquarterConverter

@Entity(tableName = "company_table")
data class Company(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String,
    @SerializedName("founder")
    @ColumnInfo(name = "founder")
    val founder: String,
    @SerializedName("founded")
    @ColumnInfo(name = "founded")
    val founded: Int,
    @SerializedName("employees")
    @ColumnInfo(name = "employees")
    val employees: Int,
    @SerializedName("vehicles")
    @ColumnInfo(name = "vehicles")
    val vehicles: Int,
    @SerializedName("launch_sites")
    @ColumnInfo(name = "launch_sites")
    val launchSites: Int,
    @SerializedName("test_sites")
    @ColumnInfo(name = "test_sites")
    val testSites: Int,
    @SerializedName("ceo")
    @ColumnInfo(name = "ceo")
    val ceo: String,
    @SerializedName("cto")
    @ColumnInfo(name = "cto")
    val cto: String,
    @SerializedName("coo")
    @ColumnInfo(name = "coo")
    val coo: String,
    @SerializedName("cto_propulsion")
    @ColumnInfo(name = "cto_propulsion")
    val ctoPropulsion: String,
    @SerializedName("valuation")
    @ColumnInfo(name = "valuation")
    val valuation: Long,
    @SerializedName("headquarters")
    @TypeConverters(HeadquarterConverter::class)
    @ColumnInfo(name = "headquarters")
    val headquarters: Headquarters,
    @SerializedName("summary")
    @ColumnInfo(name = "summary")
    val summary: String
) {
    data class Headquarters(
        @SerializedName("address")
        val address: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("state")
        val state: String
    )
}