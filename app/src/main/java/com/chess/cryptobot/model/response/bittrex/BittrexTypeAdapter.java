package com.chess.cryptobot.model.response.bittrex;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BittrexTypeAdapter extends TypeAdapter<BittrexResponse> {

    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, BittrexResponse response) {
        gson.toJson(response, BittrexResponse.class, jsonWriter);
    }

    @Override
    public BittrexResponse read(JsonReader jsonReader) throws IOException {
        BittrexResponse response;

        jsonReader.beginObject();
        jsonReader.nextName();
        Boolean success = jsonReader.nextBoolean();
        jsonReader.nextName();
        String message = jsonReader.nextString();
        jsonReader.nextName();

        if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
            response = new BittrexResponse((BittrexGenericResponse[]) gson.fromJson(jsonReader, BittrexGenericResponse[].class));
        } else if(jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
            response = new BittrexResponse((BittrexGenericResponse) gson.fromJson(jsonReader, BittrexGenericResponse.class));
        }else if(jsonReader.peek() == JsonToken.NULL) {
            response = new BittrexResponse(new BittrexGenericResponse());
            jsonReader.nextNull();
        } else {
            throw new JsonParseException("Unexpected token " + jsonReader.peek());
        }

        response.setSuccess(success);
        response.setMessage(message);

        jsonReader.endObject();
        return response;
    }
}
