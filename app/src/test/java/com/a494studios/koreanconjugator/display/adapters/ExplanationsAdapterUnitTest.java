package com.a494studios.koreanconjugator.display.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ExplanationsAdapterUnitTest {
    private List<String> explanations;
    private ExplanationsAdapter adapter;

    @Before
    public void init() {
        explanations = new ArrayList<>();
        explanations.add("Reason 1");
        explanations.add("title (explain)");
        explanations.add("title (explain -> hi)");
        adapter = new ExplanationsAdapter(explanations);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullExplanation() {
        new ExplanationsAdapter(null);
    }

    @Test
    public void test_getView() {
        ViewGroup group = new LinearLayout(RuntimeEnvironment.application.getApplicationContext());
        for(int  i = 0;i<explanations.size();i++) {
            View view = adapter.getView(i,null,group);
            TextView senView = view.findViewById(R.id.item_example_sentence);
            TextView transView = view.findViewById(R.id.item_example_translation);

            if(i == 0) {
                assertEquals(explanations.get(i), senView.getText().toString());
                assertEquals(View.GONE,transView.getVisibility());
            } else {
                String sub = explanations.get(i).replace("title ","");
                assertEquals("title", senView.getText().toString());
                assertEquals(sub, transView.getText().toString());
            }

        }
    }


    @Test
    public void test_getCount() {
        assertEquals(explanations.size(),adapter.getCount());
    }

    @Test
    public void test_getItem() {
        for(int i = 0;i<explanations.size();i++) {
            assertEquals(explanations.get(i),adapter.getItem(i));
        }
    }

    @Test
    public void test_getItemId() {
        for(int i = 0;i<explanations.size();i++) {
            assertEquals(i,adapter.getItemId(i));
        }
    }
}
