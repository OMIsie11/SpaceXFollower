package io.github.omisie11.spacexfollower.ui.cores


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.fragment_core_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CoreDetailFragment : Fragment() {

    private val viewModel: CoresViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_core_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs = arguments?.let { CoreDetailFragmentArgs.fromBundle(it) }
        val selectedCoreId: Int = safeArgs?.itemId ?: 0

        viewModel.getCores().observe(viewLifecycleOwner, Observer { cores ->
            text_core_serial.text = cores[selectedCoreId].coreSerial
        })
    }
}
