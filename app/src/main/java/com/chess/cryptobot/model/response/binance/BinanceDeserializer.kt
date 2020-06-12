package com.chess.cryptobot.model.response.binance

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class BinanceDeserializer : JsonDeserializer<BinanceResponse> {
    val gson = Gson()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BinanceResponse {
        var response = BinanceResponse()
        if (json.isJsonArray) {
            val jsonArray = json.asJsonArray
            jsonArray.forEach { response = parseJsonObject(response, it) }
            return response
        }

        if (json.isJsonObject) {
            response = parseJsonObject(response, json)
        }

        return response
    }

    private fun parseJsonObject(response: BinanceResponse, json: JsonElement): BinanceResponse {
        var newResponse = response
        val jsonObject = json.asJsonObject
        val keySet = jsonObject.keySet()
        if (keySet.contains("assetDetail")) {
            for (entry in jsonObject.entrySet()) {
                if (entry.key == "assetDetail") {
                    val assetObject = entry.value.asJsonObject
                    for (asset in assetObject.entrySet()) {
                        val assetDetail = gson.fromJson(asset.value, AssetDetail::class.java)
                        assetDetail.currencyName = asset.key
                        newResponse.assetDetails.add(assetDetail)
                    }
                }
            }
        } else if (keySet.contains("coin") && keySet.contains("free")) {
            val balance = gson.fromJson(jsonObject, BinanceBalance::class.java)
            newResponse.balances.add(balance)
        } else if (keySet.contains("symbol") && keySet.contains("bidPrice") && keySet.contains("askPrice")) {
            val ticker = gson.fromJson(jsonObject, BinanceTicker::class.java)
            newResponse.tickers.add(ticker)
        } else if (keySet.contains("symbol") && keySet.contains("side")
                && keySet.contains("price") && keySet.contains("origQty") && keySet.contains("executedQty")) {
            val order = gson.fromJson(jsonObject, BinanceOrder::class.java)
            newResponse.orders.add(order)
        } else {
            newResponse = gson.fromJson(json, BinanceResponse::class.java)
        }
        return newResponse
    }

    companion object SymbolParser {
        fun symbolToPairName(symbol: String?): String {
            if (symbol == null) return ""
            for (baseName in listOf("BTC", "ETH", "USDT", "BNB", "XRP", "TRX")) {
                val index = symbol.indexOf(baseName, symbol.length - baseName.length)
                if (index > 0) {
                    return "$baseName/" + symbol.substring(0, index)
                }
            }
            return symbol
        }
    }
}