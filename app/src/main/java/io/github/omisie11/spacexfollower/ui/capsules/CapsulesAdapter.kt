package io.github.omisie11.spacexfollower.ui.capsules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var capsulesList: List<Capsule> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        capsulesList.let {
            holder.capsuleSerialTextView.text = capsulesList[position].capsuleSerial
            holder.capsuleLaunchTextView.text = if (capsulesList[position].originalLaunchUnix != null)
                getLocalTimeFromUnix(capsulesList[position].originalLaunchUnix!!) else
                holder.itemView.context.getString(R.string.launch_date_null)
            holder.capsuleStatusTextView.text = capsulesList[position].status
            holder.capsuleTypeTextView.text = capsulesList[position].type
        }
    }

    override fun getItemCount(): Int = capsulesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capsuleSerialTextView: TextView = itemView.text_capsule_serial
        val capsuleLaunchTextView: TextView = itemView.text_capsule_launch
        val capsuleStatusTextView: TextView = itemView.text_capsule_status
        val capsuleTypeTextView: TextView = itemView.text_capsule_type
    }

    fun setData(data: List<Capsule>) {
        capsulesList = data
        notifyDataSetChanged()
    }
}