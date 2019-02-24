package io.github.omisie11.spacexfollower.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.converters.MissionsConverter


// Single Core data
@Entity(tableName = "cores_table")
data class Core(
    @SerializedName("core_serial")
    @PrimaryKey
    @ColumnInfo(name = "core_serial")
    val coreSerial: String,
    @SerializedName("block")
    @ColumnInfo(name = "block")
    val block: Int?,
    @SerializedName("status")
    @ColumnInfo(name = "status")
    val status: String,
    @SerializedName("original_launch")
    @ColumnInfo(name = "original_launch")
    val originalLaunch: String?,
    @SerializedName("original_launch_unix")
    @ColumnInfo(name = "original_launch_unix")
    val originalLaunchUnix: Long?,
    @SerializedName("missions")
    @TypeConverters(MissionsConverter::class)
    @ColumnInfo(name = "missions")
    val missions: MutableList<Capsule.Mission>?,
    @SerializedName("reuse_count")
    @ColumnInfo(name = "reuse_count")
    val reuseCount: Int,
    @SerializedName("rtls_attempts")
    @ColumnInfo(name = "rtls_attempts")
    val rtlsAttempts: Int,
    @SerializedName("rtls_landings")
    @ColumnInfo(name = "rtls_landings")
    val rtlsLandings: Int,
    @SerializedName("asds_attempts")
    @ColumnInfo(name = "asds_attempts")
    val asdsAttempts: Int,
    @SerializedName("asds_landings")
    @ColumnInfo(name = "asds_landings")
    val asdsLandings: Int,
    @SerializedName("water_landing")
    @ColumnInfo(name = "water_landing")
    val waterLandings: Boolean,
    @SerializedName("details")
    @ColumnInfo(name = "details")
    val details: String?

) {
    // Model for representation of Core missions (JSON array)
    data class Mission(
        @SerializedName("name")
        val name: String,
        @SerializedName("flight")
        val flight: Int
    )
}