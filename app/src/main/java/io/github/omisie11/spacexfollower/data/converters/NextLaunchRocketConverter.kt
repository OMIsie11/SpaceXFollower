package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.Launch

// Used for Next Launch
class NextLaunchRocketConverter{

    private val gson = Gson()
    private val type = object: TypeToken<Launch.Rocket>() {}.type

    @TypeConverter
    fun rocketToString(rocket: Launch.Rocket): String = gson.toJson(rocket, type)

    @TypeConverter
    fun stringToRocket(rocketString: String): Launch.Rocket = gson.fromJson(rocketString, type)
}