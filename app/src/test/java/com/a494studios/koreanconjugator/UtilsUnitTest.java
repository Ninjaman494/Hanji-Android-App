package com.a494studios.koreanconjugator;


import android.content.Context;

import com.a494studios.koreanconjugator.parsing.Favorite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class UtilsUnitTest {
    private Context context;

    @Before
    public void setup() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
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
        assertThat(favorites, samePropertyValuesAs(Utils.getFavorites(context)));
        assertEquals(favorites.size(),Utils.getFavCount(context));
    }

    @Test
    public void test_FavoritesEmpty() {
        ArrayList<Favorite> favorites = Utils.getFavorites(context);
        assertTrue(favorites.isEmpty());
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
