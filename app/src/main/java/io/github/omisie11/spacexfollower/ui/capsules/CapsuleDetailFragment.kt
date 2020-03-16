package io.github.omisie11.spacexfollower.ui.capsules

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.squareup.picasso.Picasso
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlin.random.Random
import kotlinx.android.synthetic.main.bottom_sheet_attribution.view.*
import kotlinx.android.synthetic.main.fragment_capsule_detail.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CapsuleDetailFragment : Fragment() {

    private val viewModel: CapsulesViewModel by sharedViewModel()

    private val args: CapsuleDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capsule_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shared_container_end_view.transitionName = args.itemId.toString()
        val selectedCapsuleId: Int = args.itemId

        viewModel.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            val capsule = capsules[selectedCapsuleId]
            text_capsule_serial.text = capsule.capsuleSerial
            text_capsule_type.text = capsule.type
            text_capsule_status.text = capsule.status
            text_capsule_launch.text = if (capsule.originalLaunchUnix != null)
                getLocalTimeFromUnix(capsule.originalLaunchUnix) else
                getString(R.string.launch_date_null)
            text_capsule_details.text = if (capsule.details.isNullOrEmpty())
                getString(R.string.details_null) else capsule.details
            text_capsule_landings.text = capsule.landings.toString()
            text_capsule_reused.text = capsule.reuseCount.toString()
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
            .into(image_capsule)
        imagesArray.recycle()

        val attributionBottomSheetDialog = BottomSheetDialog(activity!!)
        val sheetView = activity!!.layoutInflater.inflate(
            R.layout.bottom_sheet_attribution,
            null
        )
        sheetView.text_attribution.text = getString(R.string.photos_attribution_spacex)
        attributionBottomSheetDialog.setContentView(sheetView)

        image_capsule.setOnClickListener { attributionBottomSheetDialog.show() }
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
