package com.a494studios.koreanconjugator.parsing;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {

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
}