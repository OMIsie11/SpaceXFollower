package io.github.omisie11.spacexfollower.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Company

class CompanyViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mCompanyInfo by lazy { repository.getCompanyInfo() }

    fun getCompanyInfo(): LiveData<Company> = mCompanyInfo
}