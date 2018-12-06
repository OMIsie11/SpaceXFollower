package io.github.omisie11.spacexfollower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: CapsulesAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val repository: SpaceRepository by inject()
    val model: CapsulesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = CapsulesAdapter()

        capsulesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


        //val model = ViewModelProviders.of(this).get(CapsulesViewModel::class.java)
        model.getCapsules().observe(this, Observer<List<Capsule>>{capsules ->
            viewAdapter.setData(capsules)
        })

        fetchButton.setOnClickListener {
            repository.fetchCapsulesAndSaveToDb()
        }

        deleteEntriesButton.setOnClickListener {
            repository.deleteAllCapsules()
        }
    }
}
