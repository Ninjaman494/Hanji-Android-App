package com.a494studios.koreanconjugator.parsing;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
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

public class EntrySerializer implements JsonDeserializer<Map.Entry<String,Category[]>> {
    @Override
    public Map.Entry<String, Category[]> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Crashlytics.log("Parsing Json Object: "+jsonObject.toString());
        String key = jsonObject.get("key").getAsString();
        Crashlytics.setString("Key",key);
        JsonArray value = jsonObject.get("value").getAsJsonArray();
        Crashlytics.setString("value",value.toString());
        Category[] categories = new Category[3];
        for(int i =0;i<value.size();i++){
            String catString = value.get(i).getAsString();
            categories[i] = Category.Categories.valueOf(catString);
        }
        return new AbstractMap.SimpleEntry<>(key,categories);
    }
}
