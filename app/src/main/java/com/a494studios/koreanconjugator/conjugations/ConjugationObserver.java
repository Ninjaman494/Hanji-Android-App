package com.a494studios.koreanconjugator.conjugations;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.observers.DisposableObserver;

public class ConjugationObserver extends DisposableObserver<Response<ConjugationQuery.Data>> {
    private ConjugationObserverListener listener;

    public ConjugationObserver(ConjugationObserverListener listener) {
        this.listener = listener;
    }

    @Override
    public void onNext(Response<ConjugationQuery.Data> response) {
        if(response.hasErrors()) {
            listener.onApiError(response.getErrors().get(0));
            return;
        }

        List<ConjugationQuery.Conjugation> conjugations = response.getData().conjugations();

        final TreeMap<String,List<ConjugationFragment>> conjMap = new TreeMap<>();
        for(ConjugationQuery.Conjugation conjugation : conjugations){
            ConjugationFragment fragment = conjugation.fragments().conjugationFragment();

            String type = fragment.type();
            if(conjMap.containsKey(type)){
                conjMap.get(type).add(fragment);
            }else{
                List<ConjugationFragment> value = new ArrayList<>();
                value.add(fragment);
                conjMap.put(type,value);
            }
        }

        List<List<ConjugationFragment>> sortedConj = new ArrayList<>(conjMap.values());
        listener.onDataReceived(sortedConj);
    }

    @Override
    public void onError(Throwable e) {
        listener.onError(e);
    }

    @Override
    public void onComplete() {

    }


    public interface ConjugationObserverListener {
        void onDataReceived(List<List<ConjugationFragment>> conjugations);

        void onError(Throwable e);

        void onApiError(Error e);
    }
}

