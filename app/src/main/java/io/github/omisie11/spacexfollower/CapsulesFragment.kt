package io.github.omisie11.spacexfollower

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.viewmodel.CapsulesViewModel
import kotlinx.android.synthetic.main.fragment_capsules.*
import kotlinx.android.synthetic.main.fragment_capsules.view.*
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
        val rootView = inflater.inflate(R.layout.fragment_capsules, container, false)

        // Setup recyclerView
        viewManager = LinearLayoutManager(activity)
        rootView.capsulesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel setup
        model.getCapsules().observe(this, Observer<List<Capsule>> { capsules ->
            viewAdapter.setData(capsules)
        })

        // Force fetching capsules
        fetchButton.setOnClickListener {
            repository.fetchCapsulesAndSaveToDb()
        }
        // Delete data from capsules table
        deleteEntriesButton.setOnClickListener {
            repository.deleteAllCapsules()
        }
    }
}
