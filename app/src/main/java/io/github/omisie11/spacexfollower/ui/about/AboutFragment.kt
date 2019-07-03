package io.github.omisie11.spacexfollower.ui.about


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_planet.setOnClickListener {
            Toast.makeText(
                activity, getString(R.string.lottie_rocket_attribution), Toast.LENGTH_LONG
            ).show()
        }

        chip_github.setOnClickListener { openWebUrl(getString(R.string.github_url_omisie11)) }
        chip_twitter.setOnClickListener { openWebUrl(getString(R.string.twitter_url_omisie11)) }
    }

    private fun openWebUrl(urlAddress: String) {
        if (urlAddress.isNotEmpty()) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress)))
    }
}
