package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PairMinTradeSizeDao {
    @Insert
    fun insert(pair: PairMinTradeSize)

    @Update
    fun update(pairMinTradeSize: PairMinTradeSize)

    @Query("SELECT * FROM" +
            " PairMinTradeSize " +
            "WHERE pairName = :pairName")
    fun getByPairName(pairName: String): PairMinTradeSize?


}