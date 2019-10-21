package io.github.omisie11.spacexfollower.network

import io.github.omisie11.spacexfollower.data.model.*
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
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

    // Upcoming Launches
    @GET("v3/launches/upcoming")
    suspend fun getUpcomingLaunches(): Response<List<UpcomingLaunch>>

    @GET("v3/launchpads")
    suspend fun getLaunchPads(): Response<List<LaunchPad>>
}