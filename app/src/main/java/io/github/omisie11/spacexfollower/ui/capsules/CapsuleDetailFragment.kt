package io.github.omisie11.spacexfollower.ui.capsules


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.fragment_capsule_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random


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
        val selectedCapsuleId: Int = safeArgs?.itemId ?: 0

        viewModel.getCapsules().observe(viewLifecycleOwner, Observer<List<Capsule>> { capsules ->
            text_capsule_serial.text = capsules[selectedCapsuleId].capsuleSerial
            text_capsule_type.text = capsules[selectedCapsuleId].type
            text_capsule_status.text = capsules[selectedCapsuleId].status
            text_capsule_launch.text = if (capsules[selectedCapsuleId].originalLaunchUnix != null)
                getLocalTimeFromUnix(capsules[selectedCapsuleId].originalLaunchUnix!!) else
                "No launch date info"
            text_capsule_details.text = if (capsules[selectedCapsuleId].details.isNullOrEmpty())
                "No details provided" else capsules[selectedCapsuleId].details
            text_capsule_landings.text = capsules[selectedCapsuleId].landings.toString()
            text_capsule_reused.text = capsules[selectedCapsuleId].reuseCount.toString()
        })

        // Randomly set image from resources
        val imageId = Random.nextInt(0, 5)
        val imagesArray = resources.obtainTypedArray(R.array.detail_fragment_images)
        image_capsule.setImageResource(
            imagesArray.getResourceId(
                imageId,
                R.drawable.detail_fragment_image_capsule_dragon
            )
        )
        imagesArray.recycle()
    }
}
