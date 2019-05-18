package com.a494studios.koreanconjugator;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.a494studios.koreanconjugator.parsing.Favorite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UtilsUnitTest {
    private Context context;

    @Before
    public void setup() throws Exception {
        // Context of the app under test.
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void test_FirstBoot() {
        Utils.setFirstBoot(context,true);
        assertTrue(Utils.isFirstBoot(context));
    }

    @Test
    public void test_Favorites() {
        Favorite fav1 = new Favorite("Fav1","conj1",true);
        Favorite fav2 = new Favorite("Fav2","conj2",false);
        Favorite fav3 = new Favorite("Fav3","conj3",true);
        ArrayList<Favorite> favorites = new ArrayList<>();
        favorites.add(fav1);
        favorites.add(fav2);
        favorites.add(fav3);

        Utils.setFavorites(favorites,context);
        assertEquals(favorites.size(),Utils.getFavCount(context));

        ArrayList<Favorite> returnedFavs = Utils.getFavorites(context);
        for(int i = 0;i<favorites.size();i++) {
            Favorite actualFav = favorites.get(i);
            Favorite returnedFav = returnedFavs.get(i);
            assertEquals(actualFav.getName(),returnedFav.getName());
            assertEquals(actualFav.getConjugationName(),returnedFav.getConjugationName());
            assertEquals(actualFav.isHonorific(),returnedFav.isHonorific());
        }
    }

    @Test
    public void test_TitleCase() {
        String case1 = "hello world";
        String case2 = "helloworld";
        String case3 = "HELLO WORLD";

        assertEquals("Hello World",Utils.toTitleCase(case1));
        assertEquals("Helloworld",Utils.toTitleCase(case2));
        assertEquals("Hello World",Utils.toTitleCase(case3));
    }
}
