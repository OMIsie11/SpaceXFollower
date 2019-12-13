package io.github.omisie11.spacexfollower.ui.capsules

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.ui.capsules.CapsulesViewModel.CapsulesSortingOrder
import kotlinx.android.synthetic.main.fragment_recycler.recyclerView
import kotlinx.android.synthetic.main.fragment_recycler.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_recycler_sorting.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CapsulesFragment : Fragment(), CapsulesAdapter.OnItemClickListener {

    private lateinit var viewAdapter: CapsulesAdapter
    private val viewModel: CapsulesViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recycler_sorting, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recyclerView
        viewAdapter = CapsulesAdapter(this)
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
        viewModel.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            if (capsules != null) viewAdapter.setData(capsules)
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getCapsulesLoadingStatus()
            .observe(viewLifecycleOwner, Observer<Boolean> { areCapsulesRefreshing ->
                swipeRefreshLayout.isRefreshing = areCapsulesRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry)
                ) {
                    viewModel.refreshCapsules()
                }.show()
                viewModel.onSnackbarShown()
            }
        })

        viewModel.getCapsulesSortingOrder().observe(viewLifecycleOwner, Observer { sortingOrder ->
            button_sorting.text = when (sortingOrder) {
                CapsulesSortingOrder.BY_SERIAL_NEWEST -> getString(R.string.serial_newest)
                CapsulesSortingOrder.BY_SERIAL_OLDEST -> getString(R.string.serial_oldest)
                CapsulesSortingOrder.BY_STATUS_ACTIVE_FIRST -> getString(R.string.status_active_first)
                CapsulesSortingOrder.BY_STATUS_ACTIVE_LAST -> getString(R.string.status_active_last)
                else -> getString(R.string.serial_oldest)
            }
        })

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            Timber.i("onRefresh called from SwipeRefreshLayout")
            viewModel.refreshCapsules()
        }

        button_sorting.setOnClickListener {
            val sortingBottomSheetDialog = CapsulesSortingBottomSheetFragment()

            sortingBottomSheetDialog.show(
                childFragmentManager,
                CapsulesSortingBottomSheetFragment.TAG
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

    override fun onResume() {
        super.onResume()
        // Fetch new data if last fetch was long ago
        viewModel.refreshIfCapsulesDataOld()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    // Respond to user clicks on recyclerView items
    override fun onItemClicked(capsuleIndex: Int) {
        findNavController().navigate(
            CapsulesFragmentDirections
                .actionCapsulesDestToCapsuleDetailDest(capsuleIndex)
        )
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
            recyclerView.adapter?.notifyDataSetChanged()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
