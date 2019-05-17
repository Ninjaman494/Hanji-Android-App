package com.a494studios.koreanconjugator.parsing;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by akash on 1/13/2018.
 */
//TODO reimplement Crashlytics
public class FavoriteSerializer implements JsonDeserializer<Favorite> {
    @Override
    public Favorite deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        //Crashlytics.log("Parsing Json Object: "+jsonObject.toString());
        String name = jsonObject.get("name").getAsString();
        //Crashlytics.setString("Key",key);
        String conjugationName = jsonObject.get("conjugationName").getAsString();
        //Crashlytics.setString("value",value);
        boolean honorific = jsonObject.get("honorific").getAsBoolean();
        return new Favorite(name,conjugationName,honorific);
    }
}
