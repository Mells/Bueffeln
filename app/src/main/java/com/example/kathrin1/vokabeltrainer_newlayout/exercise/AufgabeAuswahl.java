package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.TheBook;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.VocabularyEntry;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingSelection;
import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.wunderlist.slidinglayer.SlidingLayer;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class AufgabeAuswahl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_selection);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);
        Button btn_kartei = (Button) findViewById(R.id.btn_open_kartei);
        Button btn_kontext = (Button) findViewById(R.id.btn_open_kontext);
        Button btn_book_menu = (Button) findViewById(R.id.btn_book_menu);
        Button btn_exercise3 = (Button) findViewById(R.id.btn_open_ex3);

        btn_kartei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AufgabeAuswahl.this, Translation.class);
                startActivity(intent);
            }

        });

        btn_kontext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AufgabeAuswahl.this, Kontext.class);
                startActivity(intent);
            }

        });

        btn_exercise3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AufgabeAuswahl.this, TrainAndTest.class);
                startActivity(intent);
            }

        });

        btn_book_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AufgabeAuswahl.this, TheBook.class);
                startActivity(intent);
            }
        });

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(AufgabeAuswahl.this);
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
                Intent intent_help = new Intent(AufgabeAuswahl.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(AufgabeAuswahl.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(AufgabeAuswahl.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
