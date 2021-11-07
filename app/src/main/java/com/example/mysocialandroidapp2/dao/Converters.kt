package com.example.mysocialandroidapp2.dao

import androidx.room.TypeConverter
import com.example.mysocialandroidapp2.enumeration.AttachmentType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    var gson = Gson()

    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun stringToSetLongType(data: String?): Set<Long> {
        if (data == null) {
            return emptySet()
        }
        val setType: Type = object : TypeToken<Set<Long>>() {}.type
        return gson.fromJson(data, setType)
    }

    @TypeConverter
    fun fromSetLongType(data: Set<Long>): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun stringToListStringType(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }
        val setType: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, setType)
    }

    @TypeConverter
    fun fromListStringType(data: List<String>): String? {
        return gson.toJson(data)
    }


    @TypeConverter
    fun toAnyType(value: String): Any? = value
    @TypeConverter
    fun fromAnyType(value: Any?) = value.toString()

}