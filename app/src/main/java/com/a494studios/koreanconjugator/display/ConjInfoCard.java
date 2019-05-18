package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.Utils;
import com.linearlistview.LinearListView;

import java.util.List;
import java.util.Objects;

public class ConjInfoCard implements DisplayCardBody {

    private View view;
    private String name;
    private String conjugated;
    private String pronunciation;
    private String romanization;
    private List<String> explanations;

    public ConjInfoCard(String name, String conjugated, String pronunciation, String romanization, List<String> explanations) {
        this.name = Utils.toTitleCase(Objects.requireNonNull(name));
        this.conjugated = Objects.requireNonNull(conjugated);
        this.pronunciation = Objects.requireNonNull(pronunciation);
        this.romanization = Objects.requireNonNull(romanization);
        this.explanations = Objects.requireNonNull(explanations);
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_conj_info,parentView);
        }

        ((TextView)view.findViewById(R.id.conjInfo_conjugated)).setText(conjugated);
        ((TextView)view.findViewById(R.id.conjInfo_hpronc)).setText(pronunciation);
        ((TextView)view.findViewById(R.id.conjInfo_roman)).setText(romanization);

        LinearListView listView = view.findViewById(R.id.conjInfo_explainList);
        listView.setAdapter(new ExplanationsAdapter(explanations));
        return view;
    }

    @Override
    public void onButtonClick(){
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
        return name;
    }

    private class ExplanationsAdapter extends BaseAdapter {
        private static final int RESOURCE_ID = R.layout.item_example;
        private List<String> explanations;

        public ExplanationsAdapter(List<String> explanations){
            this.explanations = explanations;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(RESOURCE_ID, viewGroup, false);
            }

            String explanation = explanations.get(i);
            int index = explanation.indexOf('(');
            String header = explanation.substring(0,index);
            String sub = explanation.substring(index);

            TextView sentenceView = view.findViewById(R.id.item_example_sentence);
            TextView transView = view.findViewById(R.id.item_example_translation);
            sentenceView.setText(header);
            transView.setText(sub);
            return view;
        }

        @Override
        public int getCount() {
            return explanations.size();
        }

        @Override
        public String getItem(int i) {
            return explanations.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

}
