package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BalanceSyncDao {
    @Insert
    fun insert(balanceSyncTicker: BalanceSyncTicker)

    @Query("SELECT * FROM BalanceSyncTicker WHERE coinName=:coinName AND marketName=:marketName")
    fun getByCoinNameAndMarket(coinName: String, marketName: String): List<BalanceSyncTicker>

    @Delete
    fun deleteAll(balanceSyncTickers: List<BalanceSyncTicker>)
}