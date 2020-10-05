package io.github.omisie11.spacexfollower.ui.launches

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.transition.MaterialContainerTransform
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.data.local.model.launch.Rocket
import io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items.*
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesDetailFragment : Fragment(R.layout.fragment_recycler),
    LinkItem.OnLinkItemClickListener {

    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>
    private val viewModel by sharedViewModel<LaunchesViewModel>()

    private val args: LaunchesDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_root_view.transitionName = args.itemId.toString()

        groupAdapter = GroupAdapter()

        // Setup recyclerView
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

    private fun addLaunchDataToAdapter(launch: Launch) {
        // Add card with main info about launch
        groupAdapter.add(
            LaunchDetailHeaderItem(
                launch.flightNumber,
                launch.links.missionPatchSmall,
                launch.missionName,
                launch.launchDateUnix,
                launch.launch_site.siteName
            )
        )

        // Screen section with cores carried in launch
        addLaunchCoresToAdapter(launch.rocket.first_stage.cores)
        // Screen section with payloads carried in launch
        addLaunchPayloadsToAdapter(launch.rocket.second_stage.payloads)
        // Screen section with links connected with launch
        addLaunchLinksToAdapter(launch.links)
    }

    private fun addLaunchCoresToAdapter(launchCores: List<Rocket.Core>?) {
        val coresHeaderItem = ExpandableHeaderItem(getString(R.string.label_cores))
        val coresGroup = ExpandableGroup(coresHeaderItem)
        launchCores?.forEach { core ->
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
    }

    private fun addLaunchPayloadsToAdapter(launchPayloads: List<Rocket.Payload>?) {
        val payloadsHeaderItem = ExpandableHeaderItem(getString(R.string.payloads))
        val payloadsGroup = ExpandableGroup(payloadsHeaderItem)
        launchPayloads?.forEach { payload ->
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
    }

    private fun addLaunchLinksToAdapter(launchLinks: Launch.Links) {
        val linksHeaderItem = ExpandableHeaderItem(getString(R.string.links))
        val linksGroup = ExpandableGroup(linksHeaderItem)

        val linksAndNamesList = launchLinks.getLinksWithNamesAsList()
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

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlAddress)
            )
        )
    }
}
