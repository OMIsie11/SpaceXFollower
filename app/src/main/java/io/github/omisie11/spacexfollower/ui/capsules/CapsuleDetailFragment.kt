package io.github.omisie11.spacexfollower.ui.capsules


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.fragment_capsule_detail.*
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

        val safeArgs = arguments?.let { CapsuleDetailFragmentArgs.fromBundle(it) }
        val selectedCapsuleId: Int = safeArgs?.itemPosition ?: 0

        viewModel.getCapsules().observe(viewLifecycleOwner, Observer { capsules ->
            text_capsule_id.text = capsules[selectedCapsuleId].capsuleSerial
        })
    }
}
