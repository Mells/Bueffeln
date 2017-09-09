package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.List;
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

    private List<String> copyOfSentenceList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo change to exercise_kontext and look why its dying
        setContentView(R.layout.exercise_kontext);

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
        viewPager.setCurrentItem(ExerciseUtils.setCurrentBook(Kontext.this));
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

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);
        final Button btn_next = (Button) findViewById(R.id.btn_next);
        final Button btn_solution = (Button) findViewById(R.id.btn_solution);
        final Button btn_hint = (Button) findViewById(R.id.btn_hint);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);

        final LinearLayout lay_vocabel = (LinearLayout) findViewById(R.id.lay_vocabel);
        final LinearLayout lay_button_menu = (LinearLayout) findViewById(R.id.lay_button_menu);
        final RelativeLayout lay_trans_down = (RelativeLayout) findViewById(R.id.lay_trans_down);

        txt_sent01 = (TextView) findViewById(R.id.txt_sentence01);
        txt_sent02 = (TextView) findViewById(R.id.txt_sentence02);
        txt_sent03 = (TextView) findViewById(R.id.txt_sentence03);

        final EditText edit_solution = (EditText) findViewById(R.id.edit_solution);
        final TextView txt_feedback = (TextView) findViewById(R.id.txt_feedback);

        //hide feedback until its needed
        final RelativeLayout lay_feedback = (RelativeLayout) findViewById(R.id.lay_feedback);
        lay_feedback.setVisibility(View.INVISIBLE);


        // on inatiating the activity
        setBookValues();

        txt_feedback.setText("");
        allVocabulary = dbManager.getWordsByBookChapterUnitLevel(book, chapter, unit, level);

        getVocabularyEnglishGerman();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_feedback.setVisibility(View.INVISIBLE);
                edit_solution.setEnabled(true);
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
                        if (copyOfSentenceList.size() > 0){
//                            <TextView
//                            android:text=""
//                            android:layout_width="wrap_content"
//                            android:layout_height="wrap_content"
//                            android:layout_marginTop="10dp"
//                            android:layout_marginLeft="60dp"
//                            android:layout_marginRight="15dp"
//                            android:layout_marginBottom="10dp"
//                            android:id="@+id/txt_sentence03" />

                            TextView newSent = new TextView(Kontext.this);
                            // TODO: NO SAME SENTENCE
                            newSent.setText(ExerciseUtils.fromHtml(
                                           ExerciseUtils.deleteWordFromSentence(ExerciseUtils.getKontextPreferenceSentence(Kontext.this, dbManager, voc, "hint", "", ""), voc)));
                            //newSent.setText(ExerciseUtils.fromHtml(
                            //        ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(copyOfSentenceList.get(0)), voc)));
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            //converting pixels into dp, so that is the same scale as in the layout
                            int dpLeft = 60; // margin in dips
                            int dpRight = 15; // margin in dips
                            int dpBottom = 10; // margin in dips
                            float d = Kontext.this.getResources().getDisplayMetrics().density;
                            int marginLeft = (int)(dpLeft * d); // margin in pixels
                            int marginRight = (int)(dpRight * d); // margin in pixels
                            int marginBottom = (int)(dpBottom * d); // margin in pixels

                            // left, top, right, bottom
                            params.setMargins(marginLeft, 0, marginRight, marginBottom);
                            newSent.setLayoutParams(params);

                            lay_vocabel.addView(newSent);

                            copyOfSentenceList.remove(0);
                        }
                        else {
                            lay_feedback.setVisibility(View.VISIBLE);
                            txt_feedback.setText(ExerciseUtils.fromHtml("Es tud mir leid, es gibt keine weiteren Sätze für dieses Wort"));
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
                lay_feedback.setVisibility(View.VISIBLE);
                edit_solution.setEnabled(false);
                txt_feedback.setText(ExerciseUtils.fromHtml("Das gesuchte Wort ist <b><i>" + voc.getVoc() + "</i></b> und Übersetzt <b><i>" + voc.getTranslation() + "</i></b>"));
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
                                lay_feedback.setVisibility(View.VISIBLE);
                                txt_feedback.setText("Gratulation, das ist korrekt.");
                                allVocabulary.remove(voc);
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }
                            } else {
                                // Todo - feedback
                                lay_feedback.setVisibility(View.VISIBLE);
                                txt_feedback.setText(ExerciseUtils.fromHtml("Es tut mir leid, aber <i><b>" +
                                        edit_solution.getText().toString() + "</b></i> ist nicht korrekt."));
                            }
                    }
                    handled = true;
                }
                edit_solution.getText().clear();
                return handled;
            }
        });

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(Kontext.this);
            }
        });
    }

    private void setBookValues() {
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book_book", "0");

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

    private void getVocabularyEnglishGerman() {

        if (allVocabulary.size() == 0) {
            Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                    "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
        } else {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(allVocabulary.size());
            Log.d("KONTEXT:", Integer.toString(allVocabulary.size()));
            voc = allVocabulary.get(index);

            // TODO: MAKE SURE THERE ARE NO SAME SENTENCES
            List<String> gdexList = DBUtils.splitListString(voc.getGDEXSentences());
            List<String> bookList = DBUtils.splitListString(voc.getOldSentences());
            List<String> learnerList = DBUtils.splitListString(voc.getLearnerSentences());

            // get first preference sentence
            BookObject first_sentence = ExerciseUtils.getKontextPreferenceSentence(Kontext.this, dbManager, voc, "first", "", "");
            // get second preference sentence
            BookObject second_sentence = ExerciseUtils.getKontextPreferenceSentence(Kontext.this, dbManager, voc, "second", first_sentence.getSentence(), "");
            // get third preference sentence
            BookObject third_sentence = ExerciseUtils.getKontextPreferenceSentence(Kontext.this, dbManager, voc, "third", first_sentence.getSentence(), second_sentence.getSentence());

            txt_sent01.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(first_sentence, voc)));
            txt_sent02.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(second_sentence, voc)));
            txt_sent03.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(third_sentence, voc)));

            //Todo OLD
            /**List<String> sentenceList = DBUtils.splitListString(voc.getGDEXSentences());
            copyOfSentenceList = sentenceList;
            
            Log.d("TRANSLATION", Integer.toString(sentenceList.size()));
            if (sentenceList.size() >= 3) {
                Log.d("Kontext: sentenceList", sentenceList.toString());
                // MOVED HANDLING OF PARSING INTEGERS FROM STRINGS TO DatabaseManager.getSentence(String)
                txt_sent01.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(0)), voc)));
                txt_sent02.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(1)), voc)));
                txt_sent03.setText(ExerciseUtils.fromHtml(
                        ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(2)), voc)));

                copyOfSentenceList.remove(0);
                copyOfSentenceList.remove(1);
                copyOfSentenceList.remove(2);
            }
            else{
                // todo supplement sentences
                if (sentenceList.size() == 1){
                    txt_sent01.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(0)), voc)));
                    txt_sent02.setText("");
                    txt_sent03.setText("");

                    copyOfSentenceList.remove(0);
                }
                else if (sentenceList.size() == 2){
                    txt_sent01.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(0)), voc)));
                    txt_sent02.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.deleteWordFromSentence(dbManager.getSentence(sentenceList.get(1)), voc)));
                    txt_sent03.setText("");

                    copyOfSentenceList.remove(0);
                    copyOfSentenceList.remove(1);
                }
            }**/
        }
    }

    public SlidingLayer getSlidingLayer(){
        return mSlidingLayer;
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
            case R.id.item_settings:
                Intent intent_setting = new Intent(Kontext.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
