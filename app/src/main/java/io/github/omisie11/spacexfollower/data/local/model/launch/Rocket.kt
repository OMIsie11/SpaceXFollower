package io.github.omisie11.spacexfollower.data.local.model.launch

import com.google.gson.annotations.SerializedName

data class Rocket(
    @SerializedName("rocket_id")
    val rocket_id: String,
    @SerializedName("rocket_name")
    val rocket_name: String,
    @SerializedName("rocket_type")
    val rocket_type: String,
    @SerializedName("first_stage")
    val first_stage: FirstStage,
    @SerializedName("second_stage")
    val second_stage: SecondStage
    // @SerializedName("fairings") val fairings: Fairings
) {

    data class FirstStage(
        @SerializedName("cores")
        val cores: List<Core>?
    )

    data class Core(
        @SerializedName("core_serial")
        val core_serial: String?,
        @SerializedName("flight")
        val flight: Int?,
        @SerializedName("block")
        val block: Int?,
        @SerializedName("gridfins")
        val gridfins: Boolean?,
        @SerializedName("legs")
        val legs: Boolean?,
        @SerializedName("reused")
        val reused: Boolean?,
        @SerializedName("land_success")
        val land_success: String?,
        @SerializedName("landing_intent")
        val landing_intent: Boolean?,
        @SerializedName("landing_type")
        val landing_type: String?,
        @SerializedName("landing_vehicle")
        val landing_vehicle: String?
    )

    data class SecondStage(
        @SerializedName("block")
        val block: Int?,
        @SerializedName("payloads")
        val payloads: List<Payload>?
    )

    data class Payload(
        @SerializedName("payload_id")
        val payload_id: String?,
        // @SerializedName("norad_id")
        // val norad_id: List<String>,
        @SerializedName("reused")
        val reused: Boolean?,
        @SerializedName("customers")
        val customers: List<String>,
        @SerializedName("nationality")
        val nationality: String?,
        @SerializedName("manufacturer")
        val manufacturer: String?,
        @SerializedName("payload_type")
        val payload_type: String?,
        @SerializedName("payload_mass_kg")
        val payload_mass_kg: Double?,
        @SerializedName("payload_mass_lbs")
        val payload_mass_lbs: Double?,
        @SerializedName("orbit")
        val orbit: String?
        // @SerializedName("orbit_params")
        // val orbit_params : Orbit_params
    )
}