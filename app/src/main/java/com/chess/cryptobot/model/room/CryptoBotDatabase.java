package com.chess.cryptobot.model.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.chess.cryptobot.model.room.converter.Converters;

@Database(version = 1, entities = {ProfitPair.class}, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class CryptoBotDatabase extends RoomDatabase {
    public abstract ProfitPairDao getProfitPairDao();
}
