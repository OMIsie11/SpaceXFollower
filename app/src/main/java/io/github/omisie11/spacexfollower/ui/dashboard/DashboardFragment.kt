package io.github.omisie11.spacexfollower.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.util.animateNumber
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNumberOfLaunches().observe(viewLifecycleOwner, Observer { numberOfLaunches ->
            text_number_of_launches.animateNumber(finalValue = numberOfLaunches)
        })

        viewModel.getNumberOfCapsules().observe(viewLifecycleOwner, Observer { numberOfCapsules ->
            text_number_of_capsules.animateNumber(finalValue = numberOfCapsules)
        })

        viewModel.getNumberOfCores().observe(viewLifecycleOwner, Observer { numberOfCores ->
            text_number_of_cores.animateNumber(finalValue = numberOfCores)
        })
    }
}
