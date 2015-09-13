package com.nbsp.translator.api.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nbsp.translator.models.Language;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickolay on 13.09.15.
 */

public class LanguageDeserializer implements JsonDeserializer<List<Language>> {
    @Override
    public List<Language> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Language> languages = new ArrayList<>();
        JsonElement content = json.getAsJsonObject().get("langs");

        return languages;

    }

}