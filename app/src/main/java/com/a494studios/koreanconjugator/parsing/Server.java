package com.a494studios.koreanconjugator.parsing;

import androidx.test.espresso.idling.CountingIdlingResource;

import com.a494studios.koreanconjugator.ConjugationNamesQuery;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.StemQuery;
import com.a494studios.koreanconjugator.WordOfTheDayQuery;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.rx2.Rx2Apollo;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {
    private static CountingIdlingResource idler = CustomApplication.getIdler();
    private static ApolloClient apolloClient;

    public static Observable<Response<SearchQuery.Data>> doSearchQuery(final String query, CustomApplication app){
        return doSearchQuery(query, 0, app);
    }

    public static Observable<Response<SearchQuery.Data>> doSearchQuery(String query, int cursor, CustomApplication app) {
        SearchQuery.Builder queryBuilder = SearchQuery.builder()
                .query(query)
                .cursor(cursor);

        ApolloQueryCall<SearchQuery.Data> call = getApolloClient(app)
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);
    }

    public static Observable<Response<EntryQuery.Data>> doEntryQuery(final String id, CustomApplication app) {
        idler.increment();

        EntryQuery query = EntryQuery.builder().id(id).build();
        ApolloQueryCall<EntryQuery.Data> call = getApolloClient(app).query(query);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null)
                .doFinally(() -> idler.decrement());
    }

    public static Observable<Response<ConjugationQuery.Data>> doConjugationQuery(
            String stem, boolean honorific, boolean isAdj, Boolean regular, CustomApplication app) {
        return doConjugationQuery(stem, honorific, isAdj, regular, null, app);
    }

    public static Observable<Response<ConjugationQuery.Data>> doConjugationQuery(
            String stem, boolean honorific, boolean isAdj, Boolean regular, List<String> conjugations,
            CustomApplication app){
        idler.increment();

        ConjugationQuery.Builder queryBuilder = ConjugationQuery.builder()
                .stem(stem)
                .honorific(honorific)
                .isAdj(isAdj);
        if(conjugations != null) {
            queryBuilder.conjugations(conjugations);
        }
        if(regular != null) {
            queryBuilder.regular(regular);
        }

        ApolloQueryCall<ConjugationQuery.Data> call = getApolloClient(app)
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null)
                .doFinally(() -> idler.decrement());
    }

    public static Observable<Response<ExamplesQuery.Data>> doExamplesQuery(final String id, CustomApplication app) {
       ApolloQueryCall<ExamplesQuery.Data> call =  getApolloClient(app)
                .query(new ExamplesQuery(id))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);
       return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);

    }

    public static Observable<Response<ConjugationNamesQuery.Data>> doConjugationNamesQuery(CustomApplication app) {
        ApolloQueryCall<ConjugationNamesQuery.Data> call = getApolloClient(app)
                .query(new ConjugationNamesQuery());

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.data() != null));
    }

    public static Observable<Response<WordOfTheDayQuery.Data>> doWODQuery(CustomApplication app) {
        idler.increment();

        WordOfTheDayQuery.Builder queryBuilder = WordOfTheDayQuery.builder();

        ApolloQueryCall<WordOfTheDayQuery.Data> call = getApolloClient(app)
                .query(queryBuilder.build());

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.data() != null))
                .doFinally(() -> idler.decrement());
    }

    public static Observable<Response<StemQuery.Data>> doStemQuery(String term, CustomApplication app) {
        StemQuery query = StemQuery.builder()
                .term(term)
                .build();

        ApolloQueryCall<StemQuery.Data> call = getApolloClient(app)
                .query(query);

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.data() != null));
    }

    private static ApolloClient getApolloClient(CustomApplication app) {
        if(apolloClient == null) {
            // Setup response cache for Apollo
            File file = app.getCacheDir();
            int size = 1024*1024;
            DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file,size);

            //Build the Apollo Client
            String serverUrl = app.getServerUrl();
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            apolloClient =  ApolloClient.builder()
                    .serverUrl(serverUrl)
                    .okHttpClient(okHttpClient)
                    .httpCache(new ApolloHttpCache(cacheStore))
                    .build();
        }

        return apolloClient;
    }

    public static CountingIdlingResource getIdler() {
        return idler;
    }
}