package com.a494studios.koreanconjugator.display.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.ExamplesQuery;
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
public class ExampleAdapterUnitTest {
    private List<ExamplesQuery.Example> examples;
    private ExampleAdapter adapter;

    @Before
    public void init() {
        ExamplesQuery.Example e1 = new ExamplesQuery.Example("type","sen1","tran1");
        ExamplesQuery.Example e2 = new ExamplesQuery.Example("type","sen2","tran2");
        ExamplesQuery.Example e3 = new ExamplesQuery.Example("type","sen3","tran3");
        examples = new ArrayList<>();
        examples.add(e1);
        examples.add(e2);
        examples.add(e3);
        adapter = new ExampleAdapter(examples);
    }

    @Test(expected = NullPointerException.class)
    public void test_nullExamples() {
        new ExampleAdapter(null);
    }

    @Test
    public void test_getView() {
        ViewGroup group = new LinearLayout(RuntimeEnvironment.application.getApplicationContext());
        for(int i = 0;i<examples.size();i++) {
            View view = adapter.getView(i,null,group);
            TextView senView = view.findViewById(R.id.item_example_sentence);
            TextView transView = view.findViewById(R.id.item_example_translation);

            assertEquals(examples.get(i).sentence(),senView.getText());
            assertEquals(examples.get(i).translation(),transView.getText());
        }
    }

    @Test
    public void test_getCount() {
        assertEquals(examples.size(),adapter.getCount());
    }

    @Test
    public void test_getItem() {
        for(int i = 0;i<examples.size();i++) {
            assertEquals(examples.get(i),adapter.getItem(i));
        }
    }

    @Test
    public void test_getItemId() {
        for(int i = 0;i<examples.size();i++) {
            assertEquals(i,adapter.getItemId(i));
        }
    }
}
