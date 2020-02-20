package com.chess.cryptobot.task

import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Balance

class BalanceUpdateTask(balanceHolder: BalanceHolder) : MarketTask<Balance, Balance?>(balanceHolder) {
    private var hashCode = 0
    override fun preMarketProcess(param: Balance) {
        hashCode = param.getAmounts().hashCode()
    }

    @Throws(MarketException::class)
    override fun marketProcess(market: Market, param: Balance): Balance {
        param.setAmount(market.getMarketName(), market.getAmount(param.name))
        return param
    }

    public override fun postMarketProcess(result: Balance?): Balance? {
        return if (hashCode == result!!.getAmounts().hashCode()) null else result
    }

    override fun exceptionProcess(param: Balance, exceptionMessage: String?): Balance {
        param.message = exceptionMessage
        return param
    }

    override fun doInPostExecute(result: Balance?, holder: ContextHolder) {
        holder.setItem(result)
    }

    override fun doInOnCanceled(result: Balance?, holder: ContextHolder?) {
        holder!!.makeToast(result?.message!!)
    }
}