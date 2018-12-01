package io.github.omisie11.spacexfollower.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.RetrofitClientInstance
import io.github.omisie11.spacexfollower.network.SpaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.Response
import java.util.concurrent.Executors
import android.os.AsyncTask




class SpaceRepository(application: Application) {

    private var mCapsulesDao: CapsulesDao
    private var mAllCapsules: LiveData<List<Capsule>>
    private val mRetrofit: Retrofit? by lazy { RetrofitClientInstance.getRetrofitInstance() }
    private val mSpaceService: SpaceService? by lazy { mRetrofit?.create(SpaceService::class.java) }

    init {
        val database = SpaceDatabase.getDatabase(application)!!
        mCapsulesDao = database.capsulesDao()
        mAllCapsules = mCapsulesDao.getAllCapsules()
    }

    //fun getCapsules(): LiveData<List<Capsule>> {
    //  val data = MutableLiveData<List<Capsule>>()
    //mSpaceService!!.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
    //  override fun onResponse(call: Call<List<Capsule>>, response: Response<List<Capsule>>) {
    //    data.value = response.body()
    //   Log.d("SSS", "${data.value}")
    //}
//
    //          override fun onFailure(call: Call<List<Capsule>>, t: Throwable) {
    //            Log.d("Repo", "FAILURE")
    //      }
//
    //      })
    //    return data
    //}


    fun fetchCapsulesAndSaveToDb() {
        mSpaceService!!.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
            override fun onResponse(call: Call<List<Capsule>>, response: Response<List<Capsule>>) {
                saveCapsulesToDb(response.body()!!)
            }

            override fun onFailure(call: Call<List<Capsule>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
    }

    private fun saveCapsulesToDb(data: List<Capsule>) {

        val myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute {
            mCapsulesDao.insertCapsules(data)
        }
    }

    fun getCapsules(): LiveData<List<Capsule>> {

        return mAllCapsules
    }

    fun deleteAllCapsules() {
        DeleteAllCapsulesAsyncTask(mCapsulesDao).execute()
    }

    private class DeleteAllCapsulesAsyncTask internal constructor(
        private val mAsyncTaskDao: CapsulesDao) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            mAsyncTaskDao.deleteAllCapsules()
            return null
        }
    }
}