package com.a494studios.koreanconjugator.parsing;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by akash on 1/13/2018.
 */
public class FavoriteSerializer implements JsonDeserializer<Favorite> {
    @Override
    public Favorite deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        FirebaseCrashlytics.getInstance().log("Parsing Json Object: "+jsonObject.toString());
        String name = jsonObject.get("name").getAsString();
        FirebaseCrashlytics.getInstance().setCustomKey("Name", name);
        String conjugationName = jsonObject.get("conjugationName").getAsString();
        FirebaseCrashlytics.getInstance().setCustomKey("Conjugation Name", conjugationName);
        boolean honorific = jsonObject.get("honorific").getAsBoolean();
        return new Favorite(name,conjugationName,honorific);
    }
}
