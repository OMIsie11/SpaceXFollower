package io.github.omisie11.spacexfollower.ui.about.used_libraries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.R
import kotlinx.coroutines.launch

class UsedLibrariesViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _usedLibrariesData = MutableLiveData<List<UsedLibrary>>()

    fun getUsedLibs(): LiveData<List<UsedLibrary>> = _usedLibrariesData

    init {
        viewModelScope.launch {
            val type = object : TypeToken<List<UsedLibrary>>() {}.type
            val objectString = app.baseContext.resources.openRawResource(R.raw.used_libraries)
                .bufferedReader().use { it.readText() }
            _usedLibrariesData.postValue(Gson().fromJson(objectString, type))
        }
    }
}
