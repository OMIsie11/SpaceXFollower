package io.github.omisie11.spacexfollower.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch

class LaunchLinksConverter {

    private val gson = Gson()
    private val type = object : TypeToken<Launch.Links>() {}.type

    @TypeConverter
    fun launchSiteToString(links: Launch.Links): String? = gson.toJson(links, type)

    @TypeConverter
    fun stringToLaunchSite(linksString: String): Launch.Links? = gson.fromJson(linksString, type)
}
