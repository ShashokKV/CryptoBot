package com.chess.cryptobot.model.response;

import java.time.LocalDateTime;

public interface HistoryResponse {

    LocalDateTime getHistoryTime();

    String getHistoryName();

    String getHistoryMarket();

    Double getHistoryAmount();

    Double getHistoryPrice();

    String getHistoryAction();

    Integer getProgress();
}
