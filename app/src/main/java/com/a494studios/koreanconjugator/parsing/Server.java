package com.a494studios.koreanconjugator.parsing;

import android.content.Context;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.crashlytics.android.Crashlytics;
import com.google.common.net.UrlEscapers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {
    private static final String serverURL = com.a494studios.koreanconjugator.BuildConfig.SERVER_URL;
    //private static final String serverURL = "http://localhost:8080/";
    private static final String conjURL = serverURL + "conjugate=";
    private static final String searchKorURL = serverURL + "searchKor=";
    private static final String defKorURL = serverURL + "defineKor=";
    private static final String defEngURL = serverURL + "defineEng=";
    private static final String stemURL = serverURL + "stem=";

    private static final String KEY_CONJ_INFIN = "infinitive";
    private static final String KEY_CONJ_TYPE = "conjugation_name";
    private static final String KEY_CONJ_CONJ = "conjugated";
    private static final String KEY_CONJ_PRONC = "pronunciation";
    private static final String KEY_CONJ_ROMAN = "romanized";
    private static final String KEY_SEARCH_WORD = "key";
    private static final String KEY_SEARCH_DEF = "def";


    public static void doSearchQuery(final String query, ApolloCall.Callback<SearchQuery.Data> callback){
        CustomApplication.getApolloClient()
                .query(new SearchQuery(query))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void doEntryQuery(final String id, ApolloCall.Callback<EntryQuery.Data> callback){
        CustomApplication.getApolloClient().query(EntryQuery.builder().id(id).build()).enqueue(callback);
    }

    public static void doConjugationQuery(String stem, boolean honorific, boolean isAdj, ApolloCall.Callback<ConjugationQuery.Data> callback){
        CustomApplication.getApolloClient()
                .query(new ConjugationQuery(stem,honorific,isAdj))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void doExamplesQuery(final String id, ApolloCall.Callback<ExamplesQuery.Data> callback) {
        CustomApplication.getApolloClient()
                .query(new ExamplesQuery(id))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void requestKoreanSearch(final String kword, final Context context, final ServerListener listener){
        String encoded = UrlEscapers.urlFragmentEscaper().escape(searchKorURL + kword);// Convert to %-encoding
        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, encoded, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               if(response.toString().contains("\"type\":")) {
                   ArrayList<Conjugation> conjugations = parseConjugations(response);
                   if(conjugations == null){
                       listener.onErrorOccurred(new JSONException("Something went wrong with JSON parsing"));
                   }else{
                       listener.onResultReceived(conjugations,null);
                   }
               }else if(response.toString().contains("\"key\":")){
                   HashMap<String,String> results = parseSearchResults(response);
                   if(results == null){
                       listener.onErrorOccurred(new JSONException("Something went wrong with JSON parsing"));
                   }else{
                       listener.onResultReceived(null,results);
                   }
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.setString("requestType","koreanSearch");
                Crashlytics.setString("word",kword);
                Crashlytics.logException(error);
                listener.onErrorOccurred(error);
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
    }

    public static void requestConjugation(final String kword, Context context, final ServerListener listener) {
        String encoded = UrlEscapers.urlFragmentEscaper().escape(conjURL + kword);// Convert to %-encoding
        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, encoded, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                listener.onResultReceived(parseConjugations(response),null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.setString("requestType","conjugate");
                Crashlytics.setString("word",kword);
                Crashlytics.logException(error);
                listener.onErrorOccurred(error);
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
    }

    private static ArrayList<Conjugation> parseConjugations(JSONArray response){
        try {
            ArrayList<Conjugation> conjugations = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = ((JSONObject) response.get(i));
                String infin = object.getString(KEY_CONJ_INFIN);
                String type = object.getString(KEY_CONJ_TYPE);
                String conj = object.getString(KEY_CONJ_CONJ);
                String pronc = object.getString(KEY_CONJ_PRONC);
                String roman = object.getString(KEY_CONJ_ROMAN);
                conjugations.add(new Conjugation(infin, type, conj, pronc, roman));
            }
            return conjugations;
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, String> parseSearchResults(JSONArray response){
        try {
            HashMap<String,String> results = new HashMap<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = ((JSONObject) response.get(i));
                String key = object.getString(KEY_SEARCH_WORD);
                String value = object.getString(KEY_SEARCH_DEF);
                results.put(key,value);
            }
            return results;
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static void requestKorDefinition(final String word, Context context, final DefinitionListener listener) {
        String encoded = UrlEscapers.urlFragmentEscaper().escape(defKorURL + word); // Convert to %-encoding
        StringRequest jsRequest = new StringRequest(Request.Method.GET, encoded, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onDefinitionReceived(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.setString("requestType","koreanDefinition");
                Crashlytics.setString("word",word);
                Crashlytics.logException(error);
                listener.onErrorOccurred(error);
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
    }

    public static void requestEngDefinition(final String word, Context context, final ServerListener listener){
        String encoded = UrlEscapers.urlFragmentEscaper().escape(defEngURL + word); // Needed for spaces
        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, encoded, null,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    HashMap<String, String> entries = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        String key = response.getString(i);
                        entries.put(key,null);
                    }
                    listener.onResultReceived(null,entries);
                }catch (JSONException e){
                    e.printStackTrace();
                    listener.onErrorOccurred(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.setString("requestType","engDefinition");
                Crashlytics.setString("word",word);
                Crashlytics.logException(error);
                listener.onErrorOccurred(error);
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
    }

    public interface ServerListener {
        void onResultReceived(ArrayList<Conjugation> conjugations, HashMap<String, String> searchResults);
        void onErrorOccurred(Exception error);
    }

    public interface DefinitionListener {
        void onDefinitionReceived(String definition);
        void onErrorOccurred(Exception error);
    }
}
