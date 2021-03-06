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
    private boolean showMore = true;
    private boolean showAll = false;
    private List<String> definitions;

    public WordInfoView(Context context) {
        super(context);
        init(context);
    }

    public WordInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WordInfoView(Context context, String term, String pos, List<String> definitions, boolean showMore){
        super(context);
        init(context);
        this.showMore = showMore;
        this.definitions = definitions;

        this.setTerm(term);
        this.setPos(pos);
        this.setDefinitions(definitions);
    }

    private void init(Context context){
        View rootView = inflate(context, R.layout.view_word_info, this);
        termView = rootView.findViewById(R.id.word_info_term);
        posView = rootView.findViewById(R.id.word_info_pos);
        defsView = rootView.findViewById(R.id.word_info_recycler);
    }

    public void setTerm(String term) {
        termView.setText(term);
    }

    public void setPos(String pos) {
        posView.setText(pos);
    }

    public void clickShowAll(boolean showAll) {
        this.showAll = showAll;
        if(showAll) {
            // Remove show more text is it's showing (which should be never)
            if(showMore) {
                defsView.removeViewAt(2);
            }

            // Add views starting from index 3, or 2 if show more was removed
            int startingIndex = showMore ? 2 : 3;
            for(int i = startingIndex;i<definitions.size();i++) {
                View vi = inflate(getContext(), R.layout.item_word_info,null);
                ((TextView)vi.findViewById(R.id.content)).setText(definitions.get(i));
                defsView.addView(vi, i);
            }
        } else {
            // Remove views from index 3 till end
            for(int i = definitions.size()-1;i>2;i--) {
                defsView.removeViewAt(i);
            }
        }
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
        defsView.removeAllViews();

        for(int i = 0;i<definitions.size() && i<2;i++) {
            View vi = inflate(getContext(), R.layout.item_word_info,null);
            ((TextView)vi.findViewById(R.id.content)).setText(definitions.get(i));
            defsView.addView(vi);
        }

        if(definitions.size() > 2) {
            String thirdText = definitions.get(2);
            if (showMore) {
                thirdText = "+" + (definitions.size() - 2) + " More";
            }
            View vi = inflate(getContext(), R.layout.item_word_info, null);
            ((TextView) vi.findViewById(R.id.content)).setText(thirdText);
            defsView.addView(vi);
        }
    }

    public boolean getShowAll() {
        return showAll;
    }
}
