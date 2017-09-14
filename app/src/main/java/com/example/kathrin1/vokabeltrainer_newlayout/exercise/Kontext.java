package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.TheBook;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Kontext extends AppCompatActivity {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private DatabaseManager dbManager = null;

    private List<VocObject> allVocabulary;
    private TextView txt_sent01;
    private TextView txt_sent02;
    private TextView txt_sent03;
    private VocObject voc;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    String feedback_answer;

    private int currentLayoutId = -1;

    private List<String> copyOfSentenceList;

    ArrayList<String> currentlyUsedSentences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercise_kontext);

        dbManager = DatabaseManager.build(Kontext.this);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);
        final Button btn_next = (Button) findViewById(R.id.btn_next);
        final Button btn_solution = (Button) findViewById(R.id.btn_solution);
        final Button btn_hint = (Button) findViewById(R.id.btn_hint);
        Button btn_menu = (Button) findViewById(R.id.btn_menu);
        final Button btn_book_menu = (Button) findViewById(R.id.btn_book_menu);

        final LinearLayout lay_vocabel = (LinearLayout) findViewById(R.id.lay_vocabel);
        final RelativeLayout lay_eingabe = (RelativeLayout) findViewById(R.id.lay_eingabe);
        final LinearLayout lay_menu_buttons = (LinearLayout) findViewById(R.id.lay_menu_buttons);

        txt_sent01 = (TextView) findViewById(R.id.txt_sentence01);
        txt_sent02 = (TextView) findViewById(R.id.txt_sentence02);
        txt_sent03 = (TextView) findViewById(R.id.txt_sentence03);

        final EditText edit_solution = (EditText) findViewById(R.id.edit_solution);
        final RelativeLayout lay_feedback_scroll = (RelativeLayout) findViewById(R.id.lay_feedback_scroll);
        final ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);

        // on inatiating the activity
        setBookValues();

        currentlyUsedSentences = new ArrayList<>();

        allVocabulary = dbManager.getWordsByBookChapterUnitLevel(book, chapter, unit, level);

        getVocabularyEnglishGerman();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_solution.setEnabled(true);
                btn_hint.setEnabled(true);
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

        btn_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - next word
                if (allVocabulary != null){
                    if (allVocabulary.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                                "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
                    } else {
                        //if (copyOfSentenceList.size() > 0){
                        Log.d("Kontext: inHint", currentlyUsedSentences.toString());
                        BookObject hint_sentence = ExerciseUtils.getSentence(
                                                Kontext.this, dbManager, voc, "hint", currentlyUsedSentences);
                        if (!hint_sentence.getSentence().equals("")){
                            currentlyUsedSentences.add(hint_sentence.getSentence());

                            lay_feedback_scroll.addView(createNewRelativeLayoutView(ExerciseUtils.deleteWordFromSentence(hint_sentence, voc)));
                            scroll.postDelayed(new Runnable() { @Override public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
                        }
                        else {
                            feedback_answer = "Es tut mir leid, es gibt keine weiteren Sätze für dieses Wort";
                            lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                            scroll.postDelayed(new Runnable() { @Override public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
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
                edit_solution.setEnabled(false);
                btn_hint.setEnabled(false);
                feedback_answer = "Die korrekte Übersetzung für <b><i>" + voc.getVoc() + "</i></b> ist <b><i>" + voc.getTranslation() + "</i></b>.";
                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                scroll.postDelayed(new Runnable() { @Override public void run() {
                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
            }
        });

        btn_book_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Kontext.this, TheBook.class);
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
                            if (edit_solution.getText().toString().equals(voc.getTranslation())) {

                                feedback_answer = "Gratulation, das ist korrekt.";
                                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);

                                allVocabulary.remove(voc);

                                if (voc.getId() > 6) {
                                    dbManager.updateTested(voc.getTested() + 1, voc.getId());
                                }
                            } else {
                                // Todo - feedback
                                Feedback feedback = new Feedback(edit_solution.getText().toString(),
                                        voc, true, Kontext.this);
                                feedback_answer = feedback.generateFeedback();
                                lay_feedback_scroll.addView(createNewRelativeLayoutView(feedback_answer));
                                scroll.postDelayed(new Runnable() { @Override public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN); } }, 250);
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

        book = ExerciseUtils.getPreferenceBook(this);

        chapter = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("chapter", "Welcome");

        unit = ExerciseUtils.getPreferenceUnit(this);

        level = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("level", 0);
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
            BookObject first_sentence = ExerciseUtils.getSentence(Kontext.this, dbManager, voc, "first", currentlyUsedSentences);
            if(!first_sentence.getSentence().equals("")) {
                currentlyUsedSentences.add(first_sentence.getSentence());
            }
            Log.d("Kontext: CUS:", currentlyUsedSentences.toString());

            // get second preference sentence
            BookObject second_sentence = ExerciseUtils.getSentence(Kontext.this, dbManager, voc, "second", currentlyUsedSentences);
            if(!first_sentence.getSentence().equals("")) {
                currentlyUsedSentences.add(second_sentence.getSentence());
            }
            Log.d("Kontext: CUS:", currentlyUsedSentences.toString());

            // get third preference sentence
            BookObject third_sentence = ExerciseUtils.getSentence(Kontext.this, dbManager, voc, "third", currentlyUsedSentences);
            if(!first_sentence.getSentence().equals("")) {
                currentlyUsedSentences.add(third_sentence.getSentence());
            }
            Log.d("Kontext: CUS:", currentlyUsedSentences.toString());

            txt_sent01.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(first_sentence, voc)));
            txt_sent02.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(second_sentence, voc)));
            txt_sent03.setText(ExerciseUtils.fromHtml(ExerciseUtils.deleteWordFromSentence(third_sentence, voc)));
        }
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
        relativeLayout.setBackground(ContextCompat.getDrawable(Kontext.this, R.drawable.ic_sprechblase_main_rechts));

        int theId = generateViewId();
        Log.d("ID", String.valueOf(theId));
        relativeLayout.setId(theId);
        currentLayoutId = theId;

        relativeLayout.addView(createNewTextView(String.valueOf(s)));
        return relativeLayout;
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
        // todo changed to ALIGN_PARENT_TOP
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        //android:layout_alignParentRight="true"
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        //android:layout_alignParentEnd="true"
        // todo changed to ALIGN_PARENT_BOTTOM
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText(ExerciseUtils.fromHtml(text));
        return textView;
    }

    private int pixelToDips(int x){
        float d = Kontext.this.getResources().getDisplayMetrics().density;
        return (int)(x * d);
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
