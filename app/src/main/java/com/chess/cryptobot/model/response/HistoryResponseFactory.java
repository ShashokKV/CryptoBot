package com.chess.cryptobot.model.response;

import com.chess.cryptobot.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryResponseFactory {
    private final List<? extends HistoryResponse> historyResponseList;

    public HistoryResponseFactory(List<? extends HistoryResponse> historyResponseList) {
        this.historyResponseList = historyResponseList;
    }

    public List<History> getHistory() {
        List<History> historyList = new ArrayList<>();
        for (HistoryResponse historyResponse : historyResponseList) {
            History history = new History();
            history.setAction(historyResponse.getHistoryAction());
            history.setAmount(historyResponse.getHistoryAmount());
            history.setCurrencyName(historyResponse.getHistoryName());
            history.setDateTime(historyResponse.getHistoryTime());
            history.setMarket(historyResponse.getHistoryMarket());
            history.setPrice(historyResponse.getHistoryPrice());
            history.setProgress(historyResponse.getProgress());
            historyList.add(history);
        }
        return historyList;
    }
}
