package io.github.omisie11.spacexfollower.ui.next_launch


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.NextLaunch
import io.github.omisie11.spacexfollower.util.NumbersUtils
import kotlinx.android.synthetic.main.fragment_next_launch.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class NextLaunchFragment : Fragment() {

    private val viewModel: NextLaunchViewModel by viewModel()
    private val numberUtils: NumbersUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNextLaunchInfo().observe(viewLifecycleOwner, Observer<NextLaunch> { nextLaunch ->
            if (nextLaunch != null) {
                text_flight_number.text = nextLaunch.flightNumber.toString()
                text_mission_name.text = nextLaunch.missionName
                text_launch_date.text = numberUtils.getLocalTimeFromUnix(nextLaunch.launchDateUnix)
            }
        })

        // Observe if data is refreshing and show/hide loading indicator
        viewModel.getNextLaunchLoadingStatus().observe(viewLifecycleOwner,
            Observer<Boolean> { isNextLaunchInfoRefreshing ->
                swipeRefreshLayout.isRefreshing = isNextLaunchInfoRefreshing
            })

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(viewLifecycleOwner, Observer { text ->
            text?.let {
                Snackbar.make(swipeRefreshLayout, text, Snackbar.LENGTH_LONG).setAction(
                    getString(R.string.snackbar_action_retry), View.OnClickListener {
                        viewModel.refreshNextLaunch()
                    }).show()
                viewModel.onSnackbarShown()
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            Log.i("NextLaunchFragment", "onRefresh called from SwipeRefreshLayout")
            viewModel.refreshNextLaunch()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshIfNextLaunchDataOld()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshNextLaunch()
            true
        }
        R.id.action_delete -> {
            viewModel.deleteNextLaunchData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}