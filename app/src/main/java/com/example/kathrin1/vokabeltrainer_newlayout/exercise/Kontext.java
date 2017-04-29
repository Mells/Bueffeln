package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kathrin1 on 21.12.16.
 */

public class Kontext extends AppCompatActivity {

    private DatabaseManager dbManager = null;

    private List<VocObject> allVocabulary;
    private TextView txt_sent01;
    private TextView txt_sent02;
    private TextView txt_sent03;
    private VocObject voc;
    private SlidingLayer mSlidingLayer;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontext);

        // ----------------TABS---------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Book 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(setCurrentBook());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ExerciseUtils.updateBook(Kontext.this, tab);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // --------------------------------------------

        // ----------------SLIDER---------------------

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        // --------------------------------------------

        dbManager = DatabaseManager.build(Kontext.this);

        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button btn_solution = (Button) findViewById(R.id.btn_solution);
        Button btn_hint = (Button) findViewById(R.id.btn_hint);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);

        txt_sent01 = (TextView) findViewById(R.id.txt_sentence01);
        txt_sent02 = (TextView) findViewById(R.id.txt_sentence02);
        txt_sent03 = (TextView) findViewById(R.id.txt_sentence03);

        final EditText edit_solution = (EditText) findViewById(R.id.edit_solution);
        final TextView txt_feedback = (TextView) findViewById(R.id.txt_feedback);

        // on inatiating the activity
        setBookValues();

        txt_feedback.setText("");
        allVocabulary = dbManager.getWordsByBookChapterLevel(book, chapter, unit, level);

        getVocabularyEnglishGerman();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - next word
                txt_feedback.setText("");
                if (allVocabulary != null){
                    if (allVocabulary.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                                "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                    } else {
                        getVocabularyEnglishGerman();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                            "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - next word
                if (allVocabulary != null){
                    if (allVocabulary.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                                "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                    } else {
                        txt_feedback.setText(fromHtml("Das deutsche Word lautet: <i><b>"+voc.getTranslation()+"</b></i>."));
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                            "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_feedback.setText(fromHtml("Das gesuchte Wort ist <b><i>" + voc.getVoc() + "</i></b> und Übersetzt <b><i>" + voc.getTranslation() + "</i></b>"));
            }
        });

        btn_auswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingLayer.openLayer(true);

            }

        });

        edit_solution.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("TextEdit", "in TextEdit");
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("TextEdit", "in if");
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    if (edit_solution.getText().toString().length() > 0) {
                            if (edit_solution.getText().toString().equals(voc.getTranslation())) {
                                txt_feedback.setText("Gratulation, das ist korrekt.");
                                allVocabulary.remove(voc);
                                // todo - schreib zu datenbank
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }
                            } else {
                                // Todo - feedback
                                txt_feedback.setText(fromHtml("Es tut mir leid, aber <i><b>" +
                                        edit_solution.getText().toString() + "</b></i> ist nicht korrekt."));
                            }
                    }
                    handled = true;
                }
                edit_solution.getText().clear();
                return handled;
            }
        });
    }

    private void setBookValues() {
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book", "0");

        if (!pref_book.equals("0")){
            book = pref_book;
        }
        else{
            book = "I";
        }

        String pref_chapter = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("chapter", "Welcome");


        chapter = pref_chapter;

        Boolean pref_unit_A = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("unit_A", true);
        Boolean pref_unit_B = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("unit_A", false);
        Boolean pref_unit_C = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("unit_A", false);

        if (pref_unit_A){
            if (pref_unit_B){
                if (pref_unit_C){
                    unit = "A B C";
                }
                else{
                    unit = "A B";
                }
            }
            else {
                if (pref_unit_C){
                    unit = "A C";
                }
                else{
                    unit = "A";
                }
            }
        }
        else{
            if (pref_unit_B){
                if (pref_unit_C){
                    unit = "B C";
                }
                else{
                    unit = "B";
                }
            }
            else {
                if (pref_unit_C){
                    unit = "C";
                }
                else{
                    unit = "A";
                }
            }
        }

        int pref_level = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("level", 0);
        level = pref_level;
    }

    private int setCurrentBook() {
        int tab = 0;
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book", "I");
        switch (pref_book){
            case "I": tab = 0;
                break;
            case "II": tab = 1;
                break;
            case "III": tab = 2;
                break;
        }
        return tab;
    }

    private void getVocabularyEnglishGerman() {

        if (allVocabulary.size() == 0) {
            Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                    "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
        } else {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(allVocabulary.size());
            Log.d("TRANSLATION:", Integer.toString(allVocabulary.size()));
            voc = allVocabulary.get(index);

            // TODO:  CHANGE SENTENCE TYPE HERE
            List<String> sentenceList = DBUtils.splitListString(voc.getGDEXSentences());
            
            Log.d("TRANSLATION", Integer.toString(sentenceList.size()));
            if (sentenceList.size() >= 3) {

                // MOVED HANDLING OF PARSING INTEGERS FROM STRINGS TO DatabaseManager.getSentence(String)
                txt_sent01.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(0)), voc, "___")));
                txt_sent02.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(1)), voc, "___")));
                txt_sent03.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(2)), voc, "___")));
            }
            else{
                // todo supplement sentences
                if (sentenceList.size() == 1){
                    txt_sent01.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(0)), voc, "___")));
                    txt_sent02.setText("");
                    txt_sent03.setText("");
                }
                else if (sentenceList.size() == 2){
                    txt_sent01.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(0)), voc, "___")));
                    txt_sent02.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(dbManager.getSentence(sentenceList.get(1)), voc, "___")));
                    txt_sent03.setText("");
                }
            }
        }
    }

    public SlidingLayer getSlidingLayer(){
        return mSlidingLayer;
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_help:
                Intent intent_help = new Intent(Kontext.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Kontext.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
