package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

public class NoteCard implements DisplayCardBody {
    private View view;
    private String note;

    public NoteCard(String note) {
        this.note = note;
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView) {
        if(view == null){
            view = View.inflate(context, R.layout.dcard_simpletext,parentView);
        }

        TextView textView = view.findViewById(R.id.simpleCard_text);
        textView.setText(note);
        return view;
    }

    @Override
    public void onButtonClick() {
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
        return "Note";
    }
}
