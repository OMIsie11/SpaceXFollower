package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.Launch

// Used for Next Launch
class NextLaunchLaunchSiteConverter{

    private val gson = Gson()
    private val type = object: TypeToken<Launch.LaunchSite>() {}.type

    @TypeConverter
    fun launchSiteToString(launchSite: Launch.LaunchSite): String? = gson.toJson(launchSite, type)

    @TypeConverter
    fun stringToLaunchSite(launchSiteString: String): Launch.LaunchSite? = gson.fromJson(launchSiteString, type)
}