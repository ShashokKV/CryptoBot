package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.HistoryResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class LivecoinOrdersResponse extends LivecoinResponse {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public class Datum implements HistoryResponse {

        @SerializedName("currencyPair")
        @Expose
        public String currencyPair;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("issueTime")
        @Expose
        public Long issueTime;
        @SerializedName("price")
        @Expose
        public Double price;
        @SerializedName("quantity")
        @Expose
        public Double quantity;
        @SerializedName("remainingQuantity")
        @Expose
        public Double remainingQuantity;

        @Override
        public LocalDateTime getHistoryTime() {
            return LocalDateTime.ofEpochSecond(Math.round(issueTime / 1000), 0,
                    ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
        }

        @Override
        public String getHistoryName() {
            String[] currencies = currencyPair.split("/");
            return currencies[1].concat("/").concat(currencies[0]);
        }

        @Override
        public String getHistoryMarket() {
            return "livecoin";
        }

        @Override
        public Double getHistoryAmount() {
            return quantity;
        }

        @Override
        public Double getHistoryPrice() {
            return price;
        }

        @Override
        public String getHistoryAction() {
            return type;
        }

        @Override
        public Integer getProgress() {
            if (quantity==0d) return 0;
            return Double.valueOf(((quantity - remainingQuantity) / quantity) * 100).intValue();
        }
    }
}
