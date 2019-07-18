package io.github.omisie11.spacexfollower.ui.about


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.bottom_sheet_attribution.view.*
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attributionBottomSheetDialog = BottomSheetDialog(activity!!)
        val sheetView = activity!!.layoutInflater.inflate(R.layout.bottom_sheet_attribution, null)
        attributionBottomSheetDialog.setContentView(sheetView)

        image_planet.setOnClickListener { attributionBottomSheetDialog.show() }
        sheetView.text_attribution.setOnClickListener { openWebUrl(getString(R.string.lottie_files_url_dongdona)) }

        chip_github.setOnClickListener { openWebUrl(getString(R.string.github_url_omisie11)) }
        chip_twitter.setOnClickListener { openWebUrl(getString(R.string.twitter_url_omisie11)) }

        card_attribution_dongdona.setOnClickListener { openWebUrl(getString(R.string.lottie_files_url_dongdona)) }
        card_attribution_spacex_photos.setOnClickListener { openWebUrl(getString(R.string.photos_spacex_url)) }
        card_attribution_space_api.setOnClickListener { openWebUrl(getString(R.string.space_api_url)) }
    }

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress)))
    }
}
