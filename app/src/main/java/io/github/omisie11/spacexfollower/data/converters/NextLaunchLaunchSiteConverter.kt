package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.NextLaunch

// Used for Next NextLaunch
class NextLaunchLaunchSiteConverter{

    private val gson = Gson()
    private val type = object: TypeToken<NextLaunch.LaunchSite>() {}.type

    @TypeConverter
    fun launchSiteToString(nextLaunchSite: NextLaunch.LaunchSite): String? = gson.toJson(nextLaunchSite, type)

    @TypeConverter
    fun stringToLaunchSite(nextLaunchSiteString: String): NextLaunch.LaunchSite? = gson.fromJson(nextLaunchSiteString, type)
}