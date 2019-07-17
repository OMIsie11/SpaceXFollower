package io.github.omisie11.spacexfollower.ui.upcoming_launches


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.fragment_upcoming_launches_detail.*
import kotlinx.android.synthetic.main.fragment_upcoming_launches_detail.frame_cores_list
import kotlinx.android.synthetic.main.upcoming_launch_cores.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpcomingLaunchesDetailFragment : Fragment() {

    private val viewModel by viewModel<UpcomingLaunchesViewModel>()
    // Variable used in animating expand/collapse icon
    private var coresIconRotationAngle = 0f

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
            text_launch_date.text = if (launches[selectedLaunchId].launchDateUnix != null)
                getLocalTimeFromUnix(launches[selectedLaunchId].launchDateUnix!!) else
                getString(R.string.launch_date_null)
            text_launch_site_name.text = launches[selectedLaunchId].launch_site.siteName

            // Dynamically add views for Cores used in flight
            if (launches[selectedLaunchId].rocket.first_stage.cores == null) {
                val noCoresTextView = TextView(activity).apply {
                    text = "No cores are used in this mission"
                }
                frame_cores_list.addView(noCoresTextView)
            } else {
                for (core in launches[selectedLaunchId].rocket.first_stage.cores!!) {
                    if (core.core_serial.isNullOrEmpty() || core.block == null || core.flight == null) {
                        val noDataTextView = TextView(activity).apply {
                            text = "No precision info provided"
                        }
                        frame_cores_list.addView(noDataTextView)
                    } else {
                        val coreLinearLayout = layoutInflater.inflate(
                            R.layout.upcoming_launch_cores,
                            frame_cores_list, false
                        )
                        frame_cores_list.addView(coreLinearLayout)
                        coreLinearLayout.text_core_serial.text = core.core_serial
                        coreLinearLayout.text_core_block.text = core.block.toString()
                        coreLinearLayout.text_core_flight.text = core.flight.toString()
                    }
                }
            }
            // Dynamically add views for Payloads in flight
            if (launches[selectedLaunchId].rocket.second_stage.payloads == null) {
                val noPayloadsTextView = TextView(activity).apply {
                    text = "No payloads"
                }
                frame_cores_list.addView(noPayloadsTextView)
            } else {
                for (payload in launches[selectedLaunchId].rocket.second_stage.payloads!!) {
                    if (payload.payload_id.isNullOrEmpty() || payload.reused == null || payload.manufacturer == null) {
                        val noDataTextView = TextView(activity).apply {
                            text = "No precision info provided"
                        }
                        frame_payloads.addView(noDataTextView)
                    } else {
                        val coreLinearLayout = layoutInflater.inflate(
                            R.layout.upcoming_launch_cores,
                            frame_payloads, false
                        )
                        frame_payloads.addView(coreLinearLayout)
                        coreLinearLayout.text_core_serial.text = payload.payload_id
                        coreLinearLayout.text_core_block.text = payload.payload_type
                        coreLinearLayout.text_core_flight.text = payload.nationality
                    }
                }
            }

        })

        card_cores_list.setOnClickListener {
            TransitionManager.beginDelayedTransition(card_cores_list)
            when (frame_cores_list.visibility) {
                View.GONE -> frame_cores_list.visibility = View.VISIBLE
                View.VISIBLE -> frame_cores_list.visibility = View.GONE
            }
            coresIconRotationAngle = if (coresIconRotationAngle == 0f) 180f else 0f
            image_cores_expand_arrow.animate().rotation(coresIconRotationAngle).setDuration(500).start()
        }
    }
}
