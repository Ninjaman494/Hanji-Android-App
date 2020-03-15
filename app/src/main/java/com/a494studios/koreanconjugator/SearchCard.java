package com.a494studios.koreanconjugator;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;

import static android.content.Context.SEARCH_SERVICE;

public class SearchCard implements DisplayCardBody {
    private View view;
    private Activity activity;
    private SearchView searchView;

    public SearchCard(Activity activity){
        this.activity = activity;
    }
    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_search,parentView);
        }
        searchView = view.findViewById(R.id.searchCard_search);
        SearchManager searchManager = (SearchManager) context.getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        return view;
    }

    public SearchView getSearchView(){
        return searchView;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
    }

    @Override
    public boolean shouldHideButton() {
        return true;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getButtonText() {
        return "Button";
    }

    @Override
    public String getHeading() {
        return null;
    }
}
