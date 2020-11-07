package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CoinInfoDao {

    @Query("SELECT * " +
            "FROM CoinInfo " +
            "WHERE name = :name AND marketName = :marketName")
    fun getByNameAndMarketName(name: String?, marketName: String) : CoinInfo?

    @Update
    fun update(coinInfo: CoinInfo)

    @Insert
    fun insert(coinInfo: CoinInfo)
}