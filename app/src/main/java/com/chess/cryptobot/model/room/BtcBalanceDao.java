package com.chess.cryptobot.model.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface BtcBalanceDao {
    @Insert
    void insert(BtcBalance btcBalance);

    @Query("SELECT * " +
            "FROM BtcBalance " +
            "WHERE dateCreated BETWEEN :dateStart AND :dateEnd")
    List<BtcBalance> getByDate(LocalDateTime dateStart, LocalDateTime dateEnd);
}
