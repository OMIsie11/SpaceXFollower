package io.github.omisie11.spacexfollower.ui.upcoming_launches


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.fragment_upcoming_launches_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpcomingLaunchesDetailFragment : Fragment() {

    private val viewModel by viewModel<UpcomingLaunchesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_launches_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs = arguments?.let { UpcomingLaunchesDetailFragmentArgs.fromBundle(it) }
        val selectedLaunchId: Int = safeArgs?.itemId ?: 0

        viewModel.getUpcomingLaunches().observe(viewLifecycleOwner, Observer<List<UpcomingLaunch>> { launches ->
            text_flight_number.text = launches[selectedLaunchId].flightNumber.toString()
            text_mission_name.text = launches[selectedLaunchId].missionName
            //text_launch_date.text = getLocalTimeFromUnix(launches[selectedLaunchId].launchDateUnix)
            text_launch_date.text = if (launches[selectedLaunchId].launchDateUnix != null)
                getLocalTimeFromUnix(launches[selectedLaunchId].launchDateUnix!!) else
                "No launch date info"
            text_launch_site_name.text = launches[selectedLaunchId].launch_site.siteName
        })
    }
}
