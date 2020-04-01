package com.chess.cryptobot.market

import com.chess.cryptobot.R

class WithdrawalMarketFactory: MarketFactory() {
    override val bittrexApiKey = R.string.bittrex_api_key
    override val bittrexSecretKey = R.string.bittrex_secret_key
    override val binanceApiKey = R.string.binance_withdrawal_api_key
    override val binanceSecretKey = R.string.binance_withdrawal_secret_key
}