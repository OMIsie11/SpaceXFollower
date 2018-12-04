package io.github.omisie11.spacexfollower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.data.model.Capsule


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var mCapsulesData: List<Capsule>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.capsuleIdTextView.text = mCapsulesData!![position].capsuleId
        holder.capsuleSerialTextView.text = mCapsulesData!![position].capsuleSerial
        holder.capsuleMissionsTextView.text = mCapsulesData!![position].missions.toString()
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