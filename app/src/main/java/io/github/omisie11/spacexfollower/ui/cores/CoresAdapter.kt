package io.github.omisie11.spacexfollower.ui.cores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*

class CoresAdapter : RecyclerView.Adapter<CoresAdapter.ViewHolder>() {

    private var mCoresData: List<Core> = emptyList()

    // ToDo change item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mCoresData.let {
            holder.coreSerialTextView.text = mCoresData[position].coreSerial
            holder.coreBlock.text = mCoresData[position].block.toString()
            holder.coreStatus.text = mCoresData[position].status
        }
    }

    override fun getItemCount(): Int = mCoresData.size

    // Todo after item layout change, refactor finding views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coreSerialTextView: TextView = itemView.text_capsule_serial
        val coreBlock: TextView = itemView.text_capsule_type
        val coreStatus: TextView = itemView.text_capsule_status
        //val coreLandings: TextView = itemView.text_landings
        //val coreMissionsTextView: TextView = itemView.text_missions
        //val groupExpanded: Group = itemView.group_expand
    }

    fun setData(data: List<Core>) {
        mCoresData = data
        notifyDataSetChanged()
    }
}