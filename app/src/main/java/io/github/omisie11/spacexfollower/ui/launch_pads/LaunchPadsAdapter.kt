package io.github.omisie11.spacexfollower.ui.launch_pads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import kotlinx.android.synthetic.main.launch_pads_recycler_item.view.*

class LaunchPadsAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<LaunchPadsAdapter.ViewHolder>() {

    private var launchPadsList: List<LaunchPad> = emptyList()
    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.launch_pads_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (launchPadsList.isNotEmpty()) holder.bind(launchPadsList[position], itemClickListener)
    }

    override fun getItemCount(): Int = launchPadsList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val launchPadLocationName: TextView = itemView.text_launch_pad_location_name
        private val launchPadRegion: TextView = itemView.text_launch_pad_region
        private val launchPadStatus: TextView = itemView.text_launch_pad_status
        private val locationPin: ImageView = itemView.image_location_pin
        private val details: TextView = itemView.text_details
        private val attemptedLaunches: TextView = itemView.text_attempted_launches
        private val successfulLaunches: TextView = itemView.text_successful_launches
        private val imageExpandIndicator: ImageView = itemView.image_expand

        fun bind(launchPad: LaunchPad, itemClickListener: OnItemClickListener) {
            launchPadLocationName.text = launchPad.location.name
            launchPadRegion.text = launchPad.location.region
            launchPadStatus.text = launchPad.status
            attemptedLaunches.text = launchPad.attemptedLaunches.toString()
            successfulLaunches.text = launchPad.successfulLaunches.toString()
            details.text = launchPad.details

            // Intent to Google Maps on location pin click
            locationPin.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onLocationClicked(
                    Triple(
                        launchPad.location.latitude,
                        launchPad.location.longitude,
                        launchPad.siteNameLong ?: ""
                    )
                )
            }

            // Handle expand / collapse item
            val isExpanded = adapterPosition == expandedPosition
            itemView.item_expandable_part.visibility = if (isExpanded) View.VISIBLE else View.GONE
            itemView.isActivated = isExpanded
            itemView.setOnClickListener {
                imageExpandIndicator.setImageResource(
                    if (isExpanded) R.drawable.ic_expand_less_24dp
                    else R.drawable.ic_expand_more_24dp
                )
                expandedPosition = if (isExpanded) -1 else adapterPosition
                notifyItemChanged(adapterPosition)
            }
        }
    }

    fun setData(data: List<LaunchPad>) {
        launchPadsList = data
        notifyDataSetChanged()
    }

    // Triple represents Latitude, Longitude and name of Launch Pad location
    interface OnItemClickListener {
        fun onLocationClicked(launchPadCoordinates: Triple<Double, Double, String>)
    }
}