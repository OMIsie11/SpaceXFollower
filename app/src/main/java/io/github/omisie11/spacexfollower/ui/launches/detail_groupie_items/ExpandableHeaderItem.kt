package io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.item_expandable_header.view.*

class ExpandableHeaderItem(private val title: String) : Item(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout(): Int = R.layout.item_expandable_header

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_title.text = title
        viewHolder.itemView.image_icon.setImageResource(R.drawable.ic_expand_more_24dp)

        viewHolder.itemView.expandable_item_root.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.image_icon.setImageResource(getRotatedIconResId())
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getRotatedIconResId() =
        if (expandableGroup.isExpanded)
            R.drawable.ic_expand_less_24dp
        else
            R.drawable.ic_expand_more_24dp
}