package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.os.Bundle;

import com.a494studios.koreanconjugator.parsing.Favorite;
import com.a494studios.koreanconjugator.utils.Utils;
import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;

public class LaunchActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Utils.isFirstBoot(this) && !Utils.isFirstTwo(this)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Make default favorites, or clear old favorites from 1.0
        ArrayList<Favorite> favs = new ArrayList<>();
        favs.add(new Favorite("Past","declarative past informal high",false));
        favs.add(new Favorite("Present","declarative present informal high",false));
        favs.add(new Favorite("Future","declarative future informal high",false));
        Utils.setFavorites(favs,this);
        Utils.setFirstBoot(this,false);
        Utils.setFirstTwo(this, false);


        // First card
        String title = getString(R.string.onboarding1_title);
        String desc = getString(R.string.onboarding1_desc);
        int size = (int)dpToPixels(200, this);
        int marginVertical = (int)dpToPixels(72, this);
        int marginHorizontal = (int)dpToPixels(16, this);

        AhoyOnboarderCard card1 = new AhoyOnboarderCard(title, desc, R.drawable.ic_icon);
        card1.setBackgroundColor(R.color.white);
        card1.setTitleColor(R.color.black);
        card1.setDescriptionColor(R.color.black);
        card1.setTitleTextSize(32);
        card1.setDescriptionTextSize(16);
        card1.setIconLayoutParams(size, size, marginVertical, marginHorizontal, marginHorizontal, 0);

        // Second card
        title = getString(R.string.onboarding2_title);
        desc = getString(R.string.onboarding2_desc);
        size = (int)dpToPixels(350, this);
        marginVertical = (int)dpToPixels(24, this);

        AhoyOnboarderCard card2 = new AhoyOnboarderCard(title, desc, R.drawable.ic_word_cloud);
        card2.setBackgroundColor(R.color.white);
        card2.setTitleColor(R.color.black);
        card2.setDescriptionColor(R.color.black);
        card2.setTitleTextSize(32);
        card2.setDescriptionTextSize(16);
        card2.setIconLayoutParams(size, size, marginVertical, marginHorizontal, marginHorizontal, 0);

        // Third card
        title = getString(R.string.onboarding3_title);
        desc = getString(R.string.onboarding3_desc);
        int width = (int)dpToPixels(200, this);
        int height = (int)dpToPixels(232, this);
        marginVertical = (int)dpToPixels(72, this);

        AhoyOnboarderCard card3 = new AhoyOnboarderCard(title, desc, R.drawable.ic_form);
        card3.setBackgroundColor(R.color.white);
        card3.setTitleColor(R.color.black);
        card3.setDescriptionColor(R.color.black);
        card3.setTitleTextSize(32);
        card3.setDescriptionTextSize(16);
        card3.setIconLayoutParams(width, height, marginVertical, marginHorizontal, marginHorizontal, 0);

        ArrayList<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(card1);
        pages.add(card2);
        pages.add(card3);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.colorPrimary);
        colors.add(R.color.colorAccent);
        colors.add(R.color.orangeYellow);

        setColorBackground(colors);
        setOnboardPages(pages);
        setFinishButtonTitle(R.string.finish_btn_text);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
