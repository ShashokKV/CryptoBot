package com.chess.cryptobot.model.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface ProfitPairDao {
    @Insert
    void insertAll(List<ProfitPair> profitPairs);

    @Query("SELECT pairName, SUM(percent) AS percent, COUNT(id) AS id, dateCreated " +
            "FROM ProfitPair " +
            "WHERE (dateCreated BETWEEN :dateStart AND :dateEnd) AND percent > :minPercent AND (pairName = :pairName OR :pairName IS NULL) " +
            "GROUP BY pairName " +
            "ORDER BY percent DESC")
    List<ProfitPair> getPairsByDayAndMinPercent(String pairName, LocalDateTime dateStart, LocalDateTime dateEnd, Float minPercent);

    @Query("SELECT pairName FROM ProfitPair " +
            "WHERE (dateCreated > :date) AND percent > :minPercent " +
            "GROUP BY pairName")
    List<String> getPairNamesByDateAndMinPercent(LocalDateTime date, Float minPercent);
}
