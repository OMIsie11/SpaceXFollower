package io.github.omisie11.spacexfollower.ui.upcoming_launches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.UpcomingLaunch


class UpcomingLaunchesAdapter : RecyclerView.Adapter<UpcomingLaunchesAdapter.ViewHolder>() {

    private var mLaunchesData: List<UpcomingLaunch> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mLaunchesData.let {
        //    holder.capsuleSerialTextView.text = mCapsulesData[position].capsuleSerial
          //  holder.capsuleLaunchTextView.text = if (mCapsulesData[position].originalLaunch.isNullOrEmpty())
          //      "No launch date info" else mCapsulesData[position].originalLaunch
          //  holder.capsuleStatusTextView.text = mCapsulesData[position].status
          //  holder.capsuleTypeTextView.text = mCapsulesData[position].type
        }
    }

    override fun getItemCount(): Int = mLaunchesData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      //  val capsuleSerialTextView: TextView = itemView.text_capsule_serial
     //   val capsuleLaunchTextView: TextView = itemView.text_capsule_launch
     //   val capsuleStatusTextView: TextView = itemView.text_capsule_status
     //   val capsuleTypeTextView: TextView = itemView.text_capsule_type
    }

    fun setData(data: List<UpcomingLaunch>) {
        mLaunchesData = data
        notifyDataSetChanged()
    }

}