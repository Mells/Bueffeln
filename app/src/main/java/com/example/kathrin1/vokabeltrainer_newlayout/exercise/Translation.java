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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils.stringOfTagsToMap;

/**
 * Created by kathrin1 on 20.12.16.
 */

public class Translation extends AppCompatActivity {

    private DatabaseManager dbManager = null;
    private VocObject voc;
    private List<VocObject> allVocabulary;
    private TextView txt_voc;
    private TextView txt_bsp;
    private SlidingLayer mSlidingLayer;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation);

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
                Log.d("TAB", String.valueOf(tab.getText()));
                ExerciseUtils.updateBook(Translation.this, tab);
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

        dbManager = DatabaseManager.build(Translation.this);

        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button btn_solution = (Button) findViewById(R.id.btn_solution);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);

        txt_voc = (TextView) findViewById(R.id.txt_voc);
        txt_bsp = (TextView) findViewById(R.id.txt_bsp);

        final EditText edit_solution = (EditText) findViewById(R.id.edit_solution);
        final Switch sw_language = (Switch) findViewById(R.id.sw_language);
        final TextView txt_feedback = (TextView) findViewById(R.id.txt_feedback);

        // on inatiating the activity
        setBookValues();

        allVocabulary = dbManager.getWordsByBookChapterLevel(book, chapter, unit, level);

        if(!sw_language.isChecked()){
            getVocabularyEnglish();
        }
        else {
            getVocabularyGerman();
        }

        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() { }
            @Override
            public void onShowPreview() { }

            @Override
            public void onClose() {
                Toast.makeText(getApplicationContext(), "on fragment detached (close)", Toast.LENGTH_LONG).show();
                // todo keep allVocabulary as is if nothing has changed
                setBookValues();

                allVocabulary = dbManager.getWordsByBookChapterLevel(book, chapter, unit, level);

                if(!sw_language.isChecked()){
                    getVocabularyEnglish();
                }
                else {
                    getVocabularyGerman();
                }
            }

            @Override
            public void onOpened() { }

            @Override
            public void onPreviewShowed() { }

            @Override
            public void onClosed() {
                Toast.makeText(getApplicationContext(), "on fragment detached (closed)", Toast.LENGTH_LONG).show();

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txt_feedback.setText("");
                if (allVocabulary != null){
                    if (allVocabulary.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                                "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                    } else {
                        if(!sw_language.isChecked()){
                            getVocabularyEnglish();
                        }
                        else {
                            getVocabularyGerman();
                        }
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
                if (!sw_language.isChecked()) {
                    txt_feedback.setText(fromHtml("Die korrekte Übersetzung für <b><i>" + voc.getVoc() + "</i></b> ist <b><i>" + voc.getTranslation() + "</i></b>"));
                }
                else {
                    txt_feedback.setText(fromHtml("Die korrekte Übersetzung für <b><i>" + voc.getTranslation() + "</i></b> ist <b><i>" + voc.getVoc() + "</i></b>"));

                }
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
                        if (!sw_language.isChecked()) {
                            if (edit_solution.getText().toString().equals(voc.getTranslation())) {
                                txt_feedback.setText("Gratulation, das ist korrekt.");
                                allVocabulary.remove(voc);
                                // write to database
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }
                            } else {
                                // Todo - feedback
                                txt_feedback.setText(fromHtml("Es tut mir leid, aber <i><b>" +
                                        edit_solution.getText().toString() + "</b></i> ist nicht korrekt."));
                            }
                        } else {
                            if (edit_solution.getText().toString().equals(voc.getVoc())) {
                                txt_feedback.setText("Gratulation, das ist korrekt.");
                                allVocabulary.remove(voc);
                                // write to database
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }

                            } else {
                                // Todo - feedback
                                txt_feedback.setText(fromHtml("Es tut mir leid, aber <i><b>" +
                                        edit_solution.getText().toString() + "</b></i> ist nicht korrekt."));
                            }
                        }
                    }
                    handled = true;
                }
                edit_solution.getText().clear();
                return handled;
            }
        });

        btn_auswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingLayer.openLayer(true);
            }

        });

        sw_language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txt_feedback.setText("");
                if(!sw_language.isChecked()){
                    getVocabularyEnglish();
                }
                else {
                    getVocabularyGerman();
                }
            }
        });

    }

    private void setBookValues() {
        book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book", "I");

        chapter = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("chapter", "Welcome");

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

        Log.d("BOOKVALUE ", book + " " + chapter + " " + unit + " " + level);
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

    private void getVocabularyGerman() {
        Log.d("-----------", "-------------------");
        if (allVocabulary.size() == 0) {
            Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                    "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
        } else {
            Log.d("DE-VocabularySize", Integer.toString(allVocabulary.size()));
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(allVocabulary.size());
            voc = allVocabulary.get(index);
            Log.d("DE-Vokabel", voc.getVoc());
            Log.d("DE-Übersetzung", voc.getTranslation());
            txt_voc.setText(voc.getTranslation());


            // TODO:  CHANGE SENTENCE TYPE HERE
            SentObject sentence = dbManager.getExampleGDEXSentence(voc);

            // TODO - delete the word from bsp
//            Map<String, List<String>> smap = DBUtils.stringOfTagsToMap(sentence.getMapped());
//
//            List<String> lemmaVocList = new ArrayList<String>(Arrays.asList(voc.getLemma().substring(1, voc.getLemma().length() - 1).split(", ")));
//            String sent = sentence.getSentence();
//
//            Log.d("smap", smap.toString());
//            Log.d("lemmaList", lemmaVocList.toString());
//
//            for (String l : lemmaVocList){
//                Log.d("l", l.replaceAll("'",""));
//                String lemma = l.replaceAll("['\\[\\]]", "");
//                if (smap.containsKey(lemma)){
//                    List<String> bla = smap.get(lemma);
//                    Log.d("bla", bla.toString());
//                    for (String s : bla){
//                        sent = sent.replaceAll(s, "___");
//                    }
//                }
//            }
            txt_bsp.setText(ExerciseUtils.deleteWordFromSentence(sentence, voc));
            //txt_bsp.setText(fromHtml(sent));
            //txt_bsp.setText(sentence.getSentence());
        }
    }

    private void getVocabularyEnglish() {

        if (allVocabulary.size() == 0) {
            Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                    "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
        } else {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(allVocabulary.size());
            Log.d("EN-VocabularySize:", Integer.toString(allVocabulary.size()));
            voc = allVocabulary.get(index);
            txt_voc.setText(voc.getVoc());

            // TODO:  CHANGE SENTENCE TYPE HERE
            SentObject sentence = dbManager.getExampleGDEXSentence(voc);

            // TODO - highlight the word in bsp
            /*Log.d("Voc", voc.getVoc());
            Log.d("errorSent", sentence.getSentence());

            Map<String, List<String>> smap = stringOfTagsToMap(sentence.getMapped());

            List<String> lemmaVocList = new ArrayList<String>(Arrays.asList(voc.getLemma().substring(1, voc.getLemma().length() - 1).split(", ")));
            String sent = sentence.getSentence();

            Log.d("smap", smap.toString());
            Log.d("lemmaList", lemmaVocList.toString());

            for (String l : lemmaVocList){
                Log.d("l", l.replaceAll("'",""));
                if (smap.containsKey(l.replaceAll("'","").replaceAll("\\[","").replaceAll("\\]",""))){
                    List<String> bla = smap.get(l.replaceAll("'","").replaceAll("\\[","").replaceAll("\\]",""));
                    Log.d("bla", bla.toString());
                    for (String s : bla){
                        sent = sent.replaceAll(s, "<big><b>"+s+"</b></big>");
                    }
                }
            }*/
            txt_bsp.setText(ExerciseUtils.fromHtml(
                    ExerciseUtils.replaceWordInSentence(sentence, voc, "<b><big>%s</big></b>")));
            //txt_bsp.setText(fromHtml(sent));
            //txt_bsp.setText(sentence.getSentence());
        }
    }
    public void fragmentDetached(){

    }

    public SlidingLayer getSlidingLayer(){
        return mSlidingLayer;
    }



    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
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
                Intent intent_help = new Intent(Translation.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Translation.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
