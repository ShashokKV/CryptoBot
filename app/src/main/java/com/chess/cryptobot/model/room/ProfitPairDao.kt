package com.chess.cryptobot.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime

@Dao
interface ProfitPairDao {
    @Insert
    fun insertAll(profitPairs: List<ProfitPair?>?)

    @Query("SELECT pairName, SUM(percent) AS percent, COUNT(id) AS id, dateCreated " +
            "FROM ProfitPair " +
            "WHERE (dateCreated BETWEEN :dateStart AND :dateEnd) AND percent > :minPercent AND (pairName = :pairName OR :pairName IS NULL) " +
            "GROUP BY pairName " +
            "ORDER BY percent DESC")
    fun getPairsByDayAndMinPercent(pairName: String?, dateStart: LocalDateTime?, dateEnd: LocalDateTime?, minPercent: Float?): List<ProfitPair?>?

    @Query("SELECT pairName FROM ProfitPair " +
            "WHERE (dateCreated > :date) AND percent > :minPercent " +
            "GROUP BY pairName")
    fun getPairNamesByDateAndMinPercent(date: LocalDateTime?, minPercent: Float?): List<String>?

    @Query("SELECT * " +
            "FROM ProfitPair " +
            "WHERE dateCreated < :dateTime")
    fun getLowerThanDate(dateTime: LocalDateTime?): List<ProfitPair?>?

    @Delete
    fun deleteAll(profitPairs: List<ProfitPair?>?)
}