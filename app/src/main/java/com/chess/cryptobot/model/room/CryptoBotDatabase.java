package com.chess.cryptobot.model.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.chess.cryptobot.model.room.converter.Converters;

@Database(version = 2, entities = {ProfitPair.class, BtcBalance.class}, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class CryptoBotDatabase extends RoomDatabase {
    private static CryptoBotDatabase database;

    public abstract ProfitPairDao getProfitPairDao();

    public abstract BtcBalanceDao getBtcBalanceDao();

    public static CryptoBotDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, CryptoBotDatabase.class, "cryptobotDB")
                    .enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
}
