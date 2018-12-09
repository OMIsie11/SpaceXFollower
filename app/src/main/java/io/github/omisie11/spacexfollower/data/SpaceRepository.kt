package io.github.omisie11.spacexfollower.data

import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import android.os.AsyncTask
import io.github.omisie11.spacexfollower.data.model.Core


class SpaceRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val coresDao: CoresDao
) {

    private val mAllCapsules: LiveData<List<Capsule>> by lazy { capsulesDao.getAllCapsules() }
    private val mAllCores: LiveData<List<Core>> by lazy { coresDao.getAllCores() }

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

    // ToDO: Proper implementation of refreshing
    // Use shared prefs for saving time of last fetch

    fun fetchCapsulesAndSaveToDb() {
        spaceService.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
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
            capsulesDao.insertCapsules(data)
        }
    }

    // Wrapper for getting all capsules from DB
    fun getCapsules(): LiveData<List<Capsule>> {

        return mAllCapsules
    }

    fun deleteAllCapsules() {
        DeleteAllCapsulesAsyncTask(capsulesDao).execute()
    }

    private class DeleteAllCapsulesAsyncTask internal constructor(
        private val mAsyncTaskDao: CapsulesDao
    ) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            mAsyncTaskDao.deleteAllCapsules()
            return null
        }
    }

    // Cores
    fun fetchCoresAndSaveToDb() {
        spaceService.getAllCores().enqueue(object : Callback<List<Core>> {
            override fun onResponse(call: Call<List<Core>>, response: Response<List<Core>>) {
                saveCoresToDb(response.body()!!)
            }

            override fun onFailure(call: Call<List<Core>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
    }

    private fun saveCoresToDb(data: List<Core>) {

        val myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute {
            coresDao.insertCores(data)
        }
    }

    fun getCores(): LiveData<List<Core>> {

        return mAllCores
    }

    fun deleteAllCores() {
        DeleteAllCoresAsyncTask(coresDao).execute()
    }

    private class DeleteAllCoresAsyncTask internal constructor(
        private val mAsyncTaskDao: CoresDao
    ) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            mAsyncTaskDao.deleteAllCores()
            return null
        }
    }
}