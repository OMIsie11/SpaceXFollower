package io.github.omisie11.spacexfollower.ui.upcoming_launches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.upcoming_launches_recycler_item.view.*


class UpcomingLaunchesAdapter : RecyclerView.Adapter<UpcomingLaunchesAdapter.ViewHolder>() {

    private var launchesList: List<UpcomingLaunch> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.upcoming_launches_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        launchesList.let {
            holder.flightNumberTextView.text = holder.itemView.context.resources
                .getString(R.string.flight_number, launchesList[position].flightNumber)
            holder.launchDateTextView.text = if (launchesList[position].launchDateUnix != null)
                getLocalTimeFromUnix(launchesList[position].launchDateUnix!!) else
                holder.itemView.context.getString(R.string.launch_date_null)
            holder.missionNameTextView.text = launchesList[position].missionName
            holder.launchSiteTextView.text = launchesList[position].launch_site.siteName
        }
    }

    override fun getItemCount(): Int = launchesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightNumberTextView: TextView = itemView.text_flight_number
        val launchDateTextView: TextView = itemView.text_launch_date
        val missionNameTextView: TextView = itemView.text_mission_name
        val launchSiteTextView: TextView = itemView.text_launch_site
    }

    fun setData(data: List<UpcomingLaunch>) {
        launchesList = data
        notifyDataSetChanged()
    }

}