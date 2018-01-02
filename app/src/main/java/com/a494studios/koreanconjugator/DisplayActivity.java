package com.a494studios.koreanconjugator;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Tense;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ArrayList<Conjugation> conjugations = (ArrayList<Conjugation>)getIntent().getSerializableExtra("conj");

        ArrayList<Conjugation> decPast = Category.Categories.getSubSet(conjugations, Form.DECLARATIVE, Tense.PAST);
        ArrayList<Conjugation> decPres = Category.Categories.getSubSet(conjugations,Form.DECLARATIVE, Tense.PRESENT);
        ArrayList<Conjugation> decFut = Category.Categories.getSubSet(conjugations,Form.DECLARATIVE, Tense.FUTURE);
        ArrayList<Conjugation> decFutC = Category.Categories.getSubSet(conjugations,Form.DECLARATIVE, Tense.FUT_COND);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Past", decPast));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Present", decPres));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Future", decFut));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Future Conditional", decFutC));
        transaction.commit();
    }
}