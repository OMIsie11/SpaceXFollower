package io.github.omisie11.spacexfollower.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.local.model.launch.LaunchSite

// Used for Launch
class LaunchSiteConverter {

    private val gson = Gson()
    private val type = object : TypeToken<LaunchSite>() {}.type

    @TypeConverter
    fun launchSiteToString(nextLaunchSite: LaunchSite): String? = gson.toJson(nextLaunchSite, type)

    @TypeConverter
    fun stringToLaunchSite(nextLaunchSiteString: String): LaunchSite? = gson.fromJson(nextLaunchSiteString, type)
}