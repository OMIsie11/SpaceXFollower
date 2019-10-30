package io.github.omisie11.spacexfollower.ui.capsules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.bottom_sheet_capsules_sorting.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CapsulesSortingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: CapsulesViewModel by sharedViewModel()
    private lateinit var selectedSorting: CapsulesViewModel.CapsulesSortingOrder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_capsules_sorting, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCapsulesSortingOrder().observe(viewLifecycleOwner, Observer { sortingOrder ->
            selectedSorting = sortingOrder

            when (selectedSorting) {
                CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_NEWEST -> radio_group_sorting.check(
                    radio_button_serial_newest.id
                )
                CapsulesViewModel.CapsulesSortingOrder.BY_SERIAL_OLDEST -> radio_group_sorting.check(
                    radio_button_serial_oldest.id
                )
            }
        })

        radio_group_sorting.check(radio_button_serial_newest.id)
        radio_group_sorting.setOnCheckedChangeListener { group, checkedId ->
            when (group.checkedRadioButtonId) {
                radio_button_serial_newest.id -> {
                    viewModel.setCapsulesSortingOrder(
                        CapsulesViewModel
                            .CapsulesSortingOrder.BY_SERIAL_NEWEST
                    )
                }
                radio_button_serial_oldest.id -> {
                    viewModel.setCapsulesSortingOrder(
                        CapsulesViewModel
                            .CapsulesSortingOrder.BY_SERIAL_OLDEST
                    )
                }
            }
            if (isVisible) dismiss()
        }
    }

    companion object {
        const val TAG = "bottom_sheet_capsules_sorting"
    }
}