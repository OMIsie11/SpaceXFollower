package io.github.omisie11.spacexfollower.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.omisie11.spacexfollower.data.model.Company

class HeadquarterConverter {

    private val gson = Gson()
    private val type = object : TypeToken<Company.Headquarters>() {}.type

    @TypeConverter
    fun headquartersToString(headquarters: Company.Headquarters): String? {

        return gson.toJson(headquarters, type)
    }

    @TypeConverter
    fun stringToHeadquarters(data: String?): Company.Headquarters? {

        return gson.fromJson(data, type)
    }
}