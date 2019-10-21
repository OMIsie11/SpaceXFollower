package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.LaunchPad

// Used to convert Launch Pad Location
class LaunchPadLocationConverter {

    private val gson = Gson()
    private val type = object : TypeToken<LaunchPad.Location>() {}.type

    @TypeConverter
    fun launchSiteToString(launchPadLocation: LaunchPad.Location): String? =
        gson.toJson(launchPadLocation, type)

    @TypeConverter
    fun stringToLaunchSite(launchSiteLocationString: String): LaunchPad.Location? =
        gson.fromJson(launchSiteLocationString, type)
}