package com.chess.cryptobot.model.room.converter;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Converters {
    @TypeConverter
    public static LocalDateTime fromTimestamp(Long value) {
        return value == null ? null : LocalDateTime.ofEpochSecond(value, 0,
                ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDateTime date) {
        return date == null ? null : date.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
    }
}
