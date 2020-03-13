package com.chess.cryptobot.model.response.binance

import com.google.gson.Gson
import org.junit.Test

class BinanceTickerResponseTest {

    @Test
    fun getMarketName() {
        val binanceTickerResponse = Gson().fromJson("{\n" +
                "  \"symbol\": \"BNBBTC\",\n" +
                "  \"priceChange\": \"-94.99999800\",\n" +
                "  \"priceChangePercent\": \"-95.960\",\n" +
                "  \"weightedAvgPrice\": \"0.29628482\",\n" +
                "  \"prevClosePrice\": \"0.10002000\",\n" +
                "  \"lastPrice\": \"4.00000200\",\n" +
                "  \"lastQty\": \"200.00000000\",\n" +
                "  \"bidPrice\": \"4.00000000\",\n" +
                "  \"askPrice\": \"4.00000200\",\n" +
                "  \"openPrice\": \"99.00000000\",\n" +
                "  \"highPrice\": \"100.00000000\",\n" +
                "  \"lowPrice\": \"0.10000000\",\n" +
                "  \"volume\": \"8913.30000000\",\n" +
                "  \"quoteVolume\": \"15.30000000\",\n" +
                "  \"openTime\": 1499783499040,\n" +
                "  \"closeTime\": 1499869899040,\n" +
                "  \"firstId\": 28385,   // First tradeId\n" +
                "  \"lastId\": 28460,    // Last tradeId\n" +
                "  \"count\": 76         // Trade count\n" +
                "}", BinanceTickerResponse::class.java)
        assert(binanceTickerResponse.marketName == "BNB/BTC")
    }
}