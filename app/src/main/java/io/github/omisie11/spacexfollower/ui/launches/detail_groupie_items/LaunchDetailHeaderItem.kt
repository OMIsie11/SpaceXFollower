package io.github.omisie11.spacexfollower.ui.launches.detail_groupie_items

import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.recycler_header_item.view.*

open class LaunchDetailHeaderItem(
    private val flightNumber: Int,
    private val missionPatchSmall: String?,
    private val missionName: String,
    private val launchDateUnix: Long?,
    private val siteName: String
) : Item() {

    override fun getLayout(): Int = R.layout.recycler_header_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_flight_number.text = flightNumber.toString()
        viewHolder.itemView.text_mission_name.text = missionName
        viewHolder.itemView.text_launch_date.text =
            if (launchDateUnix != null) getLocalTimeFromUnix(launchDateUnix)
            else viewHolder.itemView.context.getString(R.string.launch_date_null)
        viewHolder.itemView.text_launch_site_name.text = siteName

        if (!missionPatchSmall.isNullOrBlank()) {
            Picasso.get()
                .load(missionPatchSmall)
                .placeholder(R.drawable.ic_circle_48dp)
                .fit()
                .centerInside()
                .tag(viewHolder.itemView.context)
                .into(viewHolder.itemView.image_mission_patch)
        }
    }
}