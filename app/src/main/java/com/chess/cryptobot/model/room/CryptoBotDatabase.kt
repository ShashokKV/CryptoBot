package com.chess.cryptobot.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chess.cryptobot.model.room.converter.TimestampConverter
import com.chess.cryptobot.util.SingletonHolder

@Database(version = 7, entities = [
    ProfitPair::class,
    CryptoBalance::class,
    BalanceSyncTicker::class,
    PairMinTradeSize::class,
    CoinInfo::class
], exportSchema = false)
@TypeConverters(TimestampConverter::class)
abstract class CryptoBotDatabase : RoomDatabase() {
    abstract val profitPairDao: ProfitPairDao
    abstract val cryptoBalanceDao: CryptoBalanceDao
    abstract val balanceSyncDao: BalanceSyncDao
    abstract val minTradeSizeDao: PairMinTradeSizeDao
    abstract val coinInfoDao: CoinInfoDao

    companion object : SingletonHolder<CryptoBotDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, CryptoBotDatabase::class.java, "cryptobotDB")
                .enableMultiInstanceInvalidation()
                .fallbackToDestructiveMigration()
                .build()
    })
}