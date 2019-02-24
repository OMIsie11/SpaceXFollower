package io.github.omisie11.spacexfollower.network

import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface SpaceService {

    // Get all capsules
    @GET("v3/capsules")
    fun getAllCapsules(): Deferred<Response<List<Capsule>>>

    // Get all cores
    @GET("v3/cores")
    fun getAllCores(): Deferred<Response<List<Core>>>

    // Get company info
    @GET("v3/info")
    fun getCompanyInfo(): Deferred<Response<Company>>
}