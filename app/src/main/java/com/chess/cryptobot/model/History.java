package com.chess.cryptobot.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class History implements ViewItem, Comparable<History> {
    private LocalDateTime dateTime;
    private String market;
    private String currencyName;
    private String action;
    private Double amount;
    private Double price;
    private Integer progress;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Override
    public String getName() {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME).concat(market).concat(currencyName);
    }

    @Override
    public int compareTo(History history) {
        return history.dateTime.compareTo(this.dateTime);
    }
}
