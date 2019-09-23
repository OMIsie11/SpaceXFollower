package io.github.omisie11.spacexfollower.ui.about.used_libraries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsedLibrariesViewModel : ViewModel() {

    private val _usedLibrariesData = MutableLiveData<List<UsedLibrary>>()

    fun getUsedLibs(): LiveData<List<UsedLibrary>> = _usedLibrariesData

    fun setUsedLibs(usedLibs: List<UsedLibrary>) {
        _usedLibrariesData.value = usedLibs
    }
}