package com.chess.cryptobot.task;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.response.CurrenciesResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinStatusTask extends MarketTask<Integer, Integer> {
    private Map<String, Boolean> bittrexStatuses;
    private Map<String, Boolean> livecoinStatuses;

    public CoinStatusTask(ContextHolder holder) {
        super(holder);
    }

    @Override
    public void preMarketProcess(Integer param) {
        bittrexStatuses = new HashMap<>();
        livecoinStatuses = new HashMap<>();
    }

    @Override
    public Integer marketProcess(Market market, Integer param) throws MarketException {
        if (market.getMarketName().equals(Market.BITTREX_MARKET)) {
            updateStatuses(bittrexStatuses, market.getCurrencies());
        } else if (market.getMarketName().equals(Market.LIVECOIN_MARKET)) {
            updateStatuses(livecoinStatuses, market.getCurrencies());
        }
        return 0;
    }

    private void updateStatuses(Map<String, Boolean> statusMap, List<CurrenciesResponse> currencies) {
        currencies.forEach(currency -> statusMap.put(currency.getCurrencyName(), currency.isActive()));
    }

    @Override
    public Integer postMarketProcess(Integer result) {
        return 0;
    }

    @Override
    public Integer exceptionProcess(Integer param, String exceptionMessage) {
        return null;
    }

    @Override
    public void doInPostExecute(Integer result, ContextHolder holder) {
        BalanceHolder balanceHolder = (BalanceHolder) holder;
        balanceHolder.setCurrencyStatus(bittrexStatuses, livecoinStatuses);
        balanceHolder.updateAllItems();
    }

    @Override
    public void doInOnCanceled(Integer result, ContextHolder holder) {

    }
}
