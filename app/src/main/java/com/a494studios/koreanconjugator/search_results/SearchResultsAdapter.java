package com.a494studios.koreanconjugator.search_results;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.utils.WordInfoView;

import java.util.ArrayList;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SearchQuery.Result> results;
    private String cursor;
    private Context context;

    public SearchResultsAdapter(SearchQuery.Search searchResult, Context context) {
        this.results = new ArrayList<>(searchResult.results());
        this.cursor = searchResult.cursor();
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final SearchQuery.Result result = results.get(position);

        viewHolder.wordInfoView.setTerm(result.term());
        viewHolder.wordInfoView.setPos(result.pos());
        viewHolder.wordInfoView.setDefinitions(result.definitions());
        viewHolder.button.setText(R.string.see_entry);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DisplayActivity.class);
                intent.putExtra(DisplayActivity.EXTRA_ID, result.id());
                intent.putExtra(DisplayActivity.EXTRA_TERM, result.term());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

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
