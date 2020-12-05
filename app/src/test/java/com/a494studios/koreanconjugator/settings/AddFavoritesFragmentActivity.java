package com.a494studios.koreanconjugator.settings;

import androidx.fragment.app.FragmentActivity;

import com.a494studios.koreanconjugator.parsing.Favorite;

class AddFavoritesFragmentActivity extends FragmentActivity implements AddFavoriteFragment.AddFavoriteFragmentListener {

    private Favorite favorite;

    public Favorite getFavorite() {
        return favorite;
    }

    @Override
    public void onFavoriteAdded(Favorite entry) {
       favorite = entry;
    }
}
