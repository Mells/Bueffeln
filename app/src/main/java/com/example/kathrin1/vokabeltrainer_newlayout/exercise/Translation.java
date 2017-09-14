package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.TheBook;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kathrin1 on 20.12.16.
 */

public class Translation extends AppCompatActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private DatabaseManager dbManager = null;
    private VocObject voc;
    private List<VocObject> allVocabulary;
    private TextView txt_voc;
    private TextView txt_bsp;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    private String feedback_answer;

    private int currentLayoutId = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_translation);

        dbManager = DatabaseManager.build(Translation.this);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);
        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button btn_solution = (Button) findViewById(R.id.btn_solution);
        final Button btn_menu = (Button) findViewById(R.id.btn_menu);
        final Button btn_book_menu = (Button) findViewById(R.id.btn_book_menu);


        txt_voc = (TextView) findViewById(R.id.txt_voc);
        txt_bsp = (TextView) findViewById(R.id.txt_bsp);

        final EditText edit_solution = (EditText) findViewById(R.id.edit_solution);
        final Switch sw_language = (Switch) findViewById(R.id.sw_language);
        //final TextView txt_feedback = (TextView) findViewById(R.id.txt_feedback);

        final RelativeLayout lay_feedback = (RelativeLayout) findViewById(R.id.lay_feedback);
        final RelativeLayout lay_eingabe = (RelativeLayout) findViewById(R.id.lay_eingabe);
        final LinearLayout lay_menu_buttons = (LinearLayout) findViewById(R.id.lay_menu_buttons);
        final RelativeLayout lay_feedback_scroll = (RelativeLayout) findViewById(R.id.lay_feedback_scroll);
        final ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);

        // on inatiating the activity
        setBookValues();

        Log.d("Test voc", String.valueOf(dbManager.getWordPairById(1).getVoc()));
        Log.d("Test test", String.valueOf(dbManager.getWordPairById(1).getTested()));
        Log.d("Test book", String.valueOf(dbManager.getWordPairById(1).getBook()));
        Log.d("Test chap", String.valueOf(dbManager.getWordPairById(1).getChapter()));

        allVocabulary = dbManager.getWordsByBookChapterUnitLevel(book, chapter, unit, level);

        if(!sw_language.isChecked()){
            getVocabularyEnglish();
        }
        else {
            getVocabularyGerman();
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_solution.setEnabled(true);
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
                if (lay_feedback_scroll.getChildCount() > 0) {
                    (lay_feedback_scroll).removeAllViews();
                }
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lay_eingabe.getVisibility() == View.VISIBLE){
                    lay_eingabe.setVisibility(View.INVISIBLE);
                    lay_menu_buttons.setVisibility(View.VISIBLE);
                    btn_book_menu.setVisibility(View.VISIBLE);
                }
                else {
                    lay_eingabe.setVisibility(View.VISIBLE);
                    lay_menu_buttons.setVisibility(View.INVISIBLE);
                    btn_book_menu.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_solution.setEnabled(false);
                String feedback = "";
                if (!sw_language.isChecked()) {
                    feedback = "Die korrekte Übersetzung für <b><i>" + voc.getVoc() + "</i></b> ist <b><i>" + voc.getTranslation() + "</i></b>.";
                    lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback));
                    scroll.postDelayed(new Runnable() { @Override public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                    //txt_feedback.setText(ExerciseUtils.fromHtml("Die korrekte Übersetzung für <b><i>" + voc.getVoc() + "</i></b> ist <b><i>" + voc.getTranslation() + "</i></b>"));
                }
                else {
                    feedback = "Die korrekte Übersetzung für <b><i>" + voc.getTranslation() + "</i></b> ist <b><i>" + voc.getVoc() + "</i></b>.";
                    lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback));
                    scroll.postDelayed(new Runnable() { @Override public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                    //txt_feedback.setText(ExerciseUtils.fromHtml("Die korrekte Übersetzung für <b><i>" + voc.getTranslation() + "</i></b> ist <b><i>" + voc.getVoc() + "</i></b>"));
                }
            }
        });

        btn_book_menu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(Translation.this, TheBook.class);
                 startActivity(intent);
                 setBookValues();
             }
        });

        edit_solution.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // hide keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    if (edit_solution.getText().toString().length() > 0) {
                        // German Input
                        if (!sw_language.isChecked()) {
                            if (edit_solution.getText().toString().equals(voc.getTranslation())) {
                                feedback_answer = "Gratulation, das ist korrekt.";
                                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);

                                allVocabulary.remove(voc);
                                // write to database
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }

                            } else {
                                // TODO feedback
                                feedback_answer = "Es tut mir leid, aber <i><b>" +
                                        edit_solution.getText().toString() + "</b></i> ist nicht korrekt.";
                                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                            }
                        // English Input
                        } else {
                            if (edit_solution.getText().toString().equals(voc.getVoc())) {
                                lay_feedback_scroll.addView(createNewRelativeLayoutView("Gratulation, das ist korrekt."));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                                edit_solution.setEnabled(false);
                                btn_menu.performClick();
                                //lay_feedback.setVisibility(View.VISIBLE);
                                //txt_feedback.setText("Gratulation, das ist korrekt.");
                                allVocabulary.remove(voc);
                                // write to database
                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }

                            } else {
                                Feedback feedback = new Feedback(edit_solution.getText().toString(),
                                        voc, true, Translation.this);
                                feedback_answer = feedback.generateFeedback();
                                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                                //lay_feedback.setVisibility(View.VISIBLE);
                                //txt_feedback.setText(answer);
                            }
                        }
                    }
                    handled = true;
                }
                edit_solution.getText().clear();
                return handled;
            }
        });

        sw_language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit_solution.setEnabled(false);
                if(!sw_language.isChecked()){
                    txt_voc.setText(voc.getVoc());
                    // TODO:  get original sentence
                    BookObject sentence = dbManager.getExampleGDEXSentence(voc);
                    txt_bsp.setText(ExerciseUtils.fromHtml(
                            ExerciseUtils.replaceWordInSentence(sentence, voc, "<b><big>%s</big></b>")));
                }
                else {
                    txt_voc.setText(voc.getTranslation());

                    // TODO:  get original sentence
                    BookObject sentence = dbManager.getExampleGDEXSentence(voc);
                    txt_bsp.setText(ExerciseUtils.deleteWordFromSentence(sentence, voc));
                }
            }
        });

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(Translation.this);
            }
        });
    }

    private void setBookValues() {

        book = ExerciseUtils.getPreferenceBook(this);

        chapter = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("chapter", "Welcome");

        unit = ExerciseUtils.getPreferenceUnit(this);

        level = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("level", 0);
    }

    private void getVocabularyGerman() {
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

            BookObject sentence = ExerciseUtils.getTranslationPreferenceSentence(Translation.this, dbManager, voc);
            txt_bsp.setText(ExerciseUtils.deleteWordFromSentence(sentence, voc));
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

            BookObject sentence = ExerciseUtils.getTranslationPreferenceSentence(Translation.this, dbManager, voc);
            txt_bsp.setText(ExerciseUtils.fromHtml(
                    ExerciseUtils.replaceWordInSentence(sentence, voc, "<b><big>%s</big></b>")));
        }
    }

    private TextView createNewTextView(String text) {
        //android:layout_width="wrap_content"
        //android:layout_height="wrap_content"
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        //android:layout_marginLeft="15dp"
        //android:layout_marginRight="60dp"
        params.setMargins(pixelToDips(15), 0, pixelToDips(60), 0);

        //android:layout_centerVertical="true"
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        //android:layout_alignParentLeft="true"
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        //android:layout_alignParentStart="true"
        params.addRule(RelativeLayout.ALIGN_PARENT_START);

        //android:layout_alignParentRight="true"
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        //android:layout_alignParentEnd="true"
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText(ExerciseUtils.fromHtml(text));
        return textView;
    }

    private RelativeLayout createNewRelativeLayoutView(String s){
        //android:layout_width="match_parent"
        //android:layout_height="wrap_content"
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //android:layout_marginTop="30dp"
        params.setMargins(0, pixelToDips(15), 0, 0);
        if (currentLayoutId != -1) {
            Log.d("below", String.valueOf(currentLayoutId));
            params.addRule(RelativeLayout.BELOW, currentLayoutId);
        }

        final RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(params);

        //android:background="@drawable/ic_sprechblase_main_rechts"
        relativeLayout.setBackground(ContextCompat.getDrawable(Translation.this, R.drawable.ic_sprechblase_main_rechts));

        int theId = generateViewId();
        Log.d("ID", String.valueOf(theId));
        relativeLayout.setId(theId);
        currentLayoutId = theId;

        relativeLayout.addView(createNewTextView(String.valueOf(s)));
        return relativeLayout;
    }

    private int pixelToDips(int x){
        int dpValue = x; // margin in dips
        float d = Translation.this.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);
        return margin;
    }

    @SuppressLint("NewApi")
    public int generateViewId() {

        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
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
            case R.id.item_settings:
                Intent intent_setting = new Intent(Translation.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
