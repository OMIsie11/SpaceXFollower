package io.github.omisie11.spacexfollower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Core

class CoresViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCores: LiveData<List<Core>> by lazy {
        repository.getCores()
    }
    private val mAreCoresLoading: LiveData<Boolean> by lazy {
        repository.getCoresLoadingStatus()
    }

    fun getCores(): LiveData<List<Core>> = mAllCores

    fun getCoresLoadingStatus(): LiveData<Boolean> = mAreCoresLoading

    // Wrapper for refreshing cores data
    fun refreshCores() = repository.refreshCores()

    // Wrapper for refreshing old data in onResume
    fun refreshIfCoresDataOld() = repository.refreshIfCoresDataOld()
}