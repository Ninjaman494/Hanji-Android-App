package com.a494studios.koreanconjugator.conjugations;

import com.a494studios.koreanconjugator.ConjugationQuery;
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
        if(response.data() == null) {
            return;
        }

        List<ConjugationQuery.Conjugation> conjugations = response.data().conjugations();
        final TreeMap<String,List<ConjugationQuery.Conjugation>> conjMap = new TreeMap<>();
        for(ConjugationQuery.Conjugation conjugation : conjugations){
            String type = conjugation.type();
            if(conjMap.containsKey(type)){
                conjMap.get(type).add(conjugation);
            }else{
                List<ConjugationQuery.Conjugation> value = new ArrayList<>();
                value.add(conjugation);
                conjMap.put(type,value);
            }
        }

        List<List<ConjugationQuery.Conjugation>> sortedConj = new ArrayList<>(conjMap.values());
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
        void onDataReceived(List<List<ConjugationQuery.Conjugation>> conjugations);

        void onError(Throwable e);
    }
}

