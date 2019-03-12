package io.github.omisie11.spacexfollower.ui.cores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.cores_recycler_item.view.*

class CoresAdapter : RecyclerView.Adapter<CoresAdapter.ViewHolder>() {

    private var mCoresData: List<Core> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cores_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mCoresData.let {
            holder.coreSerialTextView.text = mCoresData[position].coreSerial
            holder.coreLaunchTextView.text = if (mCoresData[position].originalLaunch.isNullOrEmpty())
                "No launch date info" else mCoresData[position].originalLaunch
            holder.coreStatusTextView.text = mCoresData[position].status
        }
    }

    override fun getItemCount(): Int = mCoresData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coreSerialTextView: TextView = itemView.text_core_serial
        val coreLaunchTextView: TextView = itemView.text_core_launch
        val coreStatusTextView: TextView = itemView.text_core_status
    }

    fun setData(data: List<Core>) {
        mCoresData = data
        notifyDataSetChanged()
    }
}