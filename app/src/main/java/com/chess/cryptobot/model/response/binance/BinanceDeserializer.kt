package com.chess.cryptobot.model.response.binance

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class BinanceDeserializer : JsonDeserializer<BinanceResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BinanceResponse {
        val gson = Gson()
        if (json.isJsonArray) {
            val response  = BinanceResponse()
            val binanceType = object : TypeToken<List<BinanceResponse>>() {}.type
            response.responsesList = context.deserialize<List<BinanceResponse>>(json, binanceType)
            return response
        }

        val jsonObject = json.asJsonObject
        if (!jsonObject.keySet().contains("assetDetail")) {
            return gson.fromJson(json, BinanceResponse::class.java)
        }

        val assets: MutableList<AssetDetail> = ArrayList()
        for (entry in jsonObject.entrySet()) {
            if (entry.key == "assetDetail") {
                val assetObject = entry.value.asJsonObject
                for (asset in assetObject.entrySet()) {
                    val assetDetail = gson.fromJson(asset.value, AssetDetail::class.java)
                    assetDetail.currencyName = asset.key
                    assets.add(assetDetail)
                }
            }
        }

        val response = BinanceResponse()
        response.assetDetails = assets
        return response
    }
}