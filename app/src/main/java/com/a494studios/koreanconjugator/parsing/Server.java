package com.a494studios.koreanconjugator.parsing;

import androidx.test.espresso.idling.CountingIdlingResource;

import com.a494studios.koreanconjugator.ConjugationNamesQuery;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CreateSuggestionMutation;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.FavoritesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.StemQuery;
import com.a494studios.koreanconjugator.WordOfTheDayQuery;
import com.a494studios.koreanconjugator.type.EntrySuggestionInput;
import com.a494studios.koreanconjugator.type.ExampleInput;
import com.a494studios.koreanconjugator.type.FavInput;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloMutationCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.rx2.Rx2Apollo;

import java.io.File;
import java.util.Collections;
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
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Response<EntryQuery.Data>> doEntryQuery(final String id, CustomApplication app) {
        idler.increment();

        EntryQuery query = EntryQuery.builder().id(id).build();
        ApolloQueryCall<EntryQuery.Data> call = getApolloClient(app).query(query);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.getData() != null)
                .doAfterTerminate(() -> idler.decrement());
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
                .doAfterTerminate(() -> idler.decrement());
    }

    public static Observable<Response<FavoritesQuery.Data>> doFavoritesQuery(
            String stem, boolean isAdj, Boolean regular, List<FavInput> favorites,
            CustomApplication app) {
        idler.increment();

        FavoritesQuery.Builder builder = FavoritesQuery.builder()
                .stem(stem)
                .isAdj(isAdj)
                .favorites(favorites);
        if (regular != null) {
            builder.regular(regular);
        }

        ApolloQueryCall<FavoritesQuery.Data> call = getApolloClient(app)
                .query(builder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.getData() != null)
                .doAfterTerminate(() -> idler.decrement());
    }

    public static Observable<Response<ConjugationNamesQuery.Data>> doConjugationNamesQuery(CustomApplication app) {
        ApolloQueryCall<ConjugationNamesQuery.Data> call = getApolloClient(app)
                .query(new ConjugationNamesQuery());

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.getData() != null));
    }

    public static Observable<Response<WordOfTheDayQuery.Data>> doWODQuery(CustomApplication app) {
        idler.increment();

        WordOfTheDayQuery.Builder queryBuilder = WordOfTheDayQuery.builder();

        ApolloQueryCall<WordOfTheDayQuery.Data> call = getApolloClient(app)
                .query(queryBuilder.build());

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> idler.decrement());
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
                .filter((dataResponse -> dataResponse.getData() != null));
    }

    public static Observable<Response<CreateSuggestionMutation.Data>> createSuggestion
            (String entryID, String antonym, String synonym, ExampleInput example, CustomApplication app){

        List<String> antonyms = null;
        List<String> synonyms = null;
        List<ExampleInput> examples =  null;
        if(antonym != null && antonym.length() > 0) {
            antonyms = Collections.singletonList(antonym);
        }
        if(synonym != null && synonym.length() > 0) {
            synonyms = Collections.singletonList(synonym);
        }
        if(example != null) {
            examples = Collections.singletonList(example);
        }
        return createSuggestion(entryID, antonyms, synonyms, examples, app);
    }

    public static Observable<Response<CreateSuggestionMutation.Data>> createSuggestion
            (String entryID, List<String> antonyms, List<String> synonyms, List<ExampleInput> examples,
             CustomApplication app) {
        idler.increment();

        EntrySuggestionInput.Builder inputBuilder = EntrySuggestionInput.builder()
                .entryID(entryID);

        if (antonyms != null && !antonyms.isEmpty()) {
            inputBuilder.antonyms(antonyms);
        }
        if (synonyms != null && !synonyms.isEmpty()) {
            inputBuilder.synonyms(synonyms);
        }
        if (examples != null && !examples.isEmpty()) {
            inputBuilder.examples(examples);
        }

        CreateSuggestionMutation mutation = CreateSuggestionMutation.builder()
                .suggestion(inputBuilder.build())
                .build();

        ApolloMutationCall<CreateSuggestionMutation.Data> call = getApolloClient(app)
                .mutate(mutation);

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.getData() != null))
                .doAfterTerminate(() -> idler.decrement());
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