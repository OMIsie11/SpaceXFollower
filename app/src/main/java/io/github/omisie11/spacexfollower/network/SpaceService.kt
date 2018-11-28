package io.github.omisie11.spacexfollower.network

import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import retrofit2.Call
import retrofit2.http.GET

interface SpaceService {

    // Get all capsules
    @GET("v3/capsules")
    fun getAllCapsules(): Call<LiveData<List<Capsule>>>
}