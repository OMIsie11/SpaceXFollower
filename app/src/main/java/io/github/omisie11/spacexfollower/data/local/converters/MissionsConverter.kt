package io.github.omisie11.spacexfollower.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import java.util.*

// Used to convert List of missions to json and back to be able to save it in Room
class MissionsConverter {

    private val gson = Gson()

    @TypeConverter
    fun missionsListToString(missions: MutableList<Capsule.Mission>?): String? {
        return gson.toJson(missions)
    }

    @TypeConverter
    fun stringToMissionsList(data: String?): MutableList<Capsule.Mission>? {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<MutableList<Capsule.Mission>>() {
        }.type

        return gson.fromJson(data, listType)
    }
}