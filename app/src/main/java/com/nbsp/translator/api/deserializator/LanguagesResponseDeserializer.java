package com.nbsp.translator.api.deserializator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nbsp.translator.models.Language;
import com.nbsp.translator.models.TranslationDirection;

import java.lang.reflect.Type;

/**
 * Created by nickolay on 14.09.15.
 */

public class LanguagesResponseDeserializer implements JsonDeserializer<TranslationDirection> {
    @Override
    public TranslationDirection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String[] pair = json.getAsString().split("-");
        return new TranslationDirection(
                new Language(pair[0]),
                new Language(pair[1])
        );
    }
}
