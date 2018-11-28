package io.github.omisie11.spacexfollower.data.model

import androidx.room.Entity

// Single capsule
data class Capsule(
    val capsuleSerial: String,
    val capsuleId: String,
    val status: String,
    val originalLaunch: String?,
    val originalLaunchUnix: Long?,
    val missions: List<CapsuleMission>?,
    val landings: Int,
    val type: String,
    val details: String?,
    val reuseCount: Int
) {
    // Model for representation of Capsule missions (JSON array)
    data class CapsuleMission(
        val name: String,
        val flight: Int
    )
}