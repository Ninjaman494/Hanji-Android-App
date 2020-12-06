package com.a494studios.koreanconjugator.display;

import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.View;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.FavoritesQuery;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;
import com.a494studios.koreanconjugator.utils.Utils;
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

public class DisplayObserver extends DisposableObserver<FavoritesQuery.Data> {
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
    public void onNext(FavoritesQuery.Data favData) {
        // Favorites, hide the card and skip if there are none
        if (favData.favConjugations().isEmpty()) {
            conjugations.setVisibility(View.GONE);
        } else {
            List<FavoritesQuery.FavConjugation> conjugations = favData.favConjugations();
            boolean isAdj = entry.pos().equals("Adjective");
            Boolean regular = entry.regular();
            List<Favorite> favorites = Utils.getFavorites(displayCardView.getContext());

            FavoritesCard card = new FavoritesCard(new ArrayList<>(), entry.term(), false, isAdj, regular);
            this.conjugations.setCardBody(card);

            Observable.fromIterable(favorites)
                    .map(favorite -> {
                        // Pair up conjugations and favorites
                        for (FavoritesQuery.FavConjugation c : conjugations) {
                            ConjugationFragment fragment = c.fragments().conjugationFragment();

                            if (fragment.name().equals(favorite.getConjugationName())
                                    && fragment.honorific() == favorite.isHonorific()) {
                                return new Pair<>(favorite, c);
                            }
                        }

                        return new Pair<>(null, null);
                    })
                    .subscribe(pair -> {
                        if(pair.first != null && pair.second != null) {
                            Favorite f = (Favorite)pair.first;
                            FavoritesQuery.FavConjugation conjugation = (FavoritesQuery.FavConjugation) pair.second;

                            ConjugationFragment fragment = conjugation.fragments().conjugationFragment();
                            Map.Entry<String, ConjugationFragment> entry =
                                    new AbstractMap.SimpleEntry<>(f.getName(), fragment);

                            card.addConjugation(entry, favorites.indexOf(f));
                        }
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
        if(entry.examples() != null && !entry.examples().isEmpty()){
            examples.setCardBody(new ExamplesCard(entry.examples()));
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
