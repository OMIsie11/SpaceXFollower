package io.github.omisie11.spacexfollower.data

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.network.SpaceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.KEY_CAPSULES_LAST_REFRESH
import io.github.omisie11.spacexfollower.util.KEY_CORES_LAST_REFRESH
import org.jetbrains.anko.doAsync


class SpaceRepository(
    private val capsulesDao: CapsulesDao,
    private val spaceService: SpaceService,
    private val coresDao: CoresDao,
    private val sharedPrefs: SharedPreferences
) {

    companion object {
        // Data refresh interval in milliseconds (default: 3h = 10800000 ms)
        private const val REFRESH_INTERVAL: Long = 10800000
    }

    // Wrapper for getting all capsules from Db
    fun getCapsules(): LiveData<List<Capsule>> {
        refreshCapsules()
        return capsulesDao.getAllCapsules()
    }

    // Wrapper for getting all cores from Db
    fun getCores(): LiveData<List<Core>> {
        refreshCores()
        return coresDao.getAllCores()
    }

    fun deleteAllCores() {
        doAsync { coresDao.deleteAllCores() }
    }

    fun deleteAllCapsules() {
        doAsync { capsulesDao.deleteAllCapsules() }
    }

    // ToDo make one function for refreshing
    fun refreshCapsules() {
        Log.d("Repository", "refreshCapsules called")
        // Check if refresh is needed
        if (checkIfRefreshIsNeeded(KEY_CAPSULES_LAST_REFRESH)) {
            Log.d("refreshCapsules", "Refreshing capsules")
            fetchCapsulesAndSaveToDb()
            // Save new refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CAPSULES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        }
    }

    fun refreshCores() {
        Log.d("Repository", "refreshCores called")
        // Check if refresh is needed
        if (checkIfRefreshIsNeeded(KEY_CORES_LAST_REFRESH)) {
            Log.d("refreshCores", "Refreshing cores")
            fetchCoresAndSaveToDb()
            // Save new refresh time
            with(sharedPrefs.edit()) {
                putLong(KEY_CORES_LAST_REFRESH, System.currentTimeMillis())
                apply()
            }
        }
    }

    fun fetchCapsulesAndSaveToDb() {
        spaceService.getAllCapsules().enqueue(object : Callback<List<Capsule>> {
            override fun onResponse(call: Call<List<Capsule>>, response: Response<List<Capsule>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        doAsync { capsulesDao.insertCapsules(it) }
                    }
                } else Log.d("Repository", "Error: ${response.errorBody()}")
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
                if (response.isSuccessful) {
                    response.body()?.let {
                        doAsync { coresDao.insertCores(it) }
                    }
                } else Log.d("Repository", "Error: ${response.errorBody()}")
            }

            override fun onFailure(call: Call<List<Core>>, t: Throwable) {
                Log.d("Repo", "FAILURE")
            }

        })
    }

    // Check if data refresh is needed
    private fun checkIfRefreshIsNeeded(sharedPrefsKey: String): Boolean {
        // Get current time in milliseconds
        val currentTimeMillis: Long = System.currentTimeMillis()
        val lastRefreshTime = sharedPrefs.getLong(sharedPrefsKey, 0)
        Log.d("Repository", "Current time in millis $currentTimeMillis")
        // If last refresh was made longer than interval, return true
        return currentTimeMillis - lastRefreshTime > REFRESH_INTERVAL
    }
}