package io.github.omisie11.spacexfollower.ui.cores

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.bottom_sheet_attribution.view.*
import kotlinx.android.synthetic.main.fragment_core_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.random.Random

class CoreDetailFragment : Fragment() {

    private val viewModel: CoresViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            val core = cores[selectedCoreId]

            text_core_name.text = core.coreSerial
            text_core_block.text = if (core.block == null) "-" else core.block.toString()
            text_core_status.text = core.status
            text_lib_desc.text = if (core.originalLaunchUnix != null)
                getLocalTimeFromUnix(core.originalLaunchUnix) else
                getString(R.string.launch_date_null)
            text_core_details.text = if (core.details.isNullOrEmpty())
                getString(R.string.details_null) else core.details
            text_core_rtls_attempts.text = core.rtlsAttempts.toString()
            text_core_rtls_landings.text = core.rtlsLandings.toString()
            text_core_asds_attempts.text = core.asdsAttempts.toString()
            text_core_asds_landings.text = core.asdsLandings.toString()
            text_core_water_landing.text = when (core.waterLandings) {
                true -> getString(R.string.yes); false -> getString(R.string.no)
            }
            text_core_reused.text = core.reuseCount.toString()
        })

        // Randomly set image from resources
        val imageId = Random.nextInt(0, 5)
        val imagesArray = resources.obtainTypedArray(R.array.detail_fragment_images)
        Picasso.get()
            .load(
                imagesArray.getResourceId(
                    imageId,
                    R.drawable.detail_fragment_image_capsule_dragon
                )
            )
            .into(image_core)
        imagesArray.recycle()

        val attributionBottomSheetDialog = BottomSheetDialog(activity!!)
        val sheetView = activity!!.layoutInflater.inflate(
            R.layout.bottom_sheet_attribution,
            null
        )
        sheetView.text_attribution.text = getString(R.string.photos_attribution_spacex)
        attributionBottomSheetDialog.setContentView(sheetView)

        image_core.setOnClickListener { attributionBottomSheetDialog.show() }
        sheetView.setOnClickListener { openWebUrl(getString(R.string.photos_spacex_url)) }
    }

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlAddress)
            )
        )
    }
}
