package io.github.omisie11.spacexfollower.network

import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Core
import retrofit2.Call
import retrofit2.http.GET

interface SpaceService {

    // Get all capsules
    @GET("v3/capsules")
    fun getAllCapsules(): Call<List<Capsule>>

    // Get all cores
    @GET("v3/coress")
    fun getAllCores(): Call<List<Core>>
}