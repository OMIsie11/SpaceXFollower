package io.github.omisie11.spacexfollower.ui.launch_pads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.LaunchPad
import kotlinx.android.synthetic.main.launch_pads_recycler_item.view.*

class LaunchPadsAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<LaunchPadsAdapter.ViewHolder>() {

    private var launchPadsList: List<LaunchPad> = emptyList()

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
        private val launchPadId: TextView = itemView.text_launch_pad_id
        private val launchPadLocationName: TextView = itemView.text_launch_pad_location_name
        private val launchPadRegion: TextView = itemView.text_launch_pad_region
        private val launchPadStatus: TextView = itemView.text_launch_pad_status

        fun bind(launchPad: LaunchPad, itemClickListener: OnItemClickListener) {
            launchPadId.text = launchPad.siteId
            launchPadLocationName.text = launchPad.location.name
            launchPadRegion.text = launchPad.location.region
            launchPadStatus.text = launchPad.status

            itemView.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onItemClicked(
                    Triple(
                        launchPad.location.latitude,
                        launchPad.location.longitude,
                        launchPad.siteNameLong ?: ""
                    )
                )
            }
        }
    }

    fun setData(data: List<LaunchPad>) {
        launchPadsList = data
        notifyDataSetChanged()
    }

    // Triple represents Latitude, Longitude and name of Launch Pad location
    interface OnItemClickListener {
        fun onItemClicked(launchPadCoordinates: Triple<Double, Double, String>)
    }
}