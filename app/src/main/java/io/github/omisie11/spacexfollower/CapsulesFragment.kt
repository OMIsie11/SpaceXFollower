package io.github.omisie11.spacexfollower

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.viewmodel.CapsulesViewModel
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CapsulesFragment : Fragment() {

    private val viewAdapter: CapsulesAdapter by inject()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val repository: SpaceRepository by inject()
    val model: CapsulesViewModel by viewModel()

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
        model.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            viewAdapter.setData(capsules)
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CapsulesFragment", "onRefresh called from SwipeRefreshLayout")
            model.refreshCapsules()
        }

        // Observe if data is refreshing and show/hide loading indicator
        model.getCapsulesLoadingStatus().observe(viewLifecycleOwner, Observer<Boolean> { areCapsulesRefreshing ->
            swipeRefreshLayout.isRefreshing = areCapsulesRefreshing
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
        model.refreshIfCapsulesDataOld()
    }
}
