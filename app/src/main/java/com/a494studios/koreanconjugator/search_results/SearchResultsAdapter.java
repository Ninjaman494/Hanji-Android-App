package com.a494studios.koreanconjugator.search_results;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.utils.WordInfoView;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SearchQuery.Result> results;
    private Context context;

    SearchResultsAdapter(Context context) {
        this.results = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);

        float elevation = view.getResources().getDimension(R.dimen.cardElevation);
        ViewCompat.setElevation(view, elevation);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final SearchQuery.Result result = results.get(position);
        View.OnClickListener listener = view -> {
            Intent intent = new Intent(context, DisplayActivity.class);
            intent.putExtra(DisplayActivity.EXTRA_ID, result.id());
            intent.putExtra(DisplayActivity.EXTRA_TERM, result.term());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        };


        viewHolder.wordInfoView.setTerm(result.term());
        viewHolder.wordInfoView.setPos(result.pos());
        viewHolder.wordInfoView.setDefinitions(result.definitions());
        viewHolder.button.setText(R.string.see_entry);
        viewHolder.button.setOnClickListener(listener);
        viewHolder.itemView.setOnClickListener(listener);

        Resources resources = context.getResources();
        if(position == 0) {
            Drawable drawable = resources.getDrawable(R.drawable.search_results_item_top);
            viewHolder.itemView.setBackground(drawable);
        } else if(position == getItemCount() - 1) {
            Drawable drawable = resources.getDrawable(R.drawable.search_results_item_bottom);
            viewHolder.itemView.setBackground(drawable);
        } else {
            Drawable drawable = resources.getDrawable(R.drawable.search_results_item_middle);
            viewHolder.itemView.setBackground(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    void addAll(List<SearchQuery.Result> results) {
        int insertIndex = this.results.size() - 1;
        this.results.addAll(results);
        notifyItemRangeChanged(insertIndex ,results.size());
    }

    public abstract void loadMore();

    private class ViewHolder extends RecyclerView.ViewHolder {
        WordInfoView wordInfoView;
        Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            wordInfoView = itemView.findViewById(R.id.item_search_result_word_info);
            button = itemView.findViewById(R.id.item_search_result_button);
        }
    }

}
