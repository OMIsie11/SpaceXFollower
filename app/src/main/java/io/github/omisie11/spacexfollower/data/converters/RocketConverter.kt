package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.Launch.Rocket


// Used for Next NextLaunch
class RocketConverter{

    private val gson = Gson()
    private val type = object: TypeToken<Rocket>() {}.type

    @TypeConverter
    fun rocketToString(rocket: Rocket): String = gson.toJson(rocket, type)

    @TypeConverter
    fun stringToRocket(rocketString: String): Rocket = gson.fromJson(rocketString, type)
}