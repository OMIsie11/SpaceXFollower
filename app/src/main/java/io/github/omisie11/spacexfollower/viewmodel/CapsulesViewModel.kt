package io.github.omisie11.spacexfollower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCapsules: LiveData<List<Capsule>> by lazy {
        repository.getCapsules()
    }
    private val mAreCapsulesLoading: LiveData<Boolean> by lazy {
        repository.getCapsulesLoadingStatus()
    }

    fun getCapsules(): LiveData<List<Capsule>> = mAllCapsules

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = mAreCapsulesLoading

    // Wrapper for refreshing capsules data
    fun refreshCapsules() = repository.refreshCapsules()

    // Wrapper for refreshing old data in onResume
    fun refreshIfCapsulesDataOld() = repository.refreshIfCapsulesDataOld()
}