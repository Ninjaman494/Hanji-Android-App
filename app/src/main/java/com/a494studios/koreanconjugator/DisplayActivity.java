package com.a494studios.koreanconjugator;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.a494studios.koreanconjugator.parsing.Category;
import com.a494studios.koreanconjugator.parsing.Conjugation;
import com.a494studios.koreanconjugator.parsing.Form;
import com.a494studios.koreanconjugator.parsing.Formality;
import com.a494studios.koreanconjugator.parsing.Server;
import com.a494studios.koreanconjugator.parsing.Tense;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    public static final String EXTRA_CONJ = "conj";
    public static final String EXTRA_DEF = "definition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ArrayList<Conjugation> conjugations = (ArrayList<Conjugation>)getIntent().getSerializableExtra(EXTRA_CONJ);
        final TextView defView = findViewById(R.id.defCard_content);
        String definition = getIntent().getStringExtra(EXTRA_DEF);
        if(definition == null) {
            Server.requestKorDefinition(conjugations.get(0).getInfinitive(), this, new Server.DefinitionListener() {
                @Override
                public void onDefinitionReceived(String definition) {
                    defView.setText(definition);
                }

                @Override
                public void onErrorOccurred(String errorMsg) {
                    System.out.println(errorMsg);
                }
            });
        }else{
            defView.setText(definition);
        }

        // Declarative
        ArrayList<Conjugation> decPast = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.PAST);
        ArrayList<Conjugation> decPres = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.PRESENT);
        ArrayList<Conjugation> decFut = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.FUTURE);
        ArrayList<Conjugation> decFutC = Category.Categories.getSubSet(conjugations,null, Form.DECLARATIVE, Tense.FUT_COND);
        // Inquisitive
        ArrayList<Conjugation> inqPast = Category.Categories.getSubSet(conjugations, null, Form.INQUISITIVE, Tense.PAST);
        ArrayList<Conjugation> inqPres = Category.Categories.getSubSet(conjugations,null, Form.INQUISITIVE, Tense.PRESENT);
        // Imperative
        ArrayList<Conjugation> imPres = Category.Categories.getSubSet(conjugations,null, Form.IMPERATIVE, Tense.PRESENT);
        // Propositive
        ArrayList<Conjugation> propPres = Category.Categories.getSubSet(conjugations,null, Form.PROPOSITIVE, Tense.PRESENT);
        // Other
        ArrayList<Conjugation> other = Category.Categories.getSubSet(conjugations,Form.NOMINAL,Form.CON_AND,Form.CON_IF);

        // Favorites
        Conjugation past = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.PAST).get(0);
        Conjugation present = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.PRESENT).get(0);
        Conjugation future = Category.Categories.getSubSet(conjugations, Formality.INFORMAL_HIGH,Form.DECLARATIVE, Tense.FUTURE).get(0);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.disp_root,FavoritesFragment.newInstance(past,present,future));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Past", decPast));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Present", decPres));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Future", decFut));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Declarative Future Conditional", decFutC));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Inquisitive Past", inqPast));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Inquisitive Present", inqPres));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Imperative Present", imPres));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Propositive Present", propPres));
        transaction.add(R.id.disp_root,ConjugationCardFragment.newInstance("Other Forms", other));
        transaction.commit();
    }
}