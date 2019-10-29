package io.github.omisie11.spacexfollower.ui.cores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.bottom_sheet_cores_sorting.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CoresSortingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: CoresViewModel by sharedViewModel()
    private lateinit var selectedSorting: CoresViewModel.CoresSortingOrder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_cores_sorting, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCoresSortingOrder().observe(viewLifecycleOwner, Observer { sortingOrder ->
            selectedSorting = sortingOrder
        })

        button_sorting_serial_newest.setOnClickListener {
            viewModel.setCoresSortingOrder(CoresViewModel.CoresSortingOrder.BY_SERIAL_NEWEST)
            dismiss()
        }
        button_sorting_serial_oldest.setOnClickListener {
            viewModel.setCoresSortingOrder(CoresViewModel.CoresSortingOrder.BY_SERIAL_OLDEST)
            dismiss()
        }

    }

    companion object {
        const val TAG = "bottom_sheet_cores_sorting"
    }
}