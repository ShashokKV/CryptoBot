package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime

@Dao
interface CryptoBalanceDao {
    @Insert
    fun insert(cryptoBalance: CryptoBalance?)

    @Query("SELECT * " +
            "FROM CryptoBalance " +
            "WHERE dateCreated BETWEEN :dateStart AND :dateEnd AND name = :coinName")
    fun getByDateAndCoinName(dateStart: LocalDateTime?, dateEnd: LocalDateTime?, coinName: String): List<CryptoBalance>?

    @Query("SELECT * " +
            "FROM CryptoBalance " +
            "WHERE dateCreated < :dateTime")
    fun getLowerThanDate(dateTime: LocalDateTime?): List<CryptoBalance?>?

    @Delete
    fun deleteAll(balances: List<CryptoBalance?>?)

    @Query("SELECT name FROM CryptoBalance GROUP BY name")
    fun getAllCoinNames(): List<String>
}