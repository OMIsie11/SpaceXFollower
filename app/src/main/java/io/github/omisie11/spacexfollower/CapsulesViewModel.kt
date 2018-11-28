package io.github.omisie11.spacexfollower

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel : ViewModel() {

    private lateinit var allCapsules: LiveData<List<Capsule>>

    fun getCapsules(): LiveData<List<Capsule>> {
        // ToDo load capsules

        return allCapsules
    }
}