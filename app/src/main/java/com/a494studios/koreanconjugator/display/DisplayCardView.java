package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;

import java.util.Objects;

public class DisplayCardView extends RelativeLayout {
    TextView headingView;
    Button button;
    DisplayCardBody cardBody;
    Context context;
    LinearLayout linearLayout;
    TextView honorificChip;

    public DisplayCardView(Context context) {
        super(context);
        init(context);
    }

    public DisplayCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DisplayCardView(Context context, DisplayCardBody cardBody){
        super(context);
        this.cardBody = Objects.requireNonNull(cardBody);
        init(context);
    }

    private void init(Context context){
        this.context = context;

        View rootView = inflate(context, R.layout.view_display_card,this);
        headingView = rootView.findViewById(R.id.displayCard_heading);
        button = rootView.findViewById(R.id.displayCard_button);
        linearLayout = rootView.findViewById(R.id.displayCard_body);
        honorificChip = rootView.findViewById(R.id.displayCard_honorific);
        // Add body view
        if(cardBody != null) {
            cardBodyUpdates();
        }
    }

    // pre: cardBody is not null
    private void cardBodyUpdates() {
        cardBody.addBodyView(context,linearLayout); // Add body view to linear layout
        hideButton(cardBody.shouldHideButton());
        button.setText(cardBody.getButtonText());
        String heading = cardBody.getHeading();
        if(heading != null) {
            headingView.setVisibility(VISIBLE);
            headingView.setText(heading);
        } else {
            headingView.setVisibility(GONE);
        }
        setButtonListener();
    }

    private void setButtonListener() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cardBody.onButtonClick();
                cardBodyUpdates();
            }
        });
    }

    public void setCardBody(DisplayCardBody cardBody) {
        this.cardBody = Objects.requireNonNull(cardBody);
        cardBodyUpdates();
    }

    public void showHonorificChip(boolean shouldShow) {
        if(shouldShow) {
            honorificChip.setVisibility(VISIBLE);
        } else {
            honorificChip.setVisibility(GONE);
        }
    }

    private void hideButton(boolean shouldHide){
        if(shouldHide){
            button.setVisibility(GONE);
            linearLayout.setPadding(0,0,0,32);
        } else {
            button.setVisibility(VISIBLE);
            linearLayout.setPadding(0,0,0,0);
        }
    }
}
