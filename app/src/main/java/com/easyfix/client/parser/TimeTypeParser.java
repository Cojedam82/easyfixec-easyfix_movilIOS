package com.easyfix.client.parser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class TimeTypeParser implements JsonDeserializer<Long>, JsonSerializer<Long> {

    private static final String TIME_FORMAT = "HH:mm";

    @Override
    public Long deserialize(JsonElement jsonElement, Type typeOF,
                            JsonDeserializationContext context) throws JsonParseException {

        try {
            return jsonElement.isJsonNull()? null : new SimpleDateFormat(TIME_FORMAT)
                    .parse(jsonElement.getAsString()).getTime();
        } catch (ParseException ignore) {}

        throw new JsonParseException("Unparseable time: \"" + jsonElement.getAsString()
                + "\". Supported format: \n" + TIME_FORMAT);
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(new SimpleDateFormat(TIME_FORMAT).format(src));
    }

}
