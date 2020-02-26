package com.chess.cryptobot.model.room.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object TimestampConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return if (value == null) null else LocalDateTime.ofEpochSecond(value, 0,
                ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
    }
}