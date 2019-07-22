package io.github.omisie11.spacexfollower.ui.upcoming_launches


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.util.OnItemClickListener
import io.github.omisie11.spacexfollower.util.addOnItemClickListener
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpcomingLaunchesFragment : Fragment() {

    private lateinit var viewAdapter: UpcomingLaunchesAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val viewModel: UpcomingLaunchesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recycler, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewAdapter = UpcomingLaunchesAdapter()

        // Setup recyclerView
        viewManager = LinearLayoutManager(activity)
        recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        // ViewModel setup
        viewModel.getUpcomingLaunches().observe(viewLifecycleOwner, Observer<List<UpcomingLaunch>> { launches ->
            viewAdapter.setData(launches)
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getLaunchesLoadingStatus().observe(viewLifecycleOwner, Observer<Boolean> { areLaunchesRefreshing ->
            swipeRefreshLayout.isRefreshing = areLaunchesRefreshing
        })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshUpcomingLaunches()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("UpcomingLFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshUpcomingLaunches()
        }

        // Respond to user clicks on recyclerView items
        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                findNavController().navigate(
                    UpcomingLaunchesFragmentDirections
                        .actionUpcomingLaunchesDestToUpcomingLaunchesDetailFragment(position)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfLaunchesDataOld()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshUpcomingLaunches()
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
