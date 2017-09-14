package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.TheBook;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.AufgabeAuswahl;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.ExerciseUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class Lektion extends AppCompatActivity {

    private DatabaseManager dbManager = null;
    private List<VocObject> allVocabulary;
    private int position = 0;
    private VocObject vocable;
    private TextToSpeech convertToSpeech;

    private TextView txt_lemma;
    private TextView txt_status;
    private TextView txt_wortart;
    private TextView txt_bsp;
    private TextView txt_fundort;
    private TextView txt_voc_de;
    private TextView txt_voc_en;
    
    private int len_vocabulary;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_lektion);


        dbManager = DatabaseManager.build(Lektion.this);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);
        txt_voc_de = (TextView) findViewById(R.id.txt_voc_de);
        txt_voc_en = (TextView) findViewById(R.id.txt_voc_en);
        Button btn_listen = (Button) findViewById(R.id.btn_listen);
        Button btn_book_menu = (Button) findViewById(R.id.btn_book_menu);
        txt_lemma = (TextView) findViewById(R.id.txt_lemma_answer);
        txt_status = (TextView) findViewById(R.id.txt_status_answer);
        txt_wortart = (TextView) findViewById(R.id.txt_wortart_answer);
        txt_bsp = (TextView) findViewById(R.id.txt_example_answer);
        txt_fundort = (TextView) findViewById(R.id.txt_fundort_answer);
        RelativeLayout lay_dict = (RelativeLayout) findViewById(R.id.lay_dict);

        setBookValues();

        setVocabulary();

        lay_dict.setOnTouchListener(new OnSwipeTouchListener(Lektion.this) {
            public void onSwipeRight() {
                position = position + 1;
                if (position > len_vocabulary) {
                    position = 0;
                }
                vocable = allVocabulary.get(position);
                setEntryText(vocable, txt_voc_de, txt_voc_en, txt_bsp, txt_fundort, txt_lemma, txt_status, txt_wortart);

            }
            public void onSwipeLeft() {
                position = position - 1;
                if (position < 0) {
                    position = len_vocabulary;
                }
                vocable = allVocabulary.get(position);
                setEntryText(vocable, txt_voc_de, txt_voc_en, txt_bsp, txt_fundort, txt_lemma, txt_status, txt_wortart);
            }
        });

        btn_book_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Lektion.this, TheBook.class);
                startActivity(intent);
                setBookValues();
                setVocabulary();
            }
        });

        btn_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            if(status == TextToSpeech.SUCCESS)
                            {
                                Log.d("Lektion: for cts", vocable.getVoc());
                                //String cts = vocable.getVoc().replace("(=", " also ");
                                String cts = vocable.getVoc().replaceAll("[\\(, \\), \\=]", " ");
                                Log.d("Lektion: cts",cts);
                                convertToSpeech.setLanguage(Locale.UK);
                                String[] word = cts.split("\\s+");
                                Log.d("Lektion: tts", Arrays.toString(word));

                                convertToSpeech.speak(cts, TextToSpeech.QUEUE_FLUSH, null, null);
//                                if (word.length == 1){
//                                }
//                                else {
//                                    for (String part : word) {
//                                        convertToSpeech.speak(part, TextToSpeech.QUEUE_ADD, null, null);
//                                        convertToSpeech.playSilentUtterance(1, TextToSpeech.QUEUE_ADD, null);
//                                    }
//                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "not init" , Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
            }
        });

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(Lektion.this);
            }
        });
    }

    public void setVocabulary() {
        allVocabulary = dbManager.getWordsByBookChapterUnit(book, chapter, unit);

        len_vocabulary = allVocabulary.size()-1;

        if (len_vocabulary > -1){

            vocable = allVocabulary.get(position);

            setEntryText(vocable, txt_voc_de, txt_voc_en, txt_bsp, txt_fundort, txt_lemma, txt_status, txt_wortart);
        }
    }

    private void setBookValues() {

        book = ExerciseUtils.getPreferenceBook(this);

        chapter = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("chapter", "Welcome");

        unit = ExerciseUtils.getPreferenceUnit(this);

        // todo used here?
        level = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("level", 0);
    }

    public void setEntryText(VocObject vocable, TextView txt_voc_de, TextView txt_voc_en, TextView txt_bsp, TextView txt_fundort, TextView txt_lemma, TextView txt_status, TextView txt_wortart){
        txt_voc_de.setText(vocable.getTranslation());
        txt_voc_en.setText(vocable.getVoc());
        // Replace all brackets and apostrophes
        txt_lemma.setText(vocable.getLemmaVocable().toString().replaceAll("[\\[\\]']", ""));
        txt_status.setText(vocable.getStatus());
        txt_wortart.setText(vocable.getPOS());
        //txt_bsp.setText();
        txt_fundort.setText(vocable.getChapter()+" / "+vocable.getBook());

        // TODO:  CHANGE SENTENCE TYPE HERE
        List<String> sentenceList = DBUtils.splitListString(vocable.getGDEXSentences());
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sentenceList.size());
        Log.d("EN-SentenceList", sentenceList.toString());

        BookObject sentence = dbManager.getSentence(sentenceList.get(index));
        // TODO - highlight the word in bsp
        txt_bsp.setText(ExerciseUtils.fromHtml(
                ExerciseUtils.replaceWordInSentence(sentence, vocable, "<b><big>%s</big></b>")));
        //txt_bsp.setText(sentence.getSentence());
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
                Intent intent_help = new Intent(Lektion.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Lektion.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(Lektion.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}