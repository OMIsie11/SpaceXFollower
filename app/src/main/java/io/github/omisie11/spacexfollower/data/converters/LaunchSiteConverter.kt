package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.launch.LaunchSite

// Used for Next NextLaunch
class LaunchSiteConverter {

    private val gson = Gson()
    private val type = object : TypeToken<LaunchSite>() {}.type

    @TypeConverter
    fun launchSiteToString(nextLaunchSite: LaunchSite): String? = gson.toJson(nextLaunchSite, type)

    @TypeConverter
    fun stringToLaunchSite(nextLaunchSiteString: String): LaunchSite? = gson.fromJson(nextLaunchSiteString, type)
}