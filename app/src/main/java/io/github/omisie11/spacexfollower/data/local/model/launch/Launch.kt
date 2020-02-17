package io.github.omisie11.spacexfollower.data.local.model.launch

import androidx.room.*
import com.google.gson.annotations.SerializedName
import io.github.omisie11.spacexfollower.data.local.converters.JsonArrayToStringConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchLinksConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchSiteConverter
import io.github.omisie11.spacexfollower.data.local.converters.RocketConverter

@Entity(tableName = "upcoming_launches_table")
data class Launch(
    @SerializedName("flight_number")
    @PrimaryKey
    @ColumnInfo(name = "flight_number")
    val flightNumber: Int,
    @SerializedName("mission_name")
    @ColumnInfo(name = "mission_name")
    val missionName: String,
    @SerializedName("mission_id")
    @TypeConverters(JsonArrayToStringConverter::class)
    @ColumnInfo(name = "mission_id")
    val missionId: MutableList<String>?,
    @SerializedName("launch_date_unix")
    @ColumnInfo(name = "launch_date_unix")
    val launchDateUnix: Long?,
    @SerializedName("is_tentative")
    @ColumnInfo(name = "is_tentative")
    val isTentative: Boolean,
    @SerializedName("launch_site")
    @TypeConverters(LaunchSiteConverter::class)
    @ColumnInfo(name = "launch_site")
    val launch_site: LaunchSite,
    @SerializedName("rocket")
    @TypeConverters(RocketConverter::class)
    @ColumnInfo(name = "rocket")
    val rocket: Rocket,
    @SerializedName("links")
    @TypeConverters(LaunchLinksConverter::class)
    @ColumnInfo(name = "links")
    val links: Links,
    @SerializedName("details")
    @ColumnInfo(name = "details")
    val details: String?
) {

    data class Links(
        @SerializedName("mission_patch")
        val missionPatch: String?,
        @SerializedName("mission_patch_small")
        val missionPatchSmall: String?,
        @SerializedName("reddit_campaign")
        val redditCampaign: String?,
        @SerializedName("reddit_launch")
        val redditLaunch: String?,
        @SerializedName("reddit_recovery")
        val redditRecovery: String?,
        @SerializedName("reddit_media")
        val redditMedia: String?,
        @SerializedName("presskit")
        val presskit: String?,
        @SerializedName("article_link")
        val articleLink: String?,
        @SerializedName("wikipedia")
        val wikipedia: String?,
        @SerializedName("video_link")
        val videoLink: String?
    ) {

        @Ignore
        fun getLinksWithNamesAsList(): List<Pair<String, String?>> = listOf(
            Pair("Reddit Campaign", redditCampaign),
            Pair("Reddit Launch", redditLaunch),
            Pair("Reddit Recovery", redditRecovery),
            Pair("Reddit Media", redditMedia),
            Pair("Presskit", presskit),
            Pair("Article", articleLink),
            Pair("Wikipedia", wikipedia),
            Pair("Video", videoLink)
        )
    }
}