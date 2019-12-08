package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.HistoryResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LivecoinHistoryResponse extends LivecoinResponse implements HistoryResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("date")
    @Expose
    private Long date;

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("variableAmount")
    @Expose
    private Double variableAmount;

    @SerializedName("fixedCurrency")
    @Expose
    private String fixedCurrency;

    @SerializedName("taxCurrency")
    @Expose
    private String taxCurrency;

    @Override
    public LocalDateTime getHistoryTime() {
        return LocalDateTime.ofEpochSecond(Math.round(date / 1000), 0,
                ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
    }

    @Override
    public String getHistoryName() {
        return fixedCurrency.equals(taxCurrency) ? fixedCurrency :
                taxCurrency.concat("/").concat(fixedCurrency);
    }

    @Override
    public String getHistoryMarket() {
        return "livecoin";
    }

    @Override
    public Double getHistoryAmount() {
        return amount;
    }

    @Override
    public Double getHistoryPrice() {
        return variableAmount == null ? null : variableAmount / amount;
    }

    @Override
    public String getHistoryAction() {
        return type;
    }

    @Override
    public Integer getProgress() {
        return 0;
    }
}
