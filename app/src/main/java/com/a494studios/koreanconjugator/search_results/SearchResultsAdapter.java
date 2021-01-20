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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.a494studios.koreanconjugator.CustomApplication;
import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.SearchQuery;
import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.utils.WordInfoView;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SearchQuery.Result> results;
    private Context context;
    private CustomApplication app;

    SearchResultsAdapter(Context context) {
        this.results = new ArrayList<>();
        this.context = context;
        this.app = (CustomApplication) context.getApplicationContext();
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
        int itemCount = getItemCount();

        // Set background based on position
        Resources resources = context.getResources();
        Resources.Theme theme = context.getTheme();
        if (position == 0) {
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.search_results_item_top, theme);
            viewHolder.itemView.setBackground(drawable);
        } else if (position == itemCount - 1) {
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.search_results_item_bottom, theme);
            viewHolder.itemView.setBackground(drawable);
        } else {
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.search_results_item_middle, theme);
            viewHolder.itemView.setBackground(drawable);
        }

        // Handle showing ad
        int offset = 0;
        if (!app.isAdFree()) {
            if (itemCount > 3) {
                // If this is the 3rd item in a list greater than 3, show ad here
                if (position == 2) {
                    viewHolder.adView.setVisibility(View.VISIBLE);
                    viewHolder.button.setVisibility(View.GONE);
                    viewHolder.wordInfoView.setVisibility(View.GONE);

                    app.handleAdCard(viewHolder.adView);
                    return;
                } else if (position > 2) {
                    // If we're past the 3rd item, use offset to get the correct item.
                    offset = -1;
                }
            } else if (position == itemCount - 1) {
                // If this is the last item in a list of 3 or less, show ad here
                viewHolder.adView.setVisibility(View.VISIBLE);
                viewHolder.button.setVisibility(View.GONE);
                viewHolder.wordInfoView.setVisibility(View.GONE);

                app.handleAdCard(viewHolder.adView);
                return;
            }

            // Not showing ad, prep for showing results
            viewHolder.adView.setVisibility(View.GONE);
            viewHolder.button.setVisibility(View.VISIBLE);
            viewHolder.wordInfoView.setVisibility(View.VISIBLE);
        }

        // Setup on click listener
        SearchQuery.Result result = results.get(position + offset);
        View.OnClickListener listener = view -> {
            Intent intent = new Intent(context, DisplayActivity.class);
            intent.putExtra(DisplayActivity.EXTRA_ID, result.id());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        };

        viewHolder.wordInfoView.setTerm(result.term());
        viewHolder.wordInfoView.setPos(result.pos());
        viewHolder.wordInfoView.setDefinitions(result.definitions());
        viewHolder.button.setText(R.string.see_entry);
        viewHolder.button.setOnClickListener(listener);
        viewHolder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        int size = results.size();
        if (size > 0) {
            return app.isAdFree() ? size : size + 1;
        } else {
            return size;
        }
    }

    void addAll(List<SearchQuery.Result> results) {
        int insertIndex = this.results.size() - 1;
        this.results.addAll(results);
        notifyItemRangeChanged(insertIndex, results.size());
    }

    public abstract void loadMore();

    @SuppressWarnings("InnerClassMayBeStatic")
    private class ViewHolder extends RecyclerView.ViewHolder {
        AdView adView;
        WordInfoView wordInfoView;
        Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            adView = itemView.findViewById(R.id.item_search_result_adView);
            wordInfoView = itemView.findViewById(R.id.item_search_result_word_info);
            button = itemView.findViewById(R.id.item_search_result_button);
        }
    }

}
