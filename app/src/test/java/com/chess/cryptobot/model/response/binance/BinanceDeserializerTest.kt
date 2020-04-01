package com.chess.cryptobot.model.response.binance

import com.google.gson.GsonBuilder
import org.junit.Test


class BinanceDeserializerTest {

    @Test
    fun deserialize() {
        val json = "{\n" +
                "    \"success\": true,\n" +
                "    \"assetDetail\": {\n" +
                "        \"CTR\": {\n" +
                "            \"minWithdrawAmount\": \"70.00000000\", //min withdraw amount\n" +
                "            \"depositStatus\": false,//deposit status (false if ALL of networks' are false)\n" +
                "            \"withdrawFee\": 35, // withdraw fee\n" +
                "            \"withdrawStatus\": true, //withdraw status (false if ALL of networks' are false)\n" +
                "            \"depositTip\": \"Delisted, Deposit Suspended\" //reason\n" +
                "        },\n" +
                "        \"SKY\": {\n" +
                "            \"minWithdrawAmount\": \"0.02000000\",\n" +
                "            \"depositStatus\": true,\n" +
                "            \"withdrawFee\": 0.01,\n" +
                "            \"withdrawStatus\": true\n" +
                "        }   \n" +
                "    }\n" +
                "}"

        val builder = GsonBuilder()
        builder.registerTypeAdapter(BinanceResponse::class.java, BinanceDeserializer())
        val gson = builder.create()
        val response: BinanceResponse = gson.fromJson(json, BinanceResponse::class.java)

        val detail = response.assetDetails?.filter { assetDetail -> assetDetail.currencyName.equals("SKY") }
        assert(detail?.get(0)?.fee == 0.01)
    }
}