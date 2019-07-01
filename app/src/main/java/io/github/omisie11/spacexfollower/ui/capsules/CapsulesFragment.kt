package io.github.omisie11.spacexfollower.ui.capsules

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
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.util.*
import kotlinx.android.synthetic.main.fragment_capsules_recycler.*
import kotlinx.android.synthetic.main.fragment_recycler.recyclerView
import kotlinx.android.synthetic.main.fragment_recycler.swipeRefreshLayout
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CapsulesFragment : Fragment() {

    private val viewAdapter: CapsulesAdapter by inject()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val viewModel: CapsulesViewModel by viewModel()

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

        // Setup recyclerView
        viewManager = LinearLayoutManager(activity)
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            layoutManager = viewManager
            adapter = viewAdapter
        }

        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorSurface)
            setColorSchemeResources(R.color.colorSecondary)
        }

        // ViewModel setup
        viewModel.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            viewAdapter.setData(capsules)
        })

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

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Log.i("CapsulesFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCapsules()
        }

        chip_group.check(R.id.chip_serial_oldest)
        chip_group.setOnCheckedChangeListener { group, checkedId ->
            // Set clickable chips, prevent uncheck checked Chip
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i)
                chip.isClickable = chip.id != group.checkedChipId
            }

            when (checkedId) {
                R.id.chip_serial_newest -> viewModel.changeCapsulesSorting(CAPSULES_SORT_SERIAL_DESC)
                R.id.chip_serial_oldest -> viewModel.changeCapsulesSorting(CAPSULES_SORT_SERIAL_ASC)
                // -1 means chip got unchecked
                -1 -> viewModel.changeCapsulesSorting(CAPSULES_SORT_SERIAL_ASC)
            }
        }

        // Respond to user clicks on recyclerView items
        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                findNavController().navigate(
                    CapsulesFragmentDirections
                        .actionCapsulesDestToCapsuleDetailDest(position)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfCapsulesDataOld()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshCapsules()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteCapsulesData()
            viewAdapter.notifyDataSetChanged()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
