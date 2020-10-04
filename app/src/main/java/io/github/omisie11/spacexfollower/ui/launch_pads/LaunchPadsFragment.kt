package io.github.omisie11.spacexfollower.ui.launch_pads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.BuildConfig
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import kotlinx.android.synthetic.main.fragment_recycler_swipe_refresh.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class LaunchPadsFragment : Fragment(R.layout.fragment_recycler_swipe_refresh),
    LaunchPadsAdapter.OnItemClickListener {

    private lateinit var viewAdapter: LaunchPadsAdapter
    private val viewModel: LaunchPadsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recyclerView
        viewAdapter = LaunchPadsAdapter(this)
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }

        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        // ViewModel setup
        viewModel.getLaunchPads()
            .observe(viewLifecycleOwner, Observer<List<LaunchPad>> { launchPads ->
                viewAdapter.setData(launchPads)
            })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getLaunchPadsLoadingStatus()
            .observe(viewLifecycleOwner, Observer<Boolean> { areLaunchPadsRefreshing ->
                swipeRefreshLayout.isRefreshing = areLaunchPadsRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(viewLifecycleOwner, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshLaunchPads()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Timber.i("onRefresh called from SwipeRefreshLayout")
            viewModel.refreshLaunchPads()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfLaunchPadsDataOld()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    override fun onLocationClicked(launchPadCoordinates: Triple<Double, Double, String>) {
        // Google Maps expects name of location with spaces replaced with '+'
        val locationName = launchPadCoordinates.third.replace(" ", "+")
        val mapUri = Uri.parse(
            "geo:${launchPadCoordinates.first},${launchPadCoordinates.second}" +
                    "?q=$locationName"
        )
        val mapIntent =
            Intent(Intent.ACTION_VIEW, mapUri).apply { setPackage("com.google.android.apps.maps") }
        startActivity(mapIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
        val deleteItem = menu.findItem(R.id.action_delete)
        // Show option to delete data only in debug builds
        deleteItem.isVisible = BuildConfig.DEBUG
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshLaunchPads()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteLaunchPadsData()
            viewAdapter.notifyDataSetChanged()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
