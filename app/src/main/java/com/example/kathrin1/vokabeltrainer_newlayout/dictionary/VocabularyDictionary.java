package com.example.kathrin1.vokabeltrainer_newlayout.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.ItemObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class VocabularyDictionary extends AppCompatActivity {

    private int startPosition = 0;
    private EditText filterText;
    private ArrayAdapter<ItemObject> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict);

        filterText = (EditText)findViewById(R.id.edit_solution);
        final ListView itemList = (ListView)findViewById(R.id.listView);

        final DatabaseManager databaseQuery = DatabaseManager.build(VocabularyDictionary.this);
        //final String[] terms = databaseQuery.dictionaryWords();


        List<ItemObject> vocabulary = new ArrayList<ItemObject>();
        int i = 0;
        for (String term : databaseQuery.dictionaryEnglishWords()) {
            vocabulary.add(new ItemObject(i++, true, term));
        }
        i = 0;
        for (String term : databaseQuery.dictionaryGermanWords()) {
            vocabulary.add(new ItemObject(i++, false, term));
        }
        //ItemObject employee = employees.get(0);

        listAdapter = new ArrayAdapter<ItemObject>(this, android.R.layout.simple_list_item_1, android.R.id.text1, vocabulary);

        itemList.setAdapter(listAdapter);
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //int wordid = position + 1;

                //VocObject allVocabulary = databaseQuery.getWordPairById(listAdapter.getItem(position).oldPos()+1);

                //String text = "English: " + allVocabulary.getVoc() + "\n"
                //        + "Deutsch: " + allVocabulary.getTranslation();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(VocabularyDictionary.this, VocabularyEntry.class);
                intent.putExtra("voc_id", listAdapter.getItem(position).oldPos()+1);
                startActivity(intent);
            }
        });



        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                VocabularyDictionary.this.listAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
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
                Intent intent_help = new Intent(VocabularyDictionary.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(VocabularyDictionary.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}