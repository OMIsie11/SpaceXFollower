package io.github.omisie11.spacexfollower.ui.capsules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*

class CapsulesAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var capsulesList: List<Capsule> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (capsulesList.isNotEmpty()) holder.bind(capsulesList[position], itemClickListener)
    }

    override fun getItemCount(): Int = capsulesList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val capsuleSerialTextView: TextView = itemView.text_capsule_serial
        private val capsuleLaunchTextView: TextView = itemView.text_capsule_launch
        private val capsuleStatusTextView: TextView = itemView.text_capsule_status
        private val capsuleTypeTextView: TextView = itemView.text_capsule_type

        fun bind(capsule: Capsule, itemClickListener: OnItemClickListener) {
            // set transition name for shared element container transition
            itemView.transitionName = capsulesList.indexOf(capsule).toString()

            capsuleSerialTextView.text = capsule.capsuleSerial
            capsuleLaunchTextView.text = if (capsule.originalLaunchUnix != null)
                getLocalTimeFromUnix(capsule.originalLaunchUnix) else
                itemView.context.getString(R.string.launch_date_null)
            capsuleStatusTextView.text = capsule.status
            capsuleTypeTextView.text = capsule.type

            itemView.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onItemClicked(
                    capsulesList.indexOf(capsule),
                    itemView
                )
            }
        }
    }

    fun setData(data: List<Capsule>) {
        capsulesList = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(capsuleIndex: Int, itemView: View)
    }
}