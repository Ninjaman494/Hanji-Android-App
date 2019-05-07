package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a494studios.koreanconjugator.R;

public class DisplayCardView extends RelativeLayout {
    TextView headingView;
    Button button;
    DisplayCardBody cardBody;
    Context context;
    LinearLayout linearLayout;

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
        init(context);
        this.cardBody = cardBody;
    }

    private void init(Context context){
        this.context = context;

        View rootView = inflate(context, R.layout.view_display_card,this);
        headingView = rootView.findViewById(R.id.displayCard_heading);
        button = rootView.findViewById(R.id.displayCard_button);
        linearLayout = rootView.findViewById(R.id.displayCard_body);
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
            headingView.setText(heading);
        } else {
            headingView.setVisibility(GONE);
        }
    }

    public void setButtonListener(OnClickListener listener) {
        button.setOnClickListener(listener);
    }

    public void setCardBody(DisplayCardBody cardBody) {
        this.cardBody = cardBody;
        cardBodyUpdates();
    }

    private void hideButton(boolean shouldHide){
        if(shouldHide){
            button.setVisibility(GONE);
        } else {
            button.setVisibility(VISIBLE);
        }
    }
}
