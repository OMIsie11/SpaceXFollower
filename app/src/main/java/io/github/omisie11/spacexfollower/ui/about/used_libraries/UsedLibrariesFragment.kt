package io.github.omisie11.spacexfollower.ui.about.used_libraries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.fragment_used_libraries.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsedLibrariesFragment : Fragment(), UsedLibrariesAdapter.OnItemClickListener {

    private lateinit var viewAdapter: UsedLibrariesAdapter
    private val viewModel: UsedLibrariesViewModel by viewModel()
    private val usedLibsJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + usedLibsJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_used_libraries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewAdapter = UsedLibrariesAdapter(this)
        recyclerView_libs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }

        uiScope.launch {
            val usedLibsList = async(Dispatchers.Default) {
                getUsedLibraries()
            }
            viewModel.setUsedLibs(usedLibsList.await())
        }

        viewModel.getUsedLibs().observe(viewLifecycleOwner, Observer { librariesList ->
            if (librariesList.isNotEmpty()) viewAdapter.setData(librariesList)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView_libs.adapter = null

        usedLibsJob.cancel()
    }

    override fun onItemClicked(usedLibraryRepoUrl: String) {
        openWebUrl(usedLibraryRepoUrl)
    }

    // Move this function to ViewModel if there is safe way to access RAW res from there
    private fun getUsedLibraries(): List<UsedLibrary> {
        val type = object : TypeToken<List<UsedLibrary>>() {}.type
        val objectString: String =
            activity!!.resources.openRawResource(R.raw.used_libraries).bufferedReader()
                .use { it.readText() }
        return Gson().fromJson(objectString, type)
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
