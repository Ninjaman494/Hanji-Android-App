package com.a494studios.koreanconjugator.display.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.DisplayCardView;

import java.util.Objects;

public class NoteCard implements DisplayCardBody {
    private View view;
    private String note;

    public NoteCard(String note) {
        this.note = Objects.requireNonNull(note);
    }

    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null){
            view = View.inflate(context, R.layout.dcard_simpletext,parentView);
        }

        TextView textView = view.findViewById(R.id.simpleCard_text);
        textView.setText(note);

        cardView.hideButton(true);

        return view;
    }

    @Override
    public void onButtonClick() {
        // Empty on purpose
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getHeading() {
        return "Note";
    }
}
