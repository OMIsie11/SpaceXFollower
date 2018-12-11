package io.github.omisie11.spacexfollower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*

class CoresAdapter : RecyclerView.Adapter<CoresAdapter.ViewHolder>() {

    private var mExpandedPosition: Int = -1
    private var mCoresData: List<Core> = emptyList()

    // ToDo change item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        // Views visible all the time
        mCoresData.let {
            holder.coreSerialTextView.text = mCoresData[position].coreSerial
            holder.coreBlock.text = mCoresData[position].block.toString()
            holder.coreStatus.text = mCoresData[position].status
            // Views below are visible when view is expanded on click
            holder.coreLandings.text = "Water landings: " + mCoresData[position].waterLandings.toString()
            holder.coreMissionsTextView.text =
                    if (mCoresData[position].missions!!.isEmpty()) "No missions yet"
                    else mCoresData[position].missions.toString()
        }

        // Variable for storing info if view is expanded
        val isExpanded = position == mExpandedPosition
        holder.groupExpanded.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            // Expand view on click
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = mCoresData.size

    // Todo after item layout change, refactor finding views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coreSerialTextView: TextView = itemView.text_capsule_serial
        val coreBlock: TextView = itemView.text_capsule_type
        val coreStatus: TextView = itemView.text_capsule_status
        val coreLandings: TextView = itemView.text_landings
        val coreMissionsTextView: TextView = itemView.text_missions
        val groupExpanded: Group = itemView.group_expand
    }

    fun setData(data: List<Core>) {
        // Reverse list, so by default it is sorted by NEWEST DATE
        mCoresData = data
        notifyDataSetChanged()
    }
}