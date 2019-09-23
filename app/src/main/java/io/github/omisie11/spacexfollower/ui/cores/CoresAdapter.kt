package io.github.omisie11.spacexfollower.ui.cores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.cores_recycler_item.view.*

class CoresAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<CoresAdapter.ViewHolder>() {

    private var coresList: List<Core> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cores_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (coresList.isNotEmpty()) holder.bind(coresList[position], itemClickListener)
    }

    override fun getItemCount(): Int = coresList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coreBlockTextView: TextView = itemView.text_core_block
        private val coreSerialTextView: TextView = itemView.text_lib_name
        private val coreLaunchTextView: TextView = itemView.text_lib_desc
        private val coreStatusTextView: TextView = itemView.text_core_status

        fun bind(core: Core, itemClickListener: OnItemClickListener) {
            coreBlockTextView.text = if (core.block != null)
                itemView.context.resources.getString(R.string.core_block, core.block) else
                itemView.context.resources.getString(R.string.core_block_null)
            coreSerialTextView.text = core.coreSerial
            coreLaunchTextView.text = if (core.originalLaunchUnix != null)
                getLocalTimeFromUnix(core.originalLaunchUnix) else
                itemView.context.getString(R.string.launch_date_null)
            coreStatusTextView.text = core.status

            itemView.setOnClickListener {
                if (adapterPosition != -1) itemClickListener.onItemClicked(
                    coresList.indexOf(core)
                )
            }
        }
    }

    fun setData(data: List<Core>) {
        coresList = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(coreIndex: Int)
    }
}