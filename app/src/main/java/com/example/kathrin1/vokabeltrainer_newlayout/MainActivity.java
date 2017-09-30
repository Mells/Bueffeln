package com.example.kathrin1.vokabeltrainer_newlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.kathrin1.vokabeltrainer_newlayout.achievement.AchievementMain;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.TheBook;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.Lektion;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.VocabularyDictionary;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.ExerciseSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.WordTest;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.status.Status;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_dictionary = (Button) findViewById(R.id.btn_dict);
        Button btn_lektion = (Button) findViewById(R.id.btn_lektion);
        Button btn_aufgabe = (Button) findViewById(R.id.btn_aufgabe);
        Button btn_status = (Button) findViewById(R.id.btn_status);
        Button btn_book_menu = (Button) findViewById(R.id.btn_book_menu);

        btn_dictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VocabularyDictionary.class);
                startActivity(intent);
            }
        });

        btn_lektion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Lektion.class);
                startActivity(intent);
            }
        });

        btn_aufgabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExerciseSelection.class);
                startActivity(intent);
            }
        });

        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Status.class);
                startActivity(intent);
            }
        });

        btn_book_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TheBook.class);
                startActivity(intent);
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
//            case R.id.item_help:
//                Intent intent_help = new Intent(MainActivity.this, Help.class);
//                startActivity(intent_help);
//                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(MainActivity.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
//            case R.id.item_achievement:
//                Intent intent_achievement = new Intent(MainActivity.this, AchievementMain.class);
//                startActivity(intent_achievement);
//                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
