package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.exceptions.BittrexException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class BittrexTypeAdapter extends TypeAdapter<BittrexResponse> {

    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, BittrexResponse response) {
        gson.toJson(response, BittrexResponse.class, jsonWriter);
    }

    @Override
    public BittrexResponse read(JsonReader jsonReader) {
        BittrexResponse response;

        try {
            if (jsonReader.peek() == JsonToken.STRING) {
                throw new BittrexException(jsonReader.nextString());
            }
            jsonReader.beginObject();
            jsonReader.nextName();
            Boolean success = jsonReader.nextBoolean();
            jsonReader.nextName();
            String message = jsonReader.nextString();
            jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                response = new BittrexResponse((BittrexGenericResponse[]) gson.fromJson(jsonReader, BittrexGenericResponse[].class));
            } else if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                response = new BittrexResponse((BittrexGenericResponse) gson.fromJson(jsonReader, BittrexGenericResponse.class));
            } else if (jsonReader.peek() == JsonToken.NULL) {
                response = new BittrexResponse(new BittrexGenericResponse());
                jsonReader.nextNull();
            } else {
                throw new JsonParseException("Unexpected token " + jsonReader.peek());
            }

            response.setSuccess(success);
            response.setMessage(message);

            if (jsonReader.peek() == JsonToken.END_OBJECT) {
                jsonReader.endObject();
            } else if (jsonReader.peek() == JsonToken.NAME) {
                jsonReader.nextName();
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                } else if (jsonReader.peek() == JsonToken.STRING) {
                    jsonReader.nextString();
                }
                jsonReader.endObject();
            }
            return response;
        }catch (Exception e) {
            response = new BittrexResponse(new BittrexGenericResponse());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
