package io.github.omisie11.spacexfollower.ui.cores

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CoresFragment : Fragment(), CoresAdapter.OnItemClickListener {

    private lateinit var viewAdapter: CoresAdapter
    private val viewModel: CoresViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recycler, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recyclerView
        viewAdapter = CoresAdapter(this)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }

        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        // ViewModel setup
        viewModel.getCores().observe(viewLifecycleOwner, Observer<List<Core>> { cores ->
            viewAdapter.setData(cores)
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getCoresLoadingStatus()
            .observe(viewLifecycleOwner, Observer<Boolean> { areCoresRefreshing ->
                swipeRefreshLayout.isRefreshing = areCoresRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshCores()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Timber.i("onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCores()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfCoresDataOld()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    // Respond to user clicks on recyclerView items
    override fun onItemClicked(coreIndex: Int) {
        findNavController().navigate(
            CoresFragmentDirections
                .actionCoresDestToCoresDetailFragment(coreIndex)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshCores()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteCoresData()
            viewAdapter.notifyDataSetChanged()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
