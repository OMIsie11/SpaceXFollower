package io.github.omisie11.spacexfollower.ui.capsules

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CapsulesFragment : Fragment() {

    private val viewAdapter: CapsulesAdapter by inject()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val repository: SpaceRepository by inject()
    val viewModel: CapsulesViewModel by viewModel()

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
        viewModel.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            viewAdapter.setData(capsules)
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CapsulesFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCapsules()
        }

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getCapsulesLoadingStatus().observe(viewLifecycleOwner, Observer<Boolean> { areCapsulesRefreshing ->
            swipeRefreshLayout.isRefreshing = areCapsulesRefreshing
        })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry), View.OnClickListener {
                        viewModel.refreshCapsules()
                }).show()
                viewModel.onSnackbarShown()
            }
        })

        // Force fetching capsules
        fetchButton.setOnClickListener {
            repository.refreshCapsules()
        }
        // Delete data from capsules table
        deleteEntriesButton.setOnClickListener {
            repository.deleteAllCapsules()
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfCapsulesDataOld()
    }
}
