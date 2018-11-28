package io.github.omisie11.spacexfollower.data.model

import com.google.gson.annotations.SerializedName

// Single capsule
data class Capsule(
    @SerializedName("capsule_serial")
    val capsuleSerial: String,
    @SerializedName("capsule_id")
    val capsuleId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("original_launch")
    val originalLaunch: String?,
    @SerializedName("original_launch_unix")
    val originalLaunchUnix: Long?,
    @SerializedName("missions")
    val missions: List<CapsuleMission>?,
    @SerializedName("landings")
    val landings: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("details")
    val details: String?,
    @SerializedName("reuse_count")
    val reuseCount: Int
) {
    // Model for representation of Capsule missions (JSON array)
    data class CapsuleMission(
        @SerializedName("name")
        val name: String,
        @SerializedName("flight")
        val flight: Int
    )
}