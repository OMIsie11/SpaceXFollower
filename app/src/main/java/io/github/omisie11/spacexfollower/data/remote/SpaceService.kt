package io.github.omisie11.spacexfollower.data.remote

import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.local.model.Company
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import retrofit2.Response
import retrofit2.http.GET

interface SpaceService {

    // Get all capsules
    @GET("v3/capsules")
    suspend fun getAllCapsules(): Response<List<Capsule>>

    // Get all cores
    @GET("v3/cores")
    suspend fun getAllCores(): Response<List<Core>>

    // Get company info
    @GET("v3/info")
    suspend fun getCompanyInfo(): Response<Company>

    // All Launches
    @GET("v3/launches")
    suspend fun getAllLaunches(): Response<List<Launch>>

    @GET("v3/launchpads")
    suspend fun getLaunchPads(): Response<List<LaunchPad>>
}