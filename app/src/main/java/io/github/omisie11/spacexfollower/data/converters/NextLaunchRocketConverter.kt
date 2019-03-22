package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.NextLaunch

// Used for Next NextLaunch
class NextLaunchRocketConverter{

    private val gson = Gson()
    private val type = object: TypeToken<NextLaunch.Rocket>() {}.type

    @TypeConverter
    fun rocketToString(rocket: NextLaunch.Rocket): String = gson.toJson(rocket, type)

    @TypeConverter
    fun stringToRocket(rocketString: String): NextLaunch.Rocket = gson.fromJson(rocketString, type)
}