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
        val response = BinanceResponse()
        if (json.isJsonArray) {
            val binanceType = object : TypeToken<List<BinanceResponse>>() {}.type
            response.responsesList = context.deserialize<List<BinanceResponse>>(json, binanceType)
            return response
        }

        if (json.isJsonObject) {
            val jsonObject = json.asJsonObject
            if (jsonObject.keySet().contains("assetDetail")) {
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
                response.assetDetails = assets
            }else {
                return gson.fromJson(json, BinanceResponse::class.java)
            }
        }

        return response
    }
}