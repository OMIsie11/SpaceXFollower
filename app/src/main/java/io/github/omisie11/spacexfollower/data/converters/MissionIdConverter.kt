package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class MissionIdConverter {

    private val gson = Gson()

    @TypeConverter
    fun missionsIdListToString(missionIds: MutableList<String>?): String? {
        return gson.toJson(missionIds)
    }

    @TypeConverter
    fun stringToMissionIdsList(data: String?): MutableList<String>? {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<MutableList<String>>() {

        }.type

        return gson.fromJson(data, listType)
    }
}