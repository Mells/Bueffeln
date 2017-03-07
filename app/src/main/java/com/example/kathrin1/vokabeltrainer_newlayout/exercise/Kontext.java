package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseQuery;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kathrin1 on 21.12.16.
 */

public class Kontext extends AppCompatActivity {

    private DatabaseQuery databaseQuery = null;

    private ArrayList<VocObject> allVocabulary;
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
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        // --------------------------------------------

        databaseQuery = new DatabaseQuery(Kontext.this);

        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button btn_solution = (Button) findViewById(R.id.btn_solution);
        Button btn_hint = (Button) findViewById(R.id.btn_hint);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);

        txt_sent01 = (TextView) findViewById(R.id.txt_sentence01);
        txt_sent02 = (TextView) findViewById(R.id.txt_sentence02);
        txt_sent03 = (TextView) findViewById(R.id.txt_sentence03);

        final TextView txt_feedback = (TextView) findViewById(R.id.txt_feedback);

        // on inatiating the activity
        setBookValues();

        txt_feedback.setText("");
        allVocabulary = databaseQuery.getWordsByBookChapterLevel(book, chapter, unit, level);

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

        btn_solution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    txt_feedback.setText(fromHtml("Das gesuchte Wort ist <b><i>" + voc.getVoc() + "</i></b> und Übersetzt <b><i>" + voc.getTranslation() + "</i></b>"));
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

    private void getVocabularyEnglishGerman() {

        if (allVocabulary.size() == 0) {
            Toast.makeText(getApplicationContext(), "Es gibt keine Vokabeln, die diese " +
                    "Kriterien beinhalten.", Toast.LENGTH_LONG).show();
        } else {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(allVocabulary.size());
            Log.d("TRANSLATION:", Integer.toString(allVocabulary.size()));
            voc = allVocabulary.get(index);

            List<String> sentenceList = new ArrayList<String>(Arrays.asList(voc.getSentences().substring(1, voc.getSentences().length() - 1).split(", ")));
            Log.d("TRANSLATION", Integer.toString(sentenceList.size()));
            // TODO - take gdex not random
            if (sentenceList.size() >= 3) {
                index = randomGenerator.nextInt(sentenceList.size());
                Log.d("TRANSLATION", Integer.toString(index));
                int numberSentence1 = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
                sentenceList.remove(index);
                index = randomGenerator.nextInt(sentenceList.size());
                int numberSentence2 = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
                sentenceList.remove(index);
                index = randomGenerator.nextInt(sentenceList.size());
                int numberSentence3 = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
                //SentObject sentence = databaseQuery.getSentence(numberSentence1).getSentence();
                // TODO - delete the word in sentences



                txt_sent01.setText(deleteWordFromSentence(databaseQuery.getSentence(numberSentence1)));
                txt_sent02.setText(deleteWordFromSentence(databaseQuery.getSentence(numberSentence2)));
                txt_sent03.setText(deleteWordFromSentence(databaseQuery.getSentence(numberSentence3)));
            }
            else{
                // todo supplement sentences
                Log.d("List:", sentenceList.toString());
                if (sentenceList.size() == 1){
                    Log.d("List", sentenceList.toString());
                    txt_sent01.setText(deleteWordFromSentence(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(0).substring(1, sentenceList.get(0).length() - 1)))));
                    txt_sent02.setText("Kein Satz vorhanden, entschuldigung.");
                    txt_sent03.setText("Kein Satz vorhanden, entschuldigung.");
                }
                else if (sentenceList.size() == 2){
                    txt_sent01.setText(deleteWordFromSentence(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(0).substring(1, sentenceList.get(0).length() - 1)))));
                    txt_sent02.setText(deleteWordFromSentence(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(1).substring(1, sentenceList.get(1).length() - 1)))));
                    txt_sent03.setText("Kein Satz vorhanden, entschuldigung.");
                }
                //else if (sentenceList.size() == 3){
                //    txt_sent01.setText(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(0).substring(1, sentenceList.get(0).length() - 1))).getSentence());
                //    txt_sent02.setText(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(1).substring(1, sentenceList.get(1).length() - 1))).getSentence());
                //    txt_sent03.setText(databaseQuery.getSentence(Integer.parseInt(sentenceList.get(2).substring(1, sentenceList.get(2).length() - 1))).getSentence());
                //}

            }
        }
    }

    private String deleteWordFromSentence(SentObject sentence) {
        Map<String, List<String>> smap = stringToMap(sentence.getTagged());

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
                    sent = sent.replaceAll(s, "___");
                }
            }
        }
        return sent;
    }

    public SlidingLayer getSlidingLayer(){
        return mSlidingLayer;
    }

    private Map stringToMap(String sentence) {
        String punctutations = ".,:;?";
        Map<String, List<String>> smap = new HashMap<String, List<String>>();
        // {'car': ['car'], 'A': ['A'], '.': ['.']}
        String[] pma = sentence.substring(1, sentence.length() - 2).replaceAll("\\[", "").replaceAll("\\'", "").split("\\], ");
        // car: car -   A: A    -   .: .
        //Log.d("errorSent", sentence);
        //Log.d("errorPma", Arrays.toString(pma));
        for (String item : pma) {
            // ,: ,, ,, ,, ,]
            String[] keyVal = item.split(": ");
            if (punctutations.contains(keyVal[0])) {
                continue;
            }
            //Log.d("error", Arrays.toString(keyVal));
            //Log.d("error", keyVal[0]+" - "+keyVal[1]);
            smap.put(keyVal[0], Arrays.asList(keyVal[1].split(",")));
        }
        return smap;
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
