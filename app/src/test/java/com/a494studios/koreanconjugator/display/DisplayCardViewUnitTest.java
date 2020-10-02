package com.a494studios.koreanconjugator.display;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.a494studios.koreanconjugator.R;
import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DisplayCardViewUnitTest {

    private final int BUTTON_ID = R.id.displayCard_button;
    private final int HEADING_ID = R.id.displayCard_heading;
    private Context context;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void test_buttonVisibility() {
        DisplayCardBody cardBody = mock(DisplayCardBody.class);
        when(cardBody.shouldHideButton()).thenReturn(true);
        DisplayCardView view1 = new DisplayCardView(context,cardBody);
        assertEquals(View.GONE,view1.findViewById(BUTTON_ID).getVisibility());

        when(cardBody.shouldHideButton()).thenReturn(false);
        DisplayCardView view2 = new DisplayCardView(context,cardBody);
        assertEquals(view2.findViewById(BUTTON_ID).getVisibility(),View.VISIBLE);
    }

    @Test
    public void test_buttonText() {
        String text = "button text";
        DisplayCardBody cardBody = mock(DisplayCardBody.class);
        when(cardBody.getButtonText()).thenReturn(text);
        DisplayCardView view = new DisplayCardView(context,cardBody);
        Button btn = view.findViewById(BUTTON_ID);
        assertEquals(text,btn.getText());
    }

    @Test
    public void test_heading() {
        String heading = "heading";
        DisplayCardBody cardBody = mock(DisplayCardBody.class);
        when(cardBody.getHeading()).thenReturn(heading);
        DisplayCardView view1 = new DisplayCardView(context,cardBody);
        TextView headingView = view1.findViewById(HEADING_ID);
        assertEquals(heading,headingView.getText());

        when(cardBody.getHeading()).thenReturn(null);
        DisplayCardView view2 = new DisplayCardView(context,cardBody);
        assertEquals(View.GONE,view2.findViewById(HEADING_ID).getVisibility());
    }

    @Test
    public void test_buttonUpdates() {
        String btnText = "button text";
        String heading = "hi";

        DisplayCardBody cardBody = mock(DisplayCardBody.class);
        DisplayCardView view = new DisplayCardView(context,cardBody);
        Button btn = view.findViewById(BUTTON_ID);
        TextView headingView = view.findViewById(HEADING_ID);

        // Simulate cardBody changing itself when clicked
        when(cardBody.getButtonText()).thenReturn(btnText);
        when(cardBody.getHeading()).thenReturn(heading);
        when(cardBody.shouldHideButton()).thenReturn(false);
        btn.callOnClick();

        assertEquals(btnText,btn.getText());
        assertEquals(View.VISIBLE,btn.getVisibility());
        assertEquals(heading,headingView.getText());
        assertEquals(View.VISIBLE,headingView.getVisibility());
    }

    @Test
    public void test_setCardBody() {
        String btnText = "button text";
        String heading = "hi";

        DisplayCardView view = new DisplayCardView(context);
        DisplayCardBody cardBody = mock(DisplayCardBody.class);
        Button btn = view.findViewById(BUTTON_ID);
        TextView headingView = view.findViewById(HEADING_ID);

        when(cardBody.getButtonText()).thenReturn(btnText);
        when(cardBody.getHeading()).thenReturn(heading);
        when(cardBody.shouldHideButton()).thenReturn(false);

        view.setCardBody(cardBody);
        assertEquals(btnText,btn.getText());
        assertEquals(View.VISIBLE,btn.getVisibility());
        assertEquals(heading,headingView.getText());
        assertEquals(View.VISIBLE,headingView.getVisibility());
    }
}
