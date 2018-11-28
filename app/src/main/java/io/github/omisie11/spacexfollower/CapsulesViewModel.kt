package io.github.omisie11.spacexfollower

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel : ViewModel() {

    private val mRepository: SpaceRepository = SpaceRepository()
    private val allCapsules: LiveData<List<Capsule>> by lazy {
        mRepository.getCapsules()
    }

    fun getCapsules(): LiveData<List<Capsule>> {
        return allCapsules
    }
}