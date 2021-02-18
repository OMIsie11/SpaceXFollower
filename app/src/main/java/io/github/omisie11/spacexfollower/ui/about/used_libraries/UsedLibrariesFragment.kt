package io.github.omisie11.spacexfollower.ui.about.used_libraries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.fragment_used_libraries.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsedLibrariesFragment : Fragment(R.layout.fragment_used_libraries),
    UsedLibrariesAdapter.OnItemClickListener {

    private lateinit var viewAdapter: UsedLibrariesAdapter
    private val viewModel: UsedLibrariesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewAdapter = UsedLibrariesAdapter(this)
        recyclerView_libs.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }

        viewModel.getUsedLibs().observe(viewLifecycleOwner, Observer { librariesList ->
            if (librariesList.isNotEmpty()) viewAdapter.setData(librariesList)
        })
    }

    override fun onItemClicked(usedLibraryRepoUrl: String) {
        openWebUrl(usedLibraryRepoUrl)
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
