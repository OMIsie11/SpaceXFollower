package io.github.omisie11.spacexfollower.ui.company


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Company
import kotlinx.android.synthetic.main.fragment_company.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CompanyFragment : Fragment() {

    private val repository: SpaceRepository by inject()
    private val viewModel: CompanyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCompanyInfo().observe(viewLifecycleOwner, Observer<Company>{companyInfo ->
            text_company.text = companyInfo?.headquarters?.address
        })
    }

    override fun onResume() {
        super.onResume()
        repository.refreshCompanyInfo()
    }
}
