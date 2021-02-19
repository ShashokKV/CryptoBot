package com.chess.cryptobot.model.response.livecoin

import com.google.gson.*
import java.lang.reflect.Type

class PoloniexDeserializer : JsonDeserializer<PoloniexResponse> {
    val gson = Gson()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PoloniexResponse {
        val response = PoloniexResponse()
        if (json.isJsonObject) {
            if (json.asJsonObject.has("error")) {
                response.error = json.asJsonObject["error"].asString
                return response
            }
        }
        if (json.isJsonArray) {
            response.arrayData = json.asJsonArray
            return response
        }

        if (json.isJsonObject) {
            response.objectData = json.asJsonObject
            return response
        }

        return response
    }
}