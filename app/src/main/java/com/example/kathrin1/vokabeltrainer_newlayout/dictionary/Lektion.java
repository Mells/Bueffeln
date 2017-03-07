package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseQuery;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.wunderlist.slidinglayer.SlidingLayer;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class Lektion extends AppCompatActivity {

    private DatabaseQuery databaseQuery = null;
    private ArrayList<VocObject> allVocabulary;
    private int position = 0;
    private VocObject vocable;
    private SlidingLayer mSlidingLayer;
    private TextView swipeText;
    private TextToSpeech convertToSpeech;

    private String book;
    private String chapter;
    private String unit;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lektion_entry_tabs);

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
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
        LayoutParams rlp = (LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        // --------------------------------------------

        databaseQuery = new DatabaseQuery(Lektion.this);

        setBookValues();

        allVocabulary = databaseQuery.getWordsByBookChapter(book, chapter, unit);

        final int len_vocabulary = allVocabulary.size()-1;

        final TextView txt_voc_de = (TextView) findViewById(R.id.txt_voc_de);
        final TextView txt_voc_en = (TextView) findViewById(R.id.txt_voc_en);
        Button btn_listen = (Button) findViewById(R.id.btn_listen);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);
        final TextView txt_lemma = (TextView) findViewById(R.id.txt_lemma_answer);
        final TextView txt_status = (TextView) findViewById(R.id.txt_status_answer);
        final TextView txt_wortart = (TextView) findViewById(R.id.txt_wortart_answer);
        final TextView txt_bsp = (TextView) findViewById(R.id.txt_example_answer);
        final TextView txt_fundort = (TextView) findViewById(R.id.txt_fundort_answer);
        RelativeLayout lay_dict = (RelativeLayout) findViewById(R.id.lay_dict);

        vocable = allVocabulary.get(position);

        setEntryText(vocable, txt_voc_de, txt_voc_en, txt_bsp, txt_fundort, txt_lemma, txt_status, txt_wortart);

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

        btn_auswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingLayer.openLayer(true);

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
                                convertToSpeech.setLanguage(Locale.UK);
                                convertToSpeech.speak(vocable.getVoc(), TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "not init" , Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
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
                .getString("chapter", "0");

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
    }

    private int setCurrentBook() {
        int tab = 0;
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book", "0");
        switch (pref_book){
            case "0": tab = 0;
                break;
            case "I": tab = 0;
                break;
            case "II": tab = 1;
                break;
            case "III": tab = 2;
                break;
        }
        return tab;
    }


    public void setEntryText(VocObject vocable, TextView txt_voc_de, TextView txt_voc_en, TextView txt_bsp, TextView txt_fundort, TextView txt_lemma, TextView txt_status, TextView txt_wortart){
        txt_voc_de.setText(vocable.getTranslation());
        txt_voc_en.setText(vocable.getVoc());
        txt_lemma.setText(vocable.getLemma().replaceAll("\\[", "").replaceAll("\\'","").replaceAll("\\]",""));
        txt_status.setText(vocable.getStatus());
        txt_wortart.setText(vocable.getPOS());
        //txt_bsp.setText();
        txt_fundort.setText(vocable.getChapter()+" / "+vocable.getBook());

        List<String> sentenceList = new ArrayList<String>(Arrays.asList(vocable.getSentences().substring(1, vocable.getSentences().length() - 1).split(", ")));
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sentenceList.size());
        Log.d("EN-SentenceList", sentenceList.toString());
        int numberSentence = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
        SentObject sentence = databaseQuery.getSentence(numberSentence);
        // TODO - highlight the word in bsp
        txt_bsp.setText(sentence.getSentence());
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
                Intent intent_help = new Intent(Lektion.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Lektion.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}