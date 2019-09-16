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
        if (launchesList.isNotEmpty()) holder.bind(launchesList[position])
    }

    override fun getItemCount(): Int = launchesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flightNumberTextView: TextView = itemView.text_flight_number
        private val launchDateTextView: TextView = itemView.text_launch_date
        private val missionNameTextView: TextView = itemView.text_mission_name
        private val launchSiteTextView: TextView = itemView.text_launch_site

        fun bind(launch: UpcomingLaunch) {
            flightNumberTextView.text = itemView.context.resources.getString(
                R.string.flight_number_template,
                launch.flightNumber
            )
            launchDateTextView.text =
                if (launch.launchDateUnix != null) getLocalTimeFromUnix(launch.launchDateUnix) else
                    itemView.context.getString(R.string.launch_date_null)
            missionNameTextView.text = launch.missionName
            launchSiteTextView.text = launch.launch_site.siteName
        }
    }

    fun setData(data: List<UpcomingLaunch>) {
        launchesList = data
        notifyDataSetChanged()
    }

}