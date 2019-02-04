package com.a494studios.koreanconjugator.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

import java.util.List;

public class WordInfoView extends RelativeLayout {
    private TextView termView;
    private TextView posView;
    private LinearLayout defsView;

    public WordInfoView(Context context) {
        super(context);
        init(context);
    }

    public WordInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WordInfoView(Context context, String term, String pos, List<String> definitions){
        super(context);
        init(context);

        this.setTerm(term);
        this.setPos(pos);
        this.setDefinitions(definitions);
    }

    private void init(Context context){
        View rootView = inflate(context, R.layout.view_word_info, this);
        termView = rootView.findViewById(R.id.item_search_result_term);
        posView = rootView.findViewById(R.id.item_search_result_pos);
        defsView = rootView.findViewById(R.id.item_search_result_recycler);
    }

    public void setTerm(String term) {
        termView.setText(term);
    }

    public void setPos(String pos) {
        posView.setText(pos);
    }

    public void setDefinitions(List<String> definitions) {
        for(String def : definitions) {
            View vi = inflate(getContext(), R.layout.item_word_info,null);
            ((TextView)vi.findViewById(R.id.content)).setText(def);
            defsView.addView(vi);
        }
    }
}
