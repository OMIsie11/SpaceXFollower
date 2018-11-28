package io.github.omisie11.spacexfollower.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClientInstance {

    companion object {
        // Base URL for SpaceX API
        const val SPACE_X_BASE_URL = "https://api.spacexdata.com/"

        private var retrofitInstance: Retrofit? = null

        fun getRetrofitInstance(): Retrofit? {
            if (retrofitInstance == null) {

                retrofitInstance = Retrofit.Builder()
                    .baseUrl(SPACE_X_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitInstance
        }
    }
}