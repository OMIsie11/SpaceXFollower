package io.github.omisie11.spacexfollower.data

import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.RetrofitClientInstance
import io.github.omisie11.spacexfollower.network.SpaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import androidx.lifecycle.MutableLiveData
import retrofit2.Response


class SpaceRepository {

    private val mRetrofit: Retrofit? by lazy { RetrofitClientInstance.getRetrofitInstance() }
    private val mSpaceService: SpaceService? by lazy { mRetrofit?.create(SpaceService::class.java) }

    fun getCapsules(): LiveData<List<Capsule>> {
        val data = MutableLiveData<List<Capsule>>()
        mSpaceService!!.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
            override fun onResponse(call: Call<List<Capsule>>, response: Response<List<Capsule>>) {
                data.value = response.body()
                Log.d("SSS", "${data.value}")
            }

            override fun onFailure(call: Call<List<Capsule>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
        return data
    }
}