package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseQuery;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_entry);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int vocId = bundle.getInt("voc_id");

        final DatabaseQuery databaseQuery = new DatabaseQuery(VocabularyEntry.this);
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

        List<String> sentenceList = new ArrayList<String>(Arrays.asList(vocable.getSentences().substring(1, vocable.getSentences().length() - 1).split(", ")));
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sentenceList.size());
        Log.d("EN-SentenceList", sentenceList.toString());
        int numberSentence = Integer.parseInt(sentenceList.get(index).substring(1, sentenceList.get(index).length() - 1));
        SentObject sentence = databaseQuery.getSentence(numberSentence);
        // TODO - highlight the word in bsp
        txt_bsp.setText(sentence.getSentence());
        

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
}
