package io.github.omisie11.spacexfollower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var mExpandedPosition : Int = -1

    private var mCapsulesData: List<Capsule>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        // Views visible all the time
        holder.capsuleIdTextView.text = mCapsulesData!![position].capsuleId
        holder.capsuleSerialTextView.text = mCapsulesData!![position].capsuleSerial
        // Views below are visible when view is expanded on click
        holder.capsuleMissionsTextView.text = mCapsulesData!![position].missions.toString()

        // Variable for storing info if view is expanded
        val isExpanded = position == mExpandedPosition
        holder.capsuleMissionsTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            // Expand view on click
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return mCapsulesData?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capsuleIdTextView: TextView = itemView.findViewById(R.id.capsuleIdTV)
        val capsuleSerialTextView: TextView = itemView.findViewById(R.id.capsuleSerialTV)
        val capsuleMissionsTextView: TextView = itemView.findViewById(R.id.missionsTV)
    }

    fun setData(data: List<Capsule>?) {
        // Reverse list, so by default it is sorted by NEWEST DATE
        mCapsulesData = data?.toMutableList()?.asReversed()
        notifyDataSetChanged()
    }
}