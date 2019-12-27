package io.github.omisie11.spacexfollower.ui.launches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items.ExpandableHeaderItem
import io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items.LaunchDetailHeaderItem
import io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items.PayloadItem
import kotlinx.android.synthetic.main.fragment_launches_detail.*
import kotlinx.android.synthetic.main.fragment_recycler.*
import kotlinx.android.synthetic.main.launch_cores.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesDetailFragment : Fragment() {

    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>
    private val viewModel by sharedViewModel<LaunchesViewModel>()
    // Variable used in animating expand/collapse icon
    private var coresIconRotationAngle = 0f
    private var payloadsIconRotationAngle = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_recycler, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupAdapter = GroupAdapter()

        // Setup recyclerView
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = groupAdapter
        }

        val safeArgs = arguments?.let { LaunchesDetailFragmentArgs.fromBundle(it) }
        val selectedLaunchId: Int = safeArgs?.itemId ?: 0

        viewModel.getAllLaunches().observe(viewLifecycleOwner, Observer<List<Launch>> { launches ->
            if (launches != null) {
                val launch = launches[selectedLaunchId]

                // Implement diff utils in future
                groupAdapter.clear()

                // Card with main info about launch
                groupAdapter.add(
                    LaunchDetailHeaderItem(
                        launch.flightNumber,
                        launch.links.missionPatchSmall,
                        launch.missionName,
                        launch.launchDateUnix,
                        launch.launch_site.siteName
                    )
                )

                // Section with payloads carried in launch
                val payloadsExpandableHeaderItem = ExpandableHeaderItem("Payloads")
                val payloadsGroup = ExpandableGroup(payloadsExpandableHeaderItem)
                launch.rocket.second_stage.payloads?.forEach {
                    payloadsGroup.add(
                        PayloadItem(
                            it.nationality
                        )
                    )
                }
                groupAdapter.add(payloadsGroup)


            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }
}
