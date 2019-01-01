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

import org.jetbrains.anko.doAsync
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
        val rootView = inflater.inflate(R.layout.fragment_recycler, container, false)

        return rootView
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
        model.getCapsules().observe(this, Observer<List<Capsule>> { capsules ->
            viewAdapter.setData(capsules)
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CapsulesFragment", "onRefresh called from SwipeRefreshLayout")
            repository.fetchCapsulesAndSaveToDb()
            // Wait 2 seconds and disable refreshing animation
            doAsync { Thread.sleep(2000)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        // Force fetching capsules
        fetchButton.setOnClickListener {
            repository.fetchCapsulesAndSaveToDb()
        }
        // Delete data from capsules table
        deleteEntriesButton.setOnClickListener {
            repository.deleteAllCapsules()
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        model.refreshCapsules()
    }
}
