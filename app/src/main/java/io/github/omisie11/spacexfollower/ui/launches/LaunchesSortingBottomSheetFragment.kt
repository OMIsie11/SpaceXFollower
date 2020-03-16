package io.github.omisie11.spacexfollower.ui.launches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.bottom_sheet_launches_sorting.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesSortingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: LaunchesViewModel by sharedViewModel()
    private lateinit var selectedSorting: LaunchesViewModel.LaunchesSortingOrder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.bottom_sheet_launches_sorting, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLaunchesSortingOrder().observe(viewLifecycleOwner, Observer { sortingOrder ->
            selectedSorting = sortingOrder

            when (selectedSorting) {
                LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_NEWEST ->
                    radio_group_sorting.check(radio_button_flight_number_newest.id)
                LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_OLDEST ->
                    radio_group_sorting.check(radio_button_flight_number_oldest.id)
            }
        })

        radio_group_sorting.check(radio_button_flight_number_newest.id)
        radio_group_sorting.setOnCheckedChangeListener { group, checkedId ->
            when (group.checkedRadioButtonId) {
                radio_button_flight_number_newest.id -> {
                    viewModel.setLaunchesSortingOrder(
                        LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_NEWEST
                    )
                }
                radio_button_flight_number_oldest.id -> {
                    viewModel.setLaunchesSortingOrder(
                        LaunchesViewModel.LaunchesSortingOrder.BY_FLIGHT_NUMBER_OLDEST
                    )
                }
            }
            if (isVisible) dismiss()
        }
    }

    companion object {
        const val TAG = "bottom_sheet_launches_sorting"
    }
}
