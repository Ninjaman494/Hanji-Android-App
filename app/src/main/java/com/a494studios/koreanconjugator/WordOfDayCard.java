package com.a494studios.koreanconjugator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.a494studios.koreanconjugator.display.DisplayActivity;
import com.a494studios.koreanconjugator.display.DisplayCardView;
import com.a494studios.koreanconjugator.display.cards.DisplayCardBody;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.utils.Utils;
import com.apollographql.apollo.api.Response;

import io.reactivex.observers.DisposableObserver;

public class WordOfDayCard implements DisplayCardBody {
    private View view;
    private String id;


    @SuppressLint("CheckResult")
    @Override
    public View addBodyView(Context context, ViewGroup parentView, DisplayCardView cardView) {
        if(view == null) {
            view = View.inflate(context, R.layout.dcard_wod,parentView);
        }

        cardView.setButtonText(context.getString(R.string.see_entry));
        cardView.disableButton(true);

        Server.doWODQuery((CustomApplication)context.getApplicationContext())
                .subscribeWith(new DisposableObserver<Response<WordOfTheDayQuery.Data>>() {
                    @Override
                    public void onNext(Response<WordOfTheDayQuery.Data> dataResponse) {
                        TextView textView = view.findViewById(R.id.wod_text);
                        textView.setText(dataResponse.getData().wordOfTheDay.term);

                        id = dataResponse.getData().wordOfTheDay.id;
                        cardView.disableButton(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Utils.handleError(e, (AppCompatActivity) context, 8);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return view;
    }

    @Override
    public void onButtonClick() {
        Intent intent = new Intent(view.getContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.EXTRA_ID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        view.getContext().startActivity(intent);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String getHeading() {
        return "Word of the Day";
    }
}
