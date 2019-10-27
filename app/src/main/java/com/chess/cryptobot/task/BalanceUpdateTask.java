package com.chess.cryptobot.task;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.Balance;

public class BalanceUpdateTask extends MarketTask<Balance, Balance> {
    private int hashCode;

    public BalanceUpdateTask(BalanceHolder balanceHolder) {
        super(balanceHolder);
    }

    @Override
    public void preMarketProcess(Balance balance) {
        hashCode = balance.getAmounts().hashCode();
    }

    @Override
    public Balance marketProcess(Market market, Balance balance) throws MarketException {
        balance.setAmount(market.getMarketName(), market.getAmount(balance.getName()));
        return balance;
    }

    @Override
    public Balance postMarketProcess(Balance balance) {
        if (hashCode==balance.getAmounts().hashCode()) return null;
        return balance;
    }

    @Override
    public Balance exceptionProcess(Balance balance, String exceptionMessage) {
        balance.setMessage(exceptionMessage);
        return balance;
    }

    @Override
    public void doInPostExecute(Balance balance, ContextHolder holder) {
        holder.setItem(balance);
    }

    @Override
    public void doInOnCanceled(Balance balance, ContextHolder holder) {
        holder.makeToast(balance.getMessage());
    }
}
