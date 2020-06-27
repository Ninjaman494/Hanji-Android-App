package com.a494studios.koreanconjugator;

import android.content.Intent;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;

public class LaunchActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int size = (int)dpToPixels(200, this);
        int marginVertical = (int)dpToPixels(72, this);
        int marginHorizontal = (int)dpToPixels(16, this);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Hanji 2.0", "Welcome to Hanji 2.0! This new update adds a ton of new features and takes Hanji to the next level.", R.drawable.ic_icon);
        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard1.setTitleColor(R.color.black);
        ahoyOnboarderCard1.setDescriptionColor(R.color.black);
        ahoyOnboarderCard1.setTitleTextSize(32);
        ahoyOnboarderCard1.setDescriptionTextSize(16);
        ahoyOnboarderCard1.setIconLayoutParams(size, size, marginVertical, marginHorizontal, marginHorizontal, 0);

        ArrayList<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard1);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.colorPrimary);
        colors.add(R.color.colorAccent);
        colors.add(R.color.orangeYellow);

        setColorBackground(colors);
        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
