package com.a494studios.koreanconjugator.conjugations;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.ConjugationQuery;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.ConjugationCard;

import java.util.List;

public class ConjugationCardsAdapter extends RecyclerView.Adapter<ConjugationCardsAdapter.ViewHolder> {

    private  List<List<ConjugationQuery.Conjugation>> conjugations;

    public ConjugationCardsAdapter(List<List<ConjugationQuery.Conjugation>> conjugations) {
        this.conjugations = conjugations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DisplayCardView conjView = new DisplayCardView(parent.getContext());
        conjView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(conjView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConjugationCard card = new ConjugationCard(conjugations.get(position));
        holder.displayCardView.setCardBody(card);
    }

    @Override
    public int getItemCount() {
        return conjugations.size();
    }

    public List<ConjugationQuery.Conjugation> getItem(int i) {
        return conjugations.get(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        DisplayCardView displayCardView;

        ViewHolder(@NonNull DisplayCardView itemView) {
            super(itemView);
            displayCardView = itemView;
        }
    }
}
