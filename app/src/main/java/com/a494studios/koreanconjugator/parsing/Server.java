package com.a494studios.koreanconjugator.parsing;

import com.a494studios.koreanconjugator.ConjugationNamesQuery;
import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.WordOfTheDayQuery;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.rx2.Rx2Apollo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by akash on 12/31/2017.
 */

public class Server {

    public static Observable<Response<SearchQuery.Data>> doSearchQuery(final String query){
        return doSearchQuery(query, 0);
    }

    public static Observable<Response<SearchQuery.Data>> doSearchQuery(String query, int cursor) {
        SearchQuery.Builder queryBuilder = SearchQuery.builder()
                .query(query)
                .cursor(cursor);

        ApolloQueryCall<SearchQuery.Data> call = CustomApplication.getApolloClient()
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);
    }

    public static Observable<Response<EntryQuery.Data>> doEntryQuery(final String id) {
        EntryQuery query = EntryQuery.builder().id(id).build();
        ApolloQueryCall<EntryQuery.Data> call = CustomApplication.getApolloClient().query(query);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);
    }

    public static Observable<Response<ConjugationQuery.Data>> doConjugationQuery(String stem, boolean honorific, boolean isAdj, Boolean regular){
        return doConjugationQuery(stem,honorific,isAdj,regular, null);
    }

    public static Observable<Response<ConjugationQuery.Data>> doConjugationQuery(String stem, boolean honorific, boolean isAdj, Boolean regular, List<String> conjugations){
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

        ApolloQueryCall<ConjugationQuery.Data> call = CustomApplication.getApolloClient()
                .query(queryBuilder.build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);
        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);
    }

    public static Observable<Response<ExamplesQuery.Data>> doExamplesQuery(final String id) {
       ApolloQueryCall<ExamplesQuery.Data> call =  CustomApplication.getApolloClient()
                .query(new ExamplesQuery(id))
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST);
       return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse) -> dataResponse.data() != null);

    }

    public static void doConjugationNamesQuery(ApolloCall.Callback<ConjugationNamesQuery.Data> callback) {
        CustomApplication.getApolloClient()
                .query(new ConjugationNamesQuery())
                .enqueue(callback);
    }

    public static Observable<Response<WordOfTheDayQuery.Data>> doWODQuery() {
        WordOfTheDayQuery.Builder queryBuilder = WordOfTheDayQuery.builder();

        ApolloQueryCall<WordOfTheDayQuery.Data> call = CustomApplication.getApolloClient()
                .query(queryBuilder.build());

        return Rx2Apollo.from(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((dataResponse -> dataResponse.data() != null));
    }
}