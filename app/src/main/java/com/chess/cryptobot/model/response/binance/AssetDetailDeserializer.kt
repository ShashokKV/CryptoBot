package com.chess.cryptobot.model.response.binance

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AssetDetailDeserializer : JsonDeserializer<BinanceResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BinanceResponse {
        val jsonObject = json.asJsonObject
        if (!jsonObject.keySet().contains("assetDetail")) {
            val response  = BinanceResponse()
            return if (json.isJsonArray) {
                val binanceType = object : TypeToken<List<BinanceResponse>>(){}.type
                response.responsesList = context.deserialize<List<BinanceResponse>>(json, binanceType)
                response
            } else {
                context.deserialize(json, BinanceResponse::class.java)
            }
        }

        val assets: MutableList<BinanceCurrenciesListResponse.AssetDetail> = ArrayList()
        for (entry in jsonObject.entrySet()) {
            if (entry.key == "assetDetail") {
                val assetObject = entry.value.asJsonObject
                for (asset in assetObject.entrySet()) {
                    val assetDetail = context.deserialize<BinanceCurrenciesListResponse.AssetDetail>(asset.value, BinanceCurrenciesListResponse.AssetDetail::class.java)
                    assetDetail.currencyName = asset.key
                    assets.add(assetDetail)
                }
            }
        }
        val response = BinanceCurrenciesListResponse()
        response.assetDetails = assets
        return response
    }
}