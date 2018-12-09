package io.github.omisie11.spacexfollower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCapsules: LiveData<List<Capsule>> by lazy {
        repository.getCapsules()
    }

    fun getCapsules(): LiveData<List<Capsule>> {
        return mAllCapsules
    }
}