package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.exceptions.BittrexException
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class BittrexTypeAdapter : TypeAdapter<BittrexResponse?>() {
    private val gson = Gson()
    override fun write(jsonWriter: JsonWriter, response: BittrexResponse?) {
        gson.toJson(response, BittrexResponse::class.java, jsonWriter)
    }

    override fun read(jsonReader: JsonReader): BittrexResponse? {
        var response: BittrexResponse
        return try {
            if (jsonReader.peek() == JsonToken.STRING) {
                throw BittrexException(jsonReader.nextString())
            }
            jsonReader.beginObject()
            jsonReader.nextName()
            val success = jsonReader.nextBoolean()
            jsonReader.nextName()
            val message = jsonReader.nextString()
            jsonReader.nextName()
            when {
                jsonReader.peek() == JsonToken.BEGIN_ARRAY -> {
                    response = BittrexResponse((gson.fromJson(jsonReader, Array<BittrexGenericResponse>::class.java) as Array<BittrexGenericResponse?>))
                }
                jsonReader.peek() == JsonToken.BEGIN_OBJECT -> {
                    response = BittrexResponse(gson.fromJson<Any>(jsonReader, BittrexGenericResponse::class.java) as BittrexGenericResponse)
                }
                jsonReader.peek() == JsonToken.NULL -> {
                    response = BittrexResponse(BittrexGenericResponse())
                    jsonReader.nextNull()
                }
                else -> {
                    throw JsonParseException("Unexpected token " + jsonReader.peek())
                }
            }
            response.setSuccess(success)
            response.setMessage(message)
            if (jsonReader.peek() == JsonToken.END_OBJECT) {
                jsonReader.endObject()
            } else if (jsonReader.peek() == JsonToken.NAME) {
                jsonReader.nextName()
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull()
                } else if (jsonReader.peek() == JsonToken.STRING) {
                    jsonReader.nextString()
                }
                jsonReader.endObject()
            }
            response
        } catch (e: Exception) {
            response = BittrexResponse(BittrexGenericResponse())
            response.setSuccess(false)
            response.setMessage(e.message)
            response
        }
    }
}