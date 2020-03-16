package io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.launch_payload_recycler_item.view.*

open class PayloadItem(
    private val payloadId: String?,
    private val nationality: String?,
    private val manufacturer: String?,
    private val payloadType: String?,
    private val payloadMassKg: Double?,
    private val orbit: String?,
    private val reused: Boolean?,
    private val customers: List<String>
) : Item() {

    override fun getLayout(): Int = R.layout.launch_payload_recycler_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val noInfoString = viewHolder.itemView.context.getString(R.string.no_info)

        viewHolder.itemView.text_payload_id.text = payloadId ?: noInfoString
        viewHolder.itemView.text_nationality.text = nationality ?: noInfoString
        viewHolder.itemView.text_manufacturer.text = manufacturer ?: noInfoString
        viewHolder.itemView.text_payload_type.text = payloadType ?: noInfoString
        viewHolder.itemView.text_payload_mass_kg.text = payloadMassKg?.toString() ?: noInfoString
        viewHolder.itemView.text_orbit.text = orbit ?: noInfoString
        viewHolder.itemView.text_reused.text = if (reused == true) {
            viewHolder.itemView.context.getString(R.string.yes)
        } else viewHolder.itemView.context.getString(R.string.no)
        viewHolder.itemView.text_customers.text = customers.joinToString(", ")
    }
}
