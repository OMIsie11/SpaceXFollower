package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import android.os.AsyncTask
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import org.jetbrains.anko.doAsync


class SpaceRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val sharedPrefs: SharedPreferences
) {

    private val mAllCapsules: LiveData<List<Capsule>> by lazy { capsulesDao.getAllCapsules() }
    private val mAllCores: LiveData<List<Core>> by lazy { coresDao.getAllCores() }

    companion object {
        // Data refresh interval in milliseconds
        private const val REFRESH_INTERVAL: Long = 10800000
    }

    // Wrapper for getting all capsules from Db
    fun getCapsules(): LiveData<List<Capsule>> {

        return mAllCapsules
    }

    // Wrapper for getting all cores from Db
    fun getCores(): LiveData<List<Core>> {

        return mAllCores
    }

    fun deleteAllCores() {
        DeleteAllCoresAsyncTask(coresDao).execute()
    }

    fun deleteAllCapsules() {
        DeleteAllCapsulesAsyncTask(capsulesDao).execute()
    }

    // ToDO: Proper implementation of refreshing
    // Use shared prefs for saving time of last fetch

    fun fetchCapsulesAndSaveToDb() {
        spaceService.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
            override fun onResponse(call: Call<List<Capsule>>, response: Response<List<Capsule>>) {
                response.body()?.let {
                    doAsync { capsulesDao.insertCapsules(it) }
                }
            }

            override fun onFailure(call: Call<List<Capsule>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
    }

    // Cores
    fun fetchCoresAndSaveToDb() {
        spaceService.getAllCores().enqueue(object : Callback<List<Core>> {
            override fun onResponse(call: Call<List<Core>>, response: Response<List<Core>>) {
                response.body()?.let {
                    doAsync { coresDao.insertCores(it) }
                }
            }

            override fun onFailure(call: Call<List<Core>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
    }

    private fun saveCoresToDb(data: List<Core>) {
        val myExecutor = Executors.newSingleThreadExecutor()
        myExecutor.execute {
            coresDao.insertCores(data)
        }
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, currentTimeMillis)
        Log.d("Repository", "Current time in millis $currentTimeMillis")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > REFRESH_INTERVAL
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