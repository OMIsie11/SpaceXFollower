package io.github.omisie11.spacexfollower

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: SpaceRepository = SpaceRepository(application)
    private val allCapsules: LiveData<List<Capsule>> by lazy {
        mRepository.getCapsules()
    }

    fun getCapsules(): LiveData<List<Capsule>> {
        return allCapsules
    }
}