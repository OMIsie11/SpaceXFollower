package io.github.omisie11.spacexfollower.ui.upcoming_launches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.Rocket
import kotlinx.android.synthetic.main.payload_recycler_item.view.*

class PayloadsRecyclerAdapter : RecyclerView.Adapter<PayloadsRecyclerAdapter.ViewHolder>() {

    private var payloads: List<Rocket.Payload>? = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.payload_recycler_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!payloads.isNullOrEmpty()) payloads.let {
            holder.payloadIdTextView.text = payloads!![position].payload_id
            holder.nationalityTextView.text = payloads!![position].nationality
            holder.manufacturerTextView.text = payloads!![position].manufacturer
            holder.payloadTypeTextView.text = payloads!![position].payload_type
            holder.payloadMassKgTextView.text = payloads!![position].payload_mass_kg.toString()
            holder.orbitTextView.text = payloads!![position].orbit
            holder.reusedTextView.text =
                if (payloads!![position].reused == null || payloads!![position].reused!!)
                    holder.itemView.context.getString(R.string.yes)
                else holder.itemView.context.getString(
                    R.string.no
                )

            holder.customersTextView.text = payloads!![position].customers.joinToString()
        }
    }

    override fun getItemCount(): Int = payloads?.size ?: 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val payloadIdTextView: TextView = itemView.text_payload_id
        val nationalityTextView: TextView = itemView.text_nationality
        val manufacturerTextView: TextView = itemView.text_manufacturer
        val payloadTypeTextView: TextView = itemView.text_payload_type
        val payloadMassKgTextView: TextView = itemView.text_payload_mass_kg
        val orbitTextView: TextView = itemView.text_orbit
        val reusedTextView: TextView = itemView.text_reused
        val customersTextView: TextView = itemView.text_customers
    }

    fun setData(data: List<Rocket.Payload>?) {
        payloads = data
        notifyDataSetChanged()
    }

}