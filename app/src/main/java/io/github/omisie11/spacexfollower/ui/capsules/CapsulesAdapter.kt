package io.github.omisie11.spacexfollower.ui.capsules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*
import kotlinx.coroutines.*


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var mExpandedPosition: Int = -1
    private var mCapsulesData: List<Capsule> = emptyList()
    private val capsulesAdapterJob = Job()
    private val capsulesAdapterScope = CoroutineScope(Dispatchers.IO + capsulesAdapterJob)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        // Views visible all the time
        mCapsulesData.let {
            holder.capsuleIdTextView.text = mCapsulesData[position].capsuleId
            holder.capsuleSerialTextView.text = mCapsulesData[position].capsuleSerial
            holder.capsuleType.text = mCapsulesData[position].type
            holder.capsuleStatus.text = mCapsulesData[position].status
            // Views below are visible when view is expanded on click
            holder.capsuleLandings.text = "Landings: " + mCapsulesData[position].landings.toString()
            holder.capsuleMissionsTextView.text =
                    if (mCapsulesData[position].missions!!.isEmpty()) "No missions yet"
                    else mCapsulesData[position].missions.toString()
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

    override fun getItemCount(): Int = mCapsulesData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capsuleIdTextView: TextView = itemView.text_capsule_id
        val capsuleSerialTextView: TextView = itemView.text_capsule_serial
        val capsuleType: TextView = itemView.text_capsule_type
        val capsuleStatus: TextView = itemView.text_capsule_status
        val capsuleLandings: TextView = itemView.text_landings
        val capsuleMissionsTextView: TextView = itemView.text_missions
        val groupExpanded: Group = itemView.group_expand
    }

    fun setData(data: List<Capsule>) {
        // Reverse list, so by default it is sorted by NEWEST DATE
        mCapsulesData = data
        notifyDataSetChanged()
    }
}