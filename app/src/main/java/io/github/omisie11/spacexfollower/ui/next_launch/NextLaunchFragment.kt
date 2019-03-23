package io.github.omisie11.spacexfollower.ui.next_launch


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.NextLaunch
import kotlinx.android.synthetic.main.fragment_next_launch.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class NextLaunchFragment : Fragment() {

    private val viewModel: NextLaunchViewModel by viewModel()

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
                text_next_launch_mission_name.text = nextLaunch.missionName
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
}
