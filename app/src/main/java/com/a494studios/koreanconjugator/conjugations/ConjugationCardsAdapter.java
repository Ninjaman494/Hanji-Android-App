package com.a494studios.koreanconjugator.conjugations;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.ConjugationCard;
import com.a494studios.koreanconjugator.fragment.ConjugationFragment;

import java.util.List;
import java.util.Objects;

public class ConjugationCardsAdapter extends RecyclerView.Adapter<ConjugationCardsAdapter.ViewHolder> {

    private List<List<ConjugationFragment>> conjugations;
    private String term;
    private String pos;

    public ConjugationCardsAdapter(List<List<ConjugationFragment>> conjugations, String term, String pos) {
        this.conjugations = Objects.requireNonNull(conjugations, "conjugations can't be null");
        this.term = Objects.requireNonNull(term, "term can't be null");
        this.pos = Objects.requireNonNull(pos, "pos can't be null");
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
        ConjugationCard card = new ConjugationCard(conjugations.get(position), term, pos);
        holder.displayCardView.setCardBody(card);
    }

    @Override
    public int getItemCount() {
        return conjugations.size();
    }

    public List<ConjugationFragment> getItem(int i) {
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
