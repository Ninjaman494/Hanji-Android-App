package com.a494studios.koreanconjugator.parsing;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.net.UrlEscapers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {
    private static final String serverURL = "http://192.168.1.9:3000/";
    private static final String searchURL = serverURL + "search=";
    private static final String defKorURL = serverURL + "defineKor=";

    private static final String KEY_CONJ_INFIN = "infinitive";
    private static final String KEY_CONJ_TYPE = "conjugation_name";
    private static final String KEY_CONJ_CONJ = "conjugated";
    private static final String KEY_CONJ_PRONC = "pronunciation";
    private static final String KEY_CONJ_ROMAN = "romanized";

    public static void requestConjugation(final String kword, Context context, final ServerListener listener) {
        String encoded = UrlEscapers.urlFragmentEscaper().escape(searchURL + kword);// Convert to %-encoding
        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, encoded, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
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
                    listener.onConjugationReceived(conjugations);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onErrorOccurred(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorOccurred(error.getMessage());
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
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
                listener.onErrorOccurred(error.getMessage());
            }
        });
        Volley.newRequestQueue(context).add(jsRequest);
    }

    public interface ServerListener {
        void onConjugationReceived(ArrayList<Conjugation> conjugations);
        void onErrorOccurred(String errorMsg);
    }

    public interface DefinitionListener {
        void onDefinitionReceived(String definition);
        void onErrorOccurred(String errorMsg);
    }
}
