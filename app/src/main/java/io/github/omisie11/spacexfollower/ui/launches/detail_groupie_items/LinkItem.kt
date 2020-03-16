package io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.omisie11.spacexfollower.R
import kotlinx.android.synthetic.main.launch_link_recycler_item.view.*

class LinkItem(
    private val title: String,
    private val url: String,
    private val itemClickListener: OnLinkItemClickListener
) : Item() {

    override fun getLayout(): Int = R.layout.launch_link_recycler_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_title.text = title

        viewHolder.itemView.setOnClickListener {
            itemClickListener.onLinkItemClicked(url)
        }
    }

    interface OnLinkItemClickListener {
        fun onLinkItemClicked(linkUrl: String)
    }
}
