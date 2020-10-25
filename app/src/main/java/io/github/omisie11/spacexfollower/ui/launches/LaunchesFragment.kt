package io.github.omisie11.spacexfollower.ui.launches

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import io.github.omisie11.spacexfollower.BuildConfig
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch
import io.github.omisie11.spacexfollower.util.themeColor
import kotlinx.android.synthetic.main.fragment_recycler_sorting.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class LaunchesFragment : Fragment(R.layout.fragment_recycler_sorting),
    LaunchesAdapter.OnItemClickListener {

    private lateinit var viewAdapter: LaunchesAdapter
    private val viewModel: LaunchesViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterAndReturnTransition()
        // Setup recyclerView
        viewAdapter = LaunchesAdapter(this)
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }

        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        // ViewModel setup
        viewModel.getAllLaunches().observe(viewLifecycleOwner, Observer<List<Launch>> { launches ->
            when {
                launches.isEmpty() -> {
                    recyclerView.visibility = View.GONE
                    empty_state.visibility = View.VISIBLE
                }
                launches != null -> {
                    recyclerView.visibility = View.VISIBLE
                    empty_state.visibility = View.GONE
                    viewAdapter.setData(launches)
                }
            }
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getLaunchesLoadingStatus()
            .observe(viewLifecycleOwner, Observer<Boolean> { areLaunchesRefreshing ->
                swipeRefreshLayout.isRefreshing = areLaunchesRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(viewLifecycleOwner, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshAllLaunches()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        viewModel.getLaunchesSortingOrder().observe(viewLifecycleOwner, Observer { sortingOrder ->
            button_sorting.text = when (sortingOrder) {
                LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_NEWEST ->
                    getString(R.string.flight_number_newest)
                LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_OLDEST ->
                    getString(R.string.flight_number_oldest)
                else -> getString(R.string.flight_number_newest)
            }
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Timber.i("onRefresh called from SwipeRefreshLayout")
            viewModel.refreshAllLaunches()
        }

        button_sorting.setOnClickListener {
            val sortingBottomSheetDialog = LaunchesSortingBottomSheetFragment()

            sortingBottomSheetDialog.show(
                childFragmentManager,
                LaunchesSortingBottomSheetFragment.TAG
            )
        }

        fab_scroll_to_top.setOnClickListener {
            recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 0)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0 && fab_scroll_to_top.visibility == View.VISIBLE) {
                    fab_scroll_to_top.hide()
                } else if (dy > 0 && fab_scroll_to_top.visibility != View.VISIBLE) {
                    fab_scroll_to_top.show()
                }
            }
        })
    }

    private fun enterAndReturnTransition() {
        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.text_label_launches)
            endView = root
            duration = resources.getInteger(R.integer.core_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSurface)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.core_motion_duration_medium).toLong()
            addTarget(R.id.root)
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfLaunchesDataOld()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    // Respond to user clicks on recyclerView items
    override fun onItemClicked(launchIndex: Int, itemView: View) {
        val extras = FragmentNavigatorExtras(
            itemView to launchIndex.toString()
        )
        val action = LaunchesFragmentDirections
            .actionUpcomingLaunchesDestToUpcomingLaunchesDetailFragment(launchIndex)
        findNavController().navigate(action, extras)
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
            viewModel.refreshAllLaunches()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteLaunchesData()
            viewAdapter.notifyDataSetChanged()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
