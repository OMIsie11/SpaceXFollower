package io.github.omisie11.spacexfollower.ui.capsules


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import io.github.omisie11.spacexfollower.R
import org.koin.androidx.viewmodel.ext.android.viewModel


class CapsuleDetailFragment : Fragment() {

    private val viewModel: CapsulesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capsule_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments?.let { CapsuleDetailFragmentArgs.fromBundle(it) }

        Toast.makeText(context, "Passed ID: ${args?.itemPosition}", Toast.LENGTH_SHORT).show()
    }
}
