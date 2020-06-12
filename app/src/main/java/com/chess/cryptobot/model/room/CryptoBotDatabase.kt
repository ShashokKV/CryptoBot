package com.chess.cryptobot.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chess.cryptobot.model.room.converter.TimestampConverter

@Database(version = 3, entities = [ProfitPair::class, BtcBalance::class, BalanceSyncTicker::class], exportSchema = false)
@TypeConverters(TimestampConverter::class)
abstract class CryptoBotDatabase : RoomDatabase() {
    abstract val profitPairDao: ProfitPairDao?
    abstract val btcBalanceDao: BtcBalanceDao?
    abstract val balanceSyncDao: BalanceSyncDao?

    companion object {
        private var database: CryptoBotDatabase? = null

        fun getInstance(context: Context?): CryptoBotDatabase? {
            if (database == null) {
                database = Room.databaseBuilder(context!!, CryptoBotDatabase::class.java, "cryptobotDB")
                        .enableMultiInstanceInvalidation()
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return database
        }
    }
}