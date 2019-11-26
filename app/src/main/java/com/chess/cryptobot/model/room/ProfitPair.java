package com.chess.cryptobot.model.room;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import static androidx.room.ColumnInfo.INTEGER;
import static androidx.room.ColumnInfo.REAL;
import static androidx.room.ColumnInfo.TEXT;

@Entity
public class ProfitPair {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = INTEGER)
    private int id;
    @ColumnInfo(name = "pairName", typeAffinity = TEXT)
    private String pairName;
    @ColumnInfo(name = "percent", typeAffinity = REAL)
    private Float percent;
    @ColumnInfo(name = "dateCreated")
    private LocalDateTime dateCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public Float getPercent() {
        return percent;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj.equals(this)) return true;

        if (obj instanceof ProfitPair) {
            return this.getPairName().equals(((ProfitPair) obj).getPairName());
        } else if (obj instanceof String) {
            return this.getPairName().equals(obj);
        } else {
            return false;
        }
    }
}
