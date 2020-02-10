package com.chess.cryptobot.task;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.history.HistoryHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.History;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryTask extends MarketTask<Integer, String> {
    private final HistoryHolder.State state;
    private List<History> historyList;

    public HistoryTask(ContextHolder holder, HistoryHolder.State state) {
        super(holder);
        this.state = state;
    }

    @Override
    protected void preMarketProcess(Integer param) {
        historyList = new ArrayList<>();
    }

    @Override
    protected String marketProcess(Market market, Integer param) throws MarketException {
        if (market.keysIsEmpty()) return "";
        if (state== HistoryHolder.State.HISTORY) {
            historyList.addAll(market.getHistory());
        } else {
            historyList.addAll(market.getOpenOrders());
        }
        return "";
    }

    @Override
    protected String postMarketProcess(String result) {
        Collections.sort(historyList);
        return "";
    }

    @Override
    protected String exceptionProcess(Integer param, String exceptionMessage) {
        return exceptionMessage;
    }

    @Override
    protected void doInPostExecute(String result, ContextHolder holder) {
        HistoryHolder historyHolder = (HistoryHolder) holder;
        if (!result.isEmpty()) {
            historyHolder.makeToast(result);
            return;
        }
        historyList.forEach(historyHolder::add);
    }

    @Override
    protected void doInOnCanceled(String result, ContextHolder holder) {

    }
}
