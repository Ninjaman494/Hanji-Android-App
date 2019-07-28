package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.View;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.ExamplesQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.a494studios.koreanconjugator.display.cards.DefPOSCard;
import com.a494studios.koreanconjugator.display.cards.ExamplesCard;
import com.a494studios.koreanconjugator.display.cards.FavoritesCard;
import com.a494studios.koreanconjugator.display.cards.NoteCard;
import com.a494studios.koreanconjugator.display.cards.SynAntCard;
import com.a494studios.koreanconjugator.parsing.Favorite;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class DisplayObserver extends DisposableObserver<Pair<ConjugationQuery.Data, ExamplesQuery.Data>> {
    private DisplayCardView displayCardView;
    private DisplayCardView note;
    private DisplayCardView examples;
    private DisplayCardView synonyms;
    private DisplayCardView antonyms;
    private DisplayCardView conjugations;

    private EntryQuery.Entry entry;
    private DisplayObserverInterface listener;

    DisplayObserver(View rootView, DisplayObserverInterface listener) {
        this.displayCardView = rootView.findViewById(R.id.disp_dcv);
        this.note = rootView.findViewById(R.id.disp_noteCard);
        this.examples = rootView.findViewById(R.id.disp_examplesCard);
        this.synonyms = rootView.findViewById(R.id.disp_synCard);
        this.antonyms = rootView.findViewById(R.id.disp_antCard);
        this.conjugations = rootView.findViewById(R.id.disp_conjCard);
        this.listener = listener;
    }

    public void setEntry(EntryQuery.Entry entry) {
        this.entry = entry;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onNext(Pair<ConjugationQuery.Data,ExamplesQuery.Data> response) {
        ConjugationQuery.Data conjData = response.first;
        ExamplesQuery.Data examplesData = response.second;

        // Favorites, skip if not a Verb or Adjective
        if (conjData == null) {
            conjugations.setVisibility(View.GONE);
        } else {
            List<ConjugationQuery.Conjugation> conjugations = conjData.conjugations();
            boolean isAdj = entry.pos().equals("Adjective");
            List<Favorite> favorites = Utils.getFavorites(displayCardView.getContext());

            FavoritesCard card = new FavoritesCard(new ArrayList<>(), entry.term(), false, isAdj);
            this.conjugations.setCardBody(card);
            Observable.fromIterable(favorites)
                    .map(favorite -> {
                        // Pair up conjugations and favorites
                        for (ConjugationQuery.Conjugation c : conjugations) {
                            if (c.name().equals(favorite.getConjugationName())) {
                                return new Pair<>(favorite, c);
                            }
                        }
                        return null;
                    })
                    .subscribe(pair -> {
                        Favorite f = pair.first;
                        ConjugationQuery.Conjugation conjugation = pair.second;
                        Map.Entry<String, ConjugationQuery.Conjugation> entry =
                                new AbstractMap.SimpleEntry<>(f.getName(), conjugation);
                        card.addConjugation(entry, favorites.indexOf(f));
                    });
        }

        // Definitions and POS
        displayCardView.setCardBody(new DefPOSCard(entry.term(),entry.pos(),entry.definitions()));

        // Note
        if(entry.note() != null ) {
            note.setCardBody(new NoteCard(entry.note()));
        } else {
            note.setVisibility(View.GONE);
        }

        // Synonyms and Antonyms
        if(entry.synonyms() != null) {
            synonyms.setCardBody(new SynAntCard(entry.synonyms(),true));
        } else {
            synonyms.setVisibility(View.GONE);
        }
        if(entry.antonyms() != null ) {
            antonyms.setCardBody(new SynAntCard(entry.antonyms(),false));
        } else {
            antonyms.setVisibility(View.GONE);
        }

        // Examples
        if(examplesData.examples() != null && !examplesData.examples().isEmpty()){
            examples.setCardBody(new ExamplesCard(examplesData.examples()));
        } else {
            examples.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(Throwable e) {
        listener.onError(e);
    }

    @Override
    public void onComplete() {
        listener.onComplete();
        this.dispose();
    }

    public interface DisplayObserverInterface {
        void onError(Throwable t);
        void onComplete();
    }
}
