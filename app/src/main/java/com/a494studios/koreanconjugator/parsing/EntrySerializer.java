package com.a494studios.koreanconjugator.parsing;

import com.crashlytics.android.Crashlytics;
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
public class EntrySerializer implements JsonDeserializer<Map.Entry<String,String>> {
    @Override
    public Map.Entry<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        //Crashlytics.log("Parsing Json Object: "+jsonObject.toString());
        String key = jsonObject.get("key").getAsString();
        //Crashlytics.setString("Key",key);
        String value = jsonObject.get("value").getAsString();
        //Crashlytics.setString("value",value);
        return new AbstractMap.SimpleEntry<>(key,value);
    }
}
