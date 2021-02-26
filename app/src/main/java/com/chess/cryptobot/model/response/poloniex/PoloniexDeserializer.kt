package com.chess.cryptobot.model.response.poloniex

import com.google.gson.*
import java.lang.reflect.Type

class PoloniexDeserializer : JsonDeserializer<PoloniexResponse> {
    val gson = Gson()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PoloniexResponse {
        val response = PoloniexResponse()
        if (json.isJsonObject) {
            val jsonObject = json.asJsonObject
            if (jsonObject.has("error")) {
                response.error =jsonObject["error"].asString
                return response
            }
            response.data = jsonObject
            return response
        }

        return response
    }
}