package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime

@Dao
interface BtcBalanceDao {
    @Insert
    fun insert(btcBalance: BtcBalance?)

    @Query("SELECT * " +
            "FROM BtcBalance " +
            "WHERE dateCreated BETWEEN :dateStart AND :dateEnd")
    fun getByDate(dateStart: LocalDateTime?, dateEnd: LocalDateTime?): List<BtcBalance>?

    @Query("SELECT * " +
            "FROM BtcBalance " +
            "WHERE dateCreated < :dateTime")
    fun getLowerThanDate(dateTime: LocalDateTime?): List<BtcBalance?>?

    @Delete
    fun deleteAll(balances: List<BtcBalance?>?)
}