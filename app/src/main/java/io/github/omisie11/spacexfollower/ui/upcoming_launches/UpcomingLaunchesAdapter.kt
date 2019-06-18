package io.github.omisie11.spacexfollower.ui.upcoming_launches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.UpcomingLaunch
import kotlinx.android.synthetic.main.upcoming_launches_recycler_item.view.*


class UpcomingLaunchesAdapter : RecyclerView.Adapter<UpcomingLaunchesAdapter.ViewHolder>() {

    private var mLaunchesData: List<UpcomingLaunch> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.upcoming_launches_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mLaunchesData.let {
            holder.flightNumberTextView.text = mLaunchesData[position].flightNumber.toString()
            holder.launchDateTextView.text = if (mLaunchesData[position].launchDateUnix.toString().isNullOrEmpty())
                "No launch date info" else mLaunchesData[position].launchDateUnix.toString()
            holder.missionNameTextView.text = mLaunchesData[position].missionName
        }
    }

    override fun getItemCount(): Int = mLaunchesData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightNumberTextView: TextView = itemView.text_flight_number
        val launchDateTextView: TextView = itemView.text_launch_date
        val missionNameTextView: TextView = itemView.text_mission_name
    }

    fun setData(data: List<UpcomingLaunch>) {
        mLaunchesData = data
        notifyDataSetChanged()
    }

}