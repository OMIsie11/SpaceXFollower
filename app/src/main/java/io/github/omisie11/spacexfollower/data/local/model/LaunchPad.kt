package io.github.omisie11.spacexfollower.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.local.converters.JsonArrayToStringConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchPadLocationConverter

@Entity(tableName = "launch_pads_table")
data class LaunchPad(
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "launch_pad_id")
    val launchPadId: Int,
    @SerializedName("status")
    @ColumnInfo(name = "status")
    val status: String,
    @SerializedName("location")
    @TypeConverters(LaunchPadLocationConverter::class)
    @ColumnInfo(name = "location")
    val location: Location,
    @SerializedName("vehicles_launched")
    @TypeConverters(JsonArrayToStringConverter::class)
    @ColumnInfo(name = "vehicles_launched")
    val vehiclesLaunched: List<String>?,
    @SerializedName("attempted_launches")
    @ColumnInfo(name = "attempted_launches")
    val attemptedLaunches: Int,
    @SerializedName("successful_launches")
    @ColumnInfo(name = "successful_launches")
    val successfulLaunches: Int,
    @SerializedName("wikipedia")
    @ColumnInfo(name = "wikipedia")
    val wikipedia: String?,
    @SerializedName("details")
    @ColumnInfo(name = "details")
    val details: String?,
    @SerializedName("site_id")
    @ColumnInfo(name = "site_id")
    val siteId: String?,
    @SerializedName("site_name_long")
    @ColumnInfo(name = "site_name_long")
    val siteNameLong: String?
) {

    // Representation of Launch Pad location
    data class Location(
        @SerializedName("name")
        val name: String,
        @SerializedName("region")
        val region: String,
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double
    )
}
