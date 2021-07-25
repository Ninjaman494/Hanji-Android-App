package com.a494studios.koreanconjugator.display.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.EntryQuery;
import com.a494studios.koreanconjugator.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class ExampleAdapterUnitTest {
    private List<EntryQuery.Example> examples;
    private ExampleAdapter adapter;

    @Before
    public void init() {
        EntryQuery.Example e1 = new EntryQuery.Example("type","sen1","tran1");
        EntryQuery.Example e2 = new EntryQuery.Example("type","sen2","tran2");
        EntryQuery.Example e3 = new EntryQuery.Example("type","sen3","tran3");
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
    public void test_nullExample() {
        ArrayList<EntryQuery.Example> examples = new ArrayList<>();
        examples.add(null);

        // Shouldn't cause an exception
        ExampleAdapter adapter = new ExampleAdapter(examples);
        ViewGroup group = new LinearLayout(ApplicationProvider.getApplicationContext());

        View view = adapter.getView(0, null, group);
        assertNull(view);
    }

    @Test
    public void test_getView() {
        ViewGroup group = new LinearLayout(ApplicationProvider.getApplicationContext());
        for(int i = 0;i<examples.size();i++) {
            View view = adapter.getView(i,null,group);
            TextView senView = view.findViewById(R.id.item_example_sentence);
            TextView transView = view.findViewById(R.id.item_example_translation);

            assertEquals(examples.get(i).sentence(), senView.getText().toString());
            assertEquals(examples.get(i).translation(), transView.getText().toString());
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
