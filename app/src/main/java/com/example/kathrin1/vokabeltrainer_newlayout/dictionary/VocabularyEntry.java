package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.ExerciseUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.WordTest;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kathrin1 on 21.02.17.
 */

public class VocabularyEntry extends AppCompatActivity {

    private TextToSpeech convertToSpeech;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_entry);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int vocId = bundle.getInt("voc_id");

        final DatabaseManager databaseQuery = DatabaseManager.build(VocabularyEntry.this);
        final VocObject vocable = databaseQuery.getWordPairById(vocId);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);

        TextView txt_voc_de = (TextView) findViewById(R.id.txt_voc_de);
        TextView txt_voc_en = (TextView) findViewById(R.id.txt_voc_en);
        Button btn_listen = (Button) findViewById(R.id.btn_listen);
        TextView txt_lemma = (TextView) findViewById(R.id.txt_lemma_answer);
        TextView txt_status = (TextView) findViewById(R.id.txt_status_answer);
        TextView txt_wortart = (TextView) findViewById(R.id.txt_wortart_answer);
        TextView txt_bsp = (TextView) findViewById(R.id.txt_example_answer);
        TextView txt_fundort = (TextView) findViewById(R.id.txt_fundort_answer);

        String german = vocable.getTranslation();
        if (vocable.getProcessedTranslation().size() > 1) {
            german = german + "<\\br><small>";
            for (ArrayList<String> g_array : (ArrayList<ArrayList<String>>) vocable.getProcessedTranslation()) {
                for (String s : g_array) {
                    german = german + s + ", ";
                }
            }
            german = german.replaceAll(", $", "");
            german = german + "<\\small>";

        }
        txt_voc_de.setText(ExerciseUtils.fromHtml(german));

        String english = vocable.getVoc();
        if (vocable.getProcessedVocable().size() > 1) {
            english = english + "<\\br><small>";
            for (ArrayList<String> g_array : (ArrayList<ArrayList<String>>) vocable.getProcessedVocable()) {
                for (String s : g_array) {
                    english = english + s + ", ";
                }
            }
            english = english.replaceAll(", $", "");
            english = english + "<\\small>";

        }
        txt_voc_en.setText(ExerciseUtils.fromHtml(english));
        txt_lemma.setText(vocable.getLemmaVocable().toString().replaceAll("\\[", "").replaceAll("\\'","").replaceAll("\\]",""));
        txt_status.setText(vocable.getStatus());
        txt_wortart.setText(posToString(vocable.getPOS()));

        txt_fundort.setText(vocable.getChapter()+" / "+vocable.getBook());

        dbManager = DatabaseManager.build(VocabularyEntry.this);

        BookObject sentence = ExerciseUtils.getWorttestSentence(VocabularyEntry.this, dbManager, vocable);
        txt_bsp.setText(ExerciseUtils.fromHtml(
                    ExerciseUtils.replaceWordInSentence(sentence, vocable, "<b><big>%s</big></b>")));
        //BookObject sentence = databaseQuery.getKontextSentence(numberSentence);



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
                                ArrayList<ArrayList<String>> x = vocable.getProcessedVocable();

                                convertToSpeech.setLanguage(Locale.UK);
                                Log.d("VOcabularyEntry", x.toString());
                                for (ArrayList<String> ar : x){
                                    Log.d("VOcabularyEntry", ar.toString());
                                    for (String s : ar){
                                        Log.d("VOcabularyEntry", s);
                                        convertToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null, null);
                                        convertToSpeech.playSilentUtterance(1, TextToSpeech.QUEUE_ADD, null);
                                    }
                                }
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
                NavUtils.navigateUpFromSameTask(VocabularyEntry.this);
            }
        });
    }

    private String posToString(String pos){
        String posToString = "";
        switch (pos){
            case "a": posToString = "Adjektive";
            case "av": posToString = "Adverb";
            case "d": posToString = "Artikel";
            case "i": posToString = "Interjektion";
            case "irr": posToString = "Irregulär";
            case "k": posToString = "Konjuktion";
            case "p": posToString = "Pronomen";
            case "ph": posToString = "Phrase";
            case "pr": posToString = "Präposition";
            case "s": posToString = "Nomen";
            case "v": posToString = "Verb";
            case "a/av": posToString = "Adjektive/Adverb";
            case "av/pr": posToString = "Adverb/Präposition";
            case "s/a": posToString = "Nomen/Adjektive";
        }
        return posToString;
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
//            case R.id.item_help:
//                Intent intent_help = new Intent(VocabularyEntry.this, Help.class);
//                startActivity(intent_help);
//                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(VocabularyEntry.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(VocabularyEntry.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
