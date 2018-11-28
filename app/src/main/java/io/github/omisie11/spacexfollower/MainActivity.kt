package io.github.omisie11.spacexfollower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // val model = ViewModelProviders.of(this).get(CapsulesViewModel::class.java)
        //model.getCapsules().observe(this, Observer<List<Capsule>>{capsule ->
         //   capsuleTextView?.text = capsule.get(0).capsuleSerial
        //})
    }
}
