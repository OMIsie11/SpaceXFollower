package io.github.omisie11.spacexfollower.data.model.Launch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.converters.LaunchSiteConverter
import io.github.omisie11.spacexfollower.data.converters.RocketConverter

// Used for Next launch screen
@Entity(tableName = "next_launch_table")
data class NextLaunch(
    @SerializedName("flight_number")
    @PrimaryKey
    @ColumnInfo(name = "flight_number")
    val flightNumber: Int,
    @SerializedName("mission_name")
    @ColumnInfo(name = "mission_name")
    val missionName: String,
    @SerializedName("launch_date_unix")
    @ColumnInfo(name = "launch_date_unix")
    val launchDateUnix: Long,
    @SerializedName("rocket")
    @TypeConverters(RocketConverter::class)
    @ColumnInfo(name = "rocket")
    val rocket: Rocket,
    @SerializedName("launch_site")
    @TypeConverters(LaunchSiteConverter::class)
    @ColumnInfo(name = "launch_site")
    val launchSite: LaunchSite
) {
/*
    // ToDo: Implement rocket stages
    data class Rocket(
        @SerializedName("rocket_id")
        val rocketId: String,
        @SerializedName("rocket_name")
        val rocketName: String,
        @SerializedName("rocket_type")
        val rocketType: String
        )

    data class LaunchSite(
        @SerializedName("site_id")
        val siteId: String,
        @SerializedName("site_name")
        val siteName: String,
        @SerializedName("site_name_long")
        val siteNameLong: String
    ) */
}