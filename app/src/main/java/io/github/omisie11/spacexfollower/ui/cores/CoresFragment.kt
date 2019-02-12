package io.github.omisie11.spacexfollower.ui.cores

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CoresFragment : Fragment() {

    private val viewAdapter: CoresAdapter by inject()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val repository: SpaceRepository by inject()
    private val viewModel: CoresViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recyclerView
        viewManager = LinearLayoutManager(activity)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // ViewModel setup
        viewModel.getCores().observe(viewLifecycleOwner, Observer<List<Core>> { cores ->
            viewAdapter.setData(cores)
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CapsulesFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCores()
        }

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getCoresLoadingStatus().observe(viewLifecycleOwner, Observer<Boolean> { areCoresRefreshing ->
            swipeRefreshLayout.isRefreshing = areCoresRefreshing
        })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry), View.OnClickListener {
                        viewModel.refreshCores()
                    }).show()
                viewModel.onSnackbarShown()
            }
        })

        // Force fetching capsules
        fetchButton.setOnClickListener {
            repository.refreshCores()
        }
        // Delete data from capsules table
        deleteEntriesButton.setOnClickListener {
            repository.deleteAllCores()
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfCoresDataOld()
    }
}
