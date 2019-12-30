package io.github.omisie11.spacexfollower.ui.launches

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items.*
import kotlinx.android.synthetic.main.fragment_recycler_swipe_refresh.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesDetailFragment : Fragment(), LinkItem.OnLinkItemClickListener {

    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>
    private val viewModel by sharedViewModel<LaunchesViewModel>()

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
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

                // Section with cores carried in launch
                val coresHeaderItem = ExpandableHeaderItem(getString(R.string.label_cores))
                val coresGroup = ExpandableGroup(coresHeaderItem)
                launch.rocket.first_stage.cores?.forEach { core ->
                    coresGroup.add(
                        CoreItem(
                            core.core_serial,
                            core.flight,
                            core.block,
                            core.reused,
                            core.land_success
                        )
                    )
                }
                groupAdapter.add(coresGroup)

                // Section with payloads carried in launch
                val payloadsHeaderItem = ExpandableHeaderItem(getString(R.string.payloads))
                val payloadsGroup = ExpandableGroup(payloadsHeaderItem)
                launch.rocket.second_stage.payloads?.forEach { payload ->
                    payloadsGroup.add(
                        PayloadItem(
                            payload.payload_id,
                            payload.nationality,
                            payload.manufacturer,
                            payload.payload_type,
                            payload.payload_mass_kg,
                            payload.orbit,
                            payload.reused,
                            payload.customers
                        )
                    )
                }
                groupAdapter.add(payloadsGroup)

                // Section with links
                val linksHeaderItem = ExpandableHeaderItem(getString(R.string.links))
                val linksGroup = ExpandableGroup(linksHeaderItem)

                val linksAndNamesList = launch.links.getLinksWithNamesAsList()
                linksAndNamesList.forEach {
                    if (it.second != null && it.second!!.isNotBlank()) {
                        linksGroup.add(
                            LinkItem(
                                it.first,
                                it.second!!,
                                this
                            )
                        )
                    }
                }
                groupAdapter.add(linksGroup)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    override fun onLinkItemClicked(linkUrl: String) {
        openWebUrl(linkUrl)
    }

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlAddress)
            )
        )
    }
}
