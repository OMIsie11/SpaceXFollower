package io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.payload_recycler_item.view.*

open class PayloadItem(
    private val nationality: String?
) : Item() {

    override fun getLayout(): Int = R.layout.payload_recycler_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_nationality.text = nationality
    }
}