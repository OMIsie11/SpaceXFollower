package io.github.omisie11.spacexfollower.ui.launches

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.launches_recycler_item.view.*

class LaunchesAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<LaunchesAdapter.ViewHolder>() {

    private var launchesList: List<Launch> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.launches_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (launchesList.isNotEmpty()) holder.bind(launchesList[position], itemClickListener)
    }

    override fun getItemCount(): Int = launchesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flightNumberTextView: TextView = itemView.text_flight_number
        private val launchDateTextView: TextView = itemView.text_launch_date
        private val missionNameTextView: TextView = itemView.text_mission_name
        private val missionPatchImage: ImageView = itemView.image_mission_patch

        fun bind(launch: Launch, itemClickListener: OnItemClickListener) {
            flightNumberTextView.text = itemView.context.resources.getString(
                R.string.flight_number_template,
                launch.flightNumber
            )
            launchDateTextView.text =
                if (launch.launchDateUnix != null) getLocalTimeFromUnix(launch.launchDateUnix) else
                    itemView.context.getString(R.string.launch_date_null)
            missionNameTextView.text = launch.missionName
            missionPatchImage.apply {
                if (!launch.links.missionPatchSmall.isNullOrBlank()) {
                    load(launch.links.missionPatchSmall) {
                        placeholder(
                            ColorDrawable(
                                ContextCompat.getColor(
                                    itemView.context, R.color.horizontalDividerBackground
                                )
                            )
                        )
                    }
                }
            }

            itemView.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onItemClicked(
                    launchesList.indexOf(launch)
                )
            }
        }
    }

    fun setData(data: List<Launch>) {
        launchesList = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(launchIndex: Int)
    }
}