package io.github.omisie11.spacexfollower

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val allCapsules: LiveData<List<Capsule>> by lazy {
        repository.getCapsules()
    }

    fun getCapsules(): LiveData<List<Capsule>> {
        return allCapsules
    }
}