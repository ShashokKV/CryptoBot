package com.chess.cryptobot.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import static androidx.room.ColumnInfo.INTEGER;
import static androidx.room.ColumnInfo.REAL;

@Entity
public class BtcBalance {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = INTEGER)
    private int id;

    @ColumnInfo(name = "balance", typeAffinity = REAL)
    private Float balance;
    @ColumnInfo(name = "dateCreated")
    private LocalDateTime dateCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
