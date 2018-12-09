package io.github.omisie11.spacexfollower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Core

class CoresViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCores: LiveData<List<Core>> by lazy {
        repository.getCores()
    }

    fun getAllCores(): LiveData<List<Core>> {
        return mAllCores
    }
}