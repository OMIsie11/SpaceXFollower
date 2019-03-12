package io.github.omisie11.spacexfollower.ui.capsules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var mCapsulesData: List<Capsule> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mCapsulesData.let {
            holder.capsuleSerialTextView.text = mCapsulesData[position].capsuleSerial
            holder.capsuleLaunchTextView.text = if (mCapsulesData[position].originalLaunch.isNullOrEmpty())
                "No launch date info" else mCapsulesData[position].originalLaunch
            holder.capsuleStatusTextView.text = mCapsulesData[position].status
            holder.capsuleTypeTextView.text = mCapsulesData[position].type
        }
    }

    override fun getItemCount(): Int = mCapsulesData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capsuleSerialTextView: TextView = itemView.text_capsule_serial
        val capsuleLaunchTextView: TextView = itemView.text_capsule_launch
        val capsuleStatusTextView: TextView = itemView.text_capsule_status
        val capsuleTypeTextView: TextView = itemView.text_capsule_type
    }

    fun setData(data: List<Capsule>) {
        mCapsulesData = data
        notifyDataSetChanged()
    }
}