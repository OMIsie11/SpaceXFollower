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
        if (!payloads.isNullOrEmpty()) holder.bind(payloads!![position])
    }

    override fun getItemCount(): Int = payloads?.size ?: 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val payloadIdTextView: TextView = itemView.text_payload_id
        private val nationalityTextView: TextView = itemView.text_nationality
        private val manufacturerTextView: TextView = itemView.text_manufacturer
        private val payloadTypeTextView: TextView = itemView.text_payload_type
        private val payloadMassKgTextView: TextView = itemView.text_payload_mass_kg
        private val orbitTextView: TextView = itemView.text_orbit
        private val reusedTextView: TextView = itemView.text_reused
        private val customersTextView: TextView = itemView.text_customers

        fun bind(payload: Rocket.Payload) {
            payloadIdTextView.text = payload.payload_id
            nationalityTextView.text = payload.nationality
            manufacturerTextView.text = payload.manufacturer
            payloadTypeTextView.text = payload.payload_type
            payloadMassKgTextView.text = payload.payload_mass_kg.toString()
            orbitTextView.text = payload.orbit
            reusedTextView.text =
                if (payload.reused == null || payload.reused)
                    itemView.context.getString(R.string.yes)
                else itemView.context.getString(R.string.no)
            customersTextView.text = payload.customers.joinToString()
        }
    }

    fun setData(data: List<Rocket.Payload>?) {
        payloads = data
        notifyDataSetChanged()
    }

}