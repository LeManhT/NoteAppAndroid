package com.example.noteappandroid.databases.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter

//class InstantConverters {
//    @TypeConverter
//    fun fromInstant(instant: kotlinx.datetime.Instant?): Long? {
//        return instant?.toEpochMilliseconds()
//    }
//
//    @TypeConverter
//    fun toInstant(epochMillis: Long?): kotlinx.datetime.Instant? {
//        return epochMillis?.let { kotlinx.datetime.Instant.fromEpochMilliseconds(it) }
//    }
@SuppressLint("NewApi")
object InstantConverters {

    @TypeConverter
    @JvmStatic
    fun toString(instant: kotlinx.datetime.Instant?): String? = instant?.toString()

    @TypeConverter
    @JvmStatic
    fun toDate(value: String?): kotlinx.datetime.Instant? =
        value?.let { kotlinx.datetime.Instant.parse(it) }

//    }
}