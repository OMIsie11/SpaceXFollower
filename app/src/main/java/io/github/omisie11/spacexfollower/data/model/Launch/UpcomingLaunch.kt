package io.github.omisie11.spacexfollower.data.model.Launch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.converters.JsonArrayToStringConverter
import io.github.omisie11.spacexfollower.data.converters.RocketConverter


@Entity(tableName = "upcoming_launches_table")
data class UpcomingLaunch(
    @SerializedName("flight_number")
    @PrimaryKey
    @ColumnInfo(name = "flight_number")
    val flightNumber: Int,
    @SerializedName("mission_name")
    @ColumnInfo(name = "mission_name")
    val missionName: String,
    @SerializedName("mission_id")
    @TypeConverters(JsonArrayToStringConverter::class)
    @ColumnInfo(name = "mission_id")
    val missionId: MutableList<String>?,
    @SerializedName("launch_date_unix")
    @ColumnInfo(name = "launch_date_unix")
    val launchDateUnix: Long,
    @SerializedName("is_tentative")
    @ColumnInfo(name = "is_tentative")
    val isTentative: Boolean,
    @SerializedName("launch_site")
    @ColumnInfo(name = "launch_site")
    val launch_site: LaunchSite,
    @SerializedName("rocket")
    @TypeConverters(RocketConverter::class)
    @ColumnInfo(name = "rocket")
    val rocket: Rocket,
    @SerializedName("details")
    @ColumnInfo(name = "details")
    val details: String?
) /* {

    data class Rocket(
        @SerializedName("rocket_id")
        @ColumnInfo(name = "rocket_id")
        val rocket_id: String,
        @SerializedName("rocket_name")
        @ColumnInfo(name = "rocket_name")
        val rocket_name: String,
        @SerializedName("rocket_type")
        @ColumnInfo(name = "rocket_type")
        val rocket_type: String,
        @SerializedName("first_stage")
        @ColumnInfo(name = "first_stage")
        val first_stage: FirstStage,
        @SerializedName("second_stage")
        @ColumnInfo(name = "second_stage")
        val second_stage: SecondStage
        //@SerializedName("fairings") val fairings: Fairings
    )

    data class FirstStage(
        @SerializedName("cores")
        @ColumnInfo(name = "cores")
        val cores: List<Core>
    )

    data class Core(
        @SerializedName("core_serial")
        @ColumnInfo(name = "core_serial")
        val core_serial: String,
        @SerializedName("flight")
        @ColumnInfo(name = "flight")
        val flight: Int,
        @SerializedName("block")
        @ColumnInfo(name = "block")
        val block: Int,
        @SerializedName("gridfins")
        @ColumnInfo(name = "gridfins")
        val gridfins: Boolean,
        @SerializedName("legs")
        @ColumnInfo(name = "legs")
        val legs: Boolean,
        @SerializedName("reused")
        @ColumnInfo(name = "reused")
        val reused: Boolean,
        @SerializedName("land_success")
        @ColumnInfo(name = "land_success")
        val land_success: String,
        @SerializedName("landing_intent")
        @ColumnInfo(name = "landing_intent")
        val landing_intent: Boolean,
        @SerializedName("landing_type")
        @ColumnInfo(name = "landing_type")
        val landing_type: String,
        @SerializedName("landing_vehicle")
        @ColumnInfo(name = "landing_vehicle")
        val landing_vehicle: String
    )

    data class SecondStage(
        @SerializedName("block")
        @ColumnInfo(name = "block")
        val block: Int,
        @SerializedName("payloads")
        @ColumnInfo(name = "payloads")
        val payloads: List<Payload>
    )

    data class Payload(
        @SerializedName("payload_id")
        @ColumnInfo(name = "payload_id")
        val payload_id: String,
        @SerializedName("norad_id")
        @ColumnInfo(name = "norad_id")
        val norad_id: List<String>,
        @SerializedName("reused")
        @ColumnInfo(name = "reused")
        val reused: Boolean,
        @SerializedName("customers")
        @ColumnInfo(name = "customers")
        val customers: List<String>,
        @SerializedName("nationality")
        @ColumnInfo(name = "nationality")
        val nationality: String,
        @SerializedName("manufacturer")
        @ColumnInfo(name = "manufacturer")
        val manufacturer: String,
        @SerializedName("payload_type")
        @ColumnInfo(name = "payload_type")
        val payload_type: String,
        @SerializedName("payload_mass_kg")
        @ColumnInfo(name = "payload_mass_kg")
        val payload_mass_kg: Int,
        @SerializedName("payload_mass_lbs")
        @ColumnInfo(name = "payload_mass_lbs")
        val payload_mass_lbs: Double,
        @SerializedName("orbit")
        @ColumnInfo(name = "orbit")
        val orbit: String
        //@SerializedName("orbit_params") val orbit_params : Orbit_params
    )

    data class LaunchSite(
        @SerializedName("site_id")
        @ColumnInfo(name = "site_id")
        val site_id: String,
        @SerializedName("site_name")
        @ColumnInfo(name = "site_name")
        val site_name: String,
        @SerializedName("site_name_long")
        @ColumnInfo(name = "site_name_long")
        val site_name_long: String
    )
} */