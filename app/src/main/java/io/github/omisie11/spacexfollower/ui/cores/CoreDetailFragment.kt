package io.github.omisie11.spacexfollower.ui.cores


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.fragment_core_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random


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

        viewModel.getCores().observe(viewLifecycleOwner, Observer<List<Core>> { cores ->
            text_core_serial.text = cores[selectedCoreId].coreSerial
            text_core_block.text = if (cores[selectedCoreId].block == null)
                "-" else cores[selectedCoreId].block.toString()
            text_core_status.text = cores[selectedCoreId].status
            text_core_launch.text = if (cores[selectedCoreId].originalLaunch.isNullOrEmpty())
                "No launch time provided" else cores[selectedCoreId].originalLaunch
            text_core_details.text = if (cores[selectedCoreId].details.isNullOrEmpty())
                "No details provided" else cores[selectedCoreId].details
            text_core_rtls_attempts.text = cores[selectedCoreId].rtlsAttempts.toString()
            text_core_rtls_landings.text = cores[selectedCoreId].rtlsLandings.toString()
            text_core_asds_attempts.text = cores[selectedCoreId].asdsAttempts.toString()
            text_core_asds_landings.text = cores[selectedCoreId].asdsLandings.toString()
            text_core_water_landing.text = when (cores[selectedCoreId].waterLandings) {
                true -> "Yes"; false -> "No"
            }
            text_core_reused.text = cores[selectedCoreId].reuseCount.toString()
        })

        // Randomly set image from resources
        val imageId = Random.nextInt(0, 5)
        val imagesArray = resources.obtainTypedArray(R.array.detail_fragment_images)
        image_core.setImageResource(
            imagesArray.getResourceId(
                imageId,
                R.drawable.detail_fragment_image_capsule_dragon
            )
        )
        imagesArray.recycle()
    }
}
