package com.example.kathrin1.vokabeltrainer_newlayout.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;


/**
 * Created by kathrin1 on 28.05.17.
 */

public class SettingWordtest extends AppCompatActivity{

    RadioButton rbtn_first_book;
    RadioButton rbtn_second_book;
    RadioButton rbtn_third_book;

    RadioButton rbtn_first_learner;
    RadioButton rbtn_second_learner;
    RadioButton rbtn_third_learner;

    RadioButton rbtn_first_gdex;
    RadioButton rbtn_second_gdex;
    RadioButton rbtn_third_gdex;

    RadioButton first_selected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_wordtest);

        //RadioGroup rbtn_first_group = (RadioGroup) findViewById(R.id.rbtn_1st_sent_1st_group);
        rbtn_first_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_book);
        rbtn_second_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_book);
        rbtn_third_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_book);

        //RadioGroup rbtn_second_group = (RadioGroup) findViewById(R.id.rbtn_1st_sent_2nd_group);
        rbtn_first_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_learner);
        rbtn_second_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_learner);
        rbtn_third_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_learner);

        //RadioGroup rbtn_third_group = (RadioGroup) findViewById(R.id.rbtn_1st_sent_3rd_group);
        rbtn_first_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_gdex);
        rbtn_second_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_gdex);
        rbtn_third_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_gdex);

    }

    public void onRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbtn_1st_sent_1st_book:
                rbtn_second_book.setEnabled(false);
                rbtn_third_book.setEnabled(false);
                rbtn_second_learner.setEnabled(true);
                rbtn_third_learner.setEnabled(true);
                rbtn_second_gdex.setEnabled(true);
                rbtn_third_gdex.setEnabled(true);
                first_selected = rbtn_first_book;
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_first", "book").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_book:
                rbtn_third_book.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_second", "book").commit();
                if (first_selected == rbtn_first_learner){
                    rbtn_third_gdex.setEnabled(true);
                    rbtn_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "gdex").commit();
                }
                else {
                    rbtn_third_learner.setEnabled(true);
                    rbtn_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "learner").commit();
                }
                break;

            case R.id.rbtn_1st_sent_1st_learner:
                rbtn_second_learner.setEnabled(false);
                rbtn_third_learner.setEnabled(false);
                rbtn_second_book.setEnabled(true);
                rbtn_third_book.setEnabled(true);
                rbtn_second_gdex.setEnabled(true);
                rbtn_third_gdex.setEnabled(true);
                first_selected = rbtn_first_learner;
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_first", "learner").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_learner:
                rbtn_third_learner.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_second", "learner").commit();
                if (first_selected == rbtn_first_book){
                    rbtn_third_gdex.setEnabled(true);
                    rbtn_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "gdex").commit();
                }
                else {
                    rbtn_third_book.setEnabled(true);
                    rbtn_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "book").commit();
                }
                break;

            case R.id.rbtn_1st_sent_1st_gdex:
                rbtn_second_gdex.setEnabled(false);
                rbtn_third_gdex.setEnabled(false);
                rbtn_second_learner.setEnabled(true);
                rbtn_third_learner.setEnabled(true);
                rbtn_second_book.setEnabled(true);
                rbtn_third_book.setEnabled(true);
                first_selected = rbtn_first_gdex;
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_first", "gdex").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_gdex:
                rbtn_third_gdex.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                        .putString("word_second", "gdex").commit();
                if (first_selected == rbtn_first_learner){
                    rbtn_third_book.setEnabled(true);
                    rbtn_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "book").commit();
                }
                else {
                    rbtn_third_learner.setEnabled(true);
                    rbtn_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingWordtest.this).edit()
                            .putString("word_third", "learner").commit();
                }
                break;
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
                Intent intent_help = new Intent(SettingWordtest.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(SettingWordtest.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(SettingWordtest.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
