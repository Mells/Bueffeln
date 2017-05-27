package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.Settings;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
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

        TextView txt_voc_de = (TextView) findViewById(R.id.txt_voc_de);
        TextView txt_voc_en = (TextView) findViewById(R.id.txt_voc_en);
        Button btn_listen = (Button) findViewById(R.id.btn_listen);
        TextView txt_lemma = (TextView) findViewById(R.id.txt_lemma_answer);
        TextView txt_status = (TextView) findViewById(R.id.txt_status_answer);
        TextView txt_wortart = (TextView) findViewById(R.id.txt_wortart_answer);
        TextView txt_bsp = (TextView) findViewById(R.id.txt_example_answer);
        TextView txt_fundort = (TextView) findViewById(R.id.txt_fundort_answer);

        txt_voc_de.setText(vocable.getTranslation());
        txt_voc_en.setText(vocable.getVoc());
        txt_lemma.setText(vocable.getLemma().replaceAll("\\[", "").replaceAll("\\'","").replaceAll("\\]",""));
        txt_status.setText(vocable.getStatus());
        txt_wortart.setText(vocable.getPOS());
        //txt_bsp.setText();
        txt_fundort.setText(vocable.getChapter()+" / "+vocable.getBook());

        dbManager = DatabaseManager.build(VocabularyEntry.this);

        // TODO:  CHANGE SENTENCE TYPE HERE
        List<String> sentenceList = new ArrayList<String>(Arrays.asList(vocable.getGDEXSentences().substring(1, vocable.getGDEXSentences().length() - 1).split(", ")));
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sentenceList.size());
        Log.d("EN-SentenceList", sentenceList.toString());
        try {
            int numberSentence = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
            SentObject sentence = dbManager.getSentence(numberSentence);
            // TODO - highlight the word in bsp
            txt_bsp.setText(sentence.getSentence());
        } catch (NumberFormatException e) // Thrown if there if the string could not be parsed as an int
        {
            // If the sentence list value cannot be parsed as an integer, just display it
            txt_bsp.setText(R.string.Sent_Missing);
        }
        //SentObject sentence = databaseQuery.getSentence(numberSentence);



        btn_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            convertToSpeech.setLanguage(Locale.ENGLISH);
                            convertToSpeech.speak(vocable.getVoc(), TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                });
            }
        });
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
                Intent intent_help = new Intent(VocabularyEntry.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(VocabularyEntry.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(VocabularyEntry.this, Settings.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
