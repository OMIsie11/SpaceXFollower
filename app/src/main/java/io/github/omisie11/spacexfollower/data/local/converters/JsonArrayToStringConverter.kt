package io.github.omisie11.spacexfollower.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class JsonArrayToStringConverter {

    private val gson = Gson()

    @TypeConverter
    fun jsonArrayToString(missionIds: MutableList<String>?): String? {
        return gson.toJson(missionIds)
    }

    @TypeConverter
    fun stringToJsonArray(data: String?): MutableList<String>? {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<MutableList<String>>() {
        }.type

        return gson.fromJson(data, listType)
    }
}
