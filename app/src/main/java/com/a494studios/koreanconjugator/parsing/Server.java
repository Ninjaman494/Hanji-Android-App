package com.a494studios.koreanconjugator.parsing;

import com.a494studios.koreanconjugator.ConjugationNamesQuery;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;

import java.util.List;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {

    public static void doSearchQuery(final String query, ApolloCall.Callback<SearchQuery.Data> callback){
        doSearchQuery(query, null, callback);
    }

    public static void doSearchQuery(String query, String cursor, ApolloCall.Callback<SearchQuery.Data> callback) {
        SearchQuery.Builder queryBuilder = SearchQuery.builder()
                .query(query);
        if(cursor != null) {
            queryBuilder.cursor(cursor);
        }

        CustomApplication.getApolloClient()
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void doEntryQuery(final String id, ApolloCall.Callback<EntryQuery.Data> callback){
        CustomApplication.getApolloClient().query(EntryQuery.builder().id(id).build()).enqueue(callback);
    }

    public static void doConjugationQuery(String stem, boolean honorific, boolean isAdj, ApolloCall.Callback<ConjugationQuery.Data> callback){
        doConjugationQuery(stem,honorific,isAdj,null,callback);
    }

    public static void doConjugationQuery(String stem, boolean honorific, boolean isAdj, List<String> conjugations, ApolloCall.Callback<ConjugationQuery.Data> callback){
        ConjugationQuery.Builder queryBuilder = ConjugationQuery.builder()
                .stem(stem)
                .honorific(honorific)
                .isAdj(isAdj);
        if(conjugations != null) {
            queryBuilder.conjugations(conjugations);
        }

        CustomApplication.getApolloClient()
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void doExamplesQuery(final String id, ApolloCall.Callback<ExamplesQuery.Data> callback) {
        CustomApplication.getApolloClient()
                .query(new ExamplesQuery(id))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(callback);
    }

    public static void doConjugationNamesQuery(ApolloCall.Callback<ConjugationNamesQuery.Data> callback) {
        CustomApplication.getApolloClient()
                .query(new ConjugationNamesQuery())
                .enqueue(callback);
    }
}