package com.example.kathrin1.vokabeltrainer_newlayout.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;

/**
 * Created by kathrin1 on 28.05.17.
 */

public class SettingKontexttest extends AppCompatActivity {

    RadioButton rbtn_first_first_book;
    RadioButton rbtn_first_second_book;
    RadioButton rbtn_first_third_book;

    RadioButton rbtn_first_first_learner;
    RadioButton rbtn_first_second_learner;
    RadioButton rbtn_first_third_learner;

    RadioButton rbtn_first_first_gdex;
    RadioButton rbtn_first_second_gdex;
    RadioButton rbtn_first_third_gdex;

    RadioButton rbtn_second_first_book;
    RadioButton rbtn_second_second_book;
    RadioButton rbtn_second_third_book;

    RadioButton rbtn_second_first_learner;
    RadioButton rbtn_second_second_learner;
    RadioButton rbtn_second_third_learner;

    RadioButton rbtn_second_first_gdex;
    RadioButton rbtn_second_second_gdex;
    RadioButton rbtn_second_third_gdex;

    RadioButton rbtn_third_first_book;
    RadioButton rbtn_third_second_book;
    RadioButton rbtn_third_third_book;

    RadioButton rbtn_third_first_learner;
    RadioButton rbtn_third_second_learner;
    RadioButton rbtn_third_third_learner;

    RadioButton rbtn_third_first_gdex;
    RadioButton rbtn_third_second_gdex;
    RadioButton rbtn_third_third_gdex;

    RadioButton rbtn_hint_first_book;
    RadioButton rbtn_hint_second_book;
    RadioButton rbtn_hint_third_book;

    RadioButton rbtn_hint_first_learner;
    RadioButton rbtn_hint_second_learner;
    RadioButton rbtn_hint_third_learner;

    RadioButton rbtn_hint_first_gdex;
    RadioButton rbtn_hint_second_gdex;
    RadioButton rbtn_hint_third_gdex;


    RadioButton first_first_selected;
    RadioButton first_second_selected;
    RadioButton first_third_selected;
    RadioButton first_hint_selected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_kontexttest);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Button btn_go_back = (Button) findViewById(R.id.btn_go_back);

        rbtn_first_first_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_book);
        rbtn_first_second_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_book);
        rbtn_first_third_book = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_book);
        rbtn_first_first_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_learner);
        rbtn_first_second_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_learner);
        rbtn_first_third_learner = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_learner);
        rbtn_first_first_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_1st_gdex);
        rbtn_first_second_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_2nd_gdex);
        rbtn_first_third_gdex = (RadioButton) findViewById(R.id.rbtn_1st_sent_3rd_gdex);

        rbtn_second_first_book = (RadioButton) findViewById(R.id.rbtn_2nd_sent_1st_book);
        rbtn_second_second_book = (RadioButton) findViewById(R.id.rbtn_2nd_sent_2nd_book);
        rbtn_second_third_book = (RadioButton) findViewById(R.id.rbtn_2nd_sent_3rd_book);
        rbtn_second_first_learner = (RadioButton) findViewById(R.id.rbtn_2nd_sent_1st_learner);
        rbtn_second_second_learner = (RadioButton) findViewById(R.id.rbtn_2nd_sent_2nd_learner);
        rbtn_second_third_learner = (RadioButton) findViewById(R.id.rbtn_2nd_sent_3rd_learner);
        rbtn_second_first_gdex = (RadioButton) findViewById(R.id.rbtn_2nd_sent_1st_gdex);
        rbtn_second_second_gdex = (RadioButton) findViewById(R.id.rbtn_2nd_sent_2nd_gdex);
        rbtn_second_third_gdex = (RadioButton) findViewById(R.id.rbtn_2nd_sent_3rd_gdex);

        rbtn_third_first_book = (RadioButton) findViewById(R.id.rbtn_3rd_sent_1st_book);
        rbtn_third_second_book = (RadioButton) findViewById(R.id.rbtn_3rd_sent_2nd_book);
        rbtn_third_third_book = (RadioButton) findViewById(R.id.rbtn_3rd_sent_3rd_book);
        rbtn_third_first_learner = (RadioButton) findViewById(R.id.rbtn_3rd_sent_1st_learner);
        rbtn_third_second_learner = (RadioButton) findViewById(R.id.rbtn_3rd_sent_2nd_learner);
        rbtn_third_third_learner = (RadioButton) findViewById(R.id.rbtn_3rd_sent_3rd_learner);
        rbtn_third_first_gdex = (RadioButton) findViewById(R.id.rbtn_3rd_sent_1st_gdex);
        rbtn_third_second_gdex = (RadioButton) findViewById(R.id.rbtn_3rd_sent_2nd_gdex);
        rbtn_third_third_gdex = (RadioButton) findViewById(R.id.rbtn_3rd_sent_3rd_gdex);

        rbtn_hint_first_book = (RadioButton) findViewById(R.id.rbtn_hint_sent_1st_book);
        rbtn_hint_second_book = (RadioButton) findViewById(R.id.rbtn_hint_sent_2nd_book);
        rbtn_hint_third_book = (RadioButton) findViewById(R.id.rbtn_hint_sent_3rd_book);
        rbtn_hint_first_learner = (RadioButton) findViewById(R.id.rbtn_hint_sent_1st_learner);
        rbtn_hint_second_learner = (RadioButton) findViewById(R.id.rbtn_hint_sent_2nd_learner);
        rbtn_hint_third_learner = (RadioButton) findViewById(R.id.rbtn_hint_sent_3rd_learner);
        rbtn_hint_first_gdex = (RadioButton) findViewById(R.id.rbtn_hint_sent_1st_gdex);
        rbtn_hint_second_gdex = (RadioButton) findViewById(R.id.rbtn_hint_sent_2nd_gdex);
        rbtn_hint_third_gdex = (RadioButton) findViewById(R.id.rbtn_hint_sent_3rd_gdex);

        rbtn_first_first_book.setChecked(sharedPreferences.getBoolean("rbtn_first_first_book", false));
        rbtn_first_second_book.setChecked(sharedPreferences.getBoolean("rbtn_first_second_book", false));
        rbtn_first_third_book.setChecked(sharedPreferences.getBoolean("rbtn_first_second_book", false));
        rbtn_first_first_learner.setChecked(sharedPreferences.getBoolean("rbtn_first_first_learner", false));
        rbtn_first_second_learner.setChecked(sharedPreferences.getBoolean("rbtn_first_second_learner", false));
        rbtn_first_third_learner.setChecked(sharedPreferences.getBoolean("rbtn_first_second_learner", false));
        rbtn_first_first_gdex.setChecked(sharedPreferences.getBoolean("rbtn_first_first_gdex", false));
        rbtn_first_second_gdex.setChecked(sharedPreferences.getBoolean("rbtn_first_second_gdex", false));
        rbtn_first_third_gdex.setChecked(sharedPreferences.getBoolean("rbtn_first_second_gdex", false));

        rbtn_second_first_book.setChecked(sharedPreferences.getBoolean("rbtn_second_first_book", false));
        rbtn_second_second_book.setChecked(sharedPreferences.getBoolean("rbtn_second_second_book", false));
        rbtn_second_third_book.setChecked(sharedPreferences.getBoolean("rbtn_second_second_book", false));
        rbtn_second_first_learner.setChecked(sharedPreferences.getBoolean("rbtn_second_first_learner", false));
        rbtn_second_second_learner.setChecked(sharedPreferences.getBoolean("rbtn_second_second_learner", false));
        rbtn_second_third_learner.setChecked(sharedPreferences.getBoolean("rbtn_second_second_learner", false));
        rbtn_second_first_gdex.setChecked(sharedPreferences.getBoolean("rbtn_second_first_gdex", false));
        rbtn_second_second_gdex.setChecked(sharedPreferences.getBoolean("rbtn_second_second_gdex", false));
        rbtn_second_third_gdex.setChecked(sharedPreferences.getBoolean("rbtn_second_second_gdex", false));

        rbtn_third_first_book.setChecked(sharedPreferences.getBoolean("rbtn_third_first_book", false));
        rbtn_third_second_book.setChecked(sharedPreferences.getBoolean("rbtn_third_second_book", false));
        rbtn_third_third_book.setChecked(sharedPreferences.getBoolean("rbtn_third_second_book", false));
        rbtn_third_first_learner.setChecked(sharedPreferences.getBoolean("rbtn_third_first_learner", false));
        rbtn_third_second_learner.setChecked(sharedPreferences.getBoolean("rbtn_third_second_learner", false));
        rbtn_third_third_learner.setChecked(sharedPreferences.getBoolean("rbtn_third_second_learner", false));
        rbtn_third_first_gdex.setChecked(sharedPreferences.getBoolean("rbtn_third_first_gdex", false));
        rbtn_third_second_gdex.setChecked(sharedPreferences.getBoolean("rbtn_third_second_gdex", false));
        rbtn_third_third_gdex.setChecked(sharedPreferences.getBoolean("rbtn_third_second_gdex", false));

        rbtn_hint_first_book.setChecked(sharedPreferences.getBoolean("rbtn_hint_first_book", false));
        rbtn_hint_second_book.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_book", false));
        rbtn_hint_third_book.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_book", false));
        rbtn_hint_first_learner.setChecked(sharedPreferences.getBoolean("rbtn_hint_first_learner", false));
        rbtn_hint_second_learner.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_learner", false));
        rbtn_hint_third_learner.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_learner", false));
        rbtn_hint_first_gdex.setChecked(sharedPreferences.getBoolean("rbtn_hint_first_gdex", false));
        rbtn_hint_second_gdex.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_gdex", false));
        rbtn_hint_third_gdex.setChecked(sharedPreferences.getBoolean("rbtn_hint_second_gdex", false));

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(SettingKontexttest.this);
            }
        });
    }

    public void onFirstRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbtn_1st_sent_1st_book:
                rbtn_first_second_book.setEnabled(false);
                rbtn_first_third_book.setEnabled(false);
                rbtn_first_second_learner.setEnabled(true);
                rbtn_first_third_learner.setEnabled(true);
                rbtn_first_second_gdex.setEnabled(true);
                rbtn_first_third_gdex.setEnabled(true);
                first_first_selected = rbtn_first_first_book;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_first", "book").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_book:
                rbtn_first_third_book.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_second", "book").commit();
                if (first_first_selected == rbtn_first_first_learner){
                    rbtn_first_third_gdex.setEnabled(true);
                    rbtn_first_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "gdex").commit();
                }
                else {
                    rbtn_first_third_learner.setEnabled(true);
                    rbtn_first_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "learner").commit();
                }
                break;

            case R.id.rbtn_1st_sent_1st_learner:
                rbtn_first_second_learner.setEnabled(false);
                rbtn_first_third_learner.setEnabled(false);
                rbtn_first_second_book.setEnabled(true);
                rbtn_first_third_book.setEnabled(true);
                rbtn_first_second_gdex.setEnabled(true);
                rbtn_first_third_gdex.setEnabled(true);
                first_first_selected = rbtn_first_first_learner;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_first", "learner").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_learner:
                rbtn_first_third_learner.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_second", "learner").commit();
                if (first_first_selected == rbtn_first_first_book){
                    rbtn_first_third_gdex.setEnabled(true);
                    rbtn_first_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "gdex").commit();
                }
                else {
                    rbtn_first_third_book.setEnabled(true);
                    rbtn_first_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "book").commit();
                }
                break;

            case R.id.rbtn_1st_sent_1st_gdex:
                rbtn_first_second_gdex.setEnabled(false);
                rbtn_first_third_gdex.setEnabled(false);
                rbtn_first_second_learner.setEnabled(true);
                rbtn_first_third_learner.setEnabled(true);
                rbtn_first_second_book.setEnabled(true);
                rbtn_first_third_book.setEnabled(true);
                first_first_selected = rbtn_first_first_gdex;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_first", "gdex").commit();
                break;
            case R.id.rbtn_1st_sent_2nd_gdex:
                rbtn_first_third_gdex.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_first_second", "gdex").commit();
                if (first_first_selected == rbtn_first_first_learner){
                    rbtn_first_third_book.setEnabled(true);
                    rbtn_first_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "book").commit();
                }
                else {
                    rbtn_first_third_learner.setEnabled(true);
                    rbtn_first_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_first_third", "learner").commit();
                }
                break;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putBoolean("rbtn_first_first_book", rbtn_first_first_book.isChecked());
        shEditor.putBoolean("rbtn_first_second_book", rbtn_first_second_book.isChecked());
        shEditor.putBoolean("rbtn_first_third_book", rbtn_first_third_book.isChecked());
        shEditor.putBoolean("rbtn_first_first_learner", rbtn_first_first_learner.isChecked());
        shEditor.putBoolean("rbtn_first_second_learner", rbtn_first_second_learner.isChecked());
        shEditor.putBoolean("rbtn_first_third_learner", rbtn_first_third_learner.isChecked());
        shEditor.putBoolean("rbtn_first_first_gdex", rbtn_first_first_gdex.isChecked());
        shEditor.putBoolean("rbtn_first_second_gdex", rbtn_first_second_gdex.isChecked());
        shEditor.putBoolean("rbtn_first_third_gdex", rbtn_first_third_gdex.isChecked());
        shEditor.commit();
    }

    public void onSecondRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbtn_2nd_sent_1st_book:
                rbtn_second_second_book.setEnabled(false);
                rbtn_second_third_book.setEnabled(false);
                rbtn_second_second_learner.setEnabled(true);
                rbtn_second_third_learner.setEnabled(true);
                rbtn_second_second_gdex.setEnabled(true);
                rbtn_second_third_gdex.setEnabled(true);
                first_second_selected = rbtn_second_first_book;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_first", "book").commit();
                break;
            case R.id.rbtn_2nd_sent_2nd_book:
                rbtn_second_third_book.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_second", "book").commit();
                if (first_second_selected == rbtn_second_first_learner){
                    rbtn_second_third_gdex.setEnabled(true);
                    rbtn_second_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "gdex").commit();
                }
                else {
                    rbtn_second_third_learner.setEnabled(true);
                    rbtn_second_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "learner").commit();
                }
                break;

            case R.id.rbtn_2nd_sent_1st_learner:
                rbtn_second_second_learner.setEnabled(false);
                rbtn_second_third_learner.setEnabled(false);
                rbtn_second_second_book.setEnabled(true);
                rbtn_second_third_book.setEnabled(true);
                rbtn_second_second_gdex.setEnabled(true);
                rbtn_second_third_gdex.setEnabled(true);
                first_second_selected = rbtn_second_first_learner;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_first", "learner").commit();
                break;
            case R.id.rbtn_2nd_sent_2nd_learner:
                rbtn_second_third_learner.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_second", "learner").commit();
                if (first_second_selected == rbtn_second_first_book){
                    rbtn_second_third_gdex.setEnabled(true);
                    rbtn_second_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "gdex").commit();
                }
                else {
                    rbtn_second_third_book.setEnabled(true);
                    rbtn_second_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "book").commit();
                }
                break;

            case R.id.rbtn_2nd_sent_1st_gdex:
                rbtn_second_second_gdex.setEnabled(false);
                rbtn_second_third_gdex.setEnabled(false);
                rbtn_second_second_learner.setEnabled(true);
                rbtn_second_third_learner.setEnabled(true);
                rbtn_second_second_book.setEnabled(true);
                rbtn_second_third_book.setEnabled(true);
                first_second_selected = rbtn_second_first_gdex;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_first", "gdex").commit();
                break;
            case R.id.rbtn_2nd_sent_2nd_gdex:
                rbtn_second_third_gdex.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_second_second", "gdex").commit();
                if (first_second_selected == rbtn_second_first_learner){
                    rbtn_second_third_book.setEnabled(true);
                    rbtn_second_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "book").commit();
                }
                else {
                    rbtn_second_third_learner.setEnabled(true);
                    rbtn_second_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_second_third", "learner").commit();
                }
                break;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putBoolean("rbtn_second_first_book", rbtn_second_first_book.isChecked());
        shEditor.putBoolean("rbtn_second_second_book", rbtn_second_second_book.isChecked());
        shEditor.putBoolean("rbtn_second_third_book", rbtn_second_third_book.isChecked());
        shEditor.putBoolean("rbtn_second_first_learner", rbtn_second_first_learner.isChecked());
        shEditor.putBoolean("rbtn_second_second_learner", rbtn_second_second_learner.isChecked());
        shEditor.putBoolean("rbtn_second_third_learner", rbtn_second_third_learner.isChecked());
        shEditor.putBoolean("rbtn_second_first_gdex", rbtn_second_first_gdex.isChecked());
        shEditor.putBoolean("rbtn_second_second_gdex", rbtn_second_second_gdex.isChecked());
        shEditor.putBoolean("rbtn_second_third_gdex", rbtn_second_third_gdex.isChecked());
        shEditor.commit();
    }

    public void onThirdRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbtn_3rd_sent_1st_book:
                rbtn_third_second_book.setEnabled(false);
                rbtn_third_third_book.setEnabled(false);
                rbtn_third_second_learner.setEnabled(true);
                rbtn_third_third_learner.setEnabled(true);
                rbtn_third_second_gdex.setEnabled(true);
                rbtn_third_third_gdex.setEnabled(true);
                first_third_selected = rbtn_third_first_book;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_first", "book").commit();
                break;
            case R.id.rbtn_3rd_sent_2nd_book:
                rbtn_third_third_book.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_second", "book").commit();
                if (first_third_selected == rbtn_third_first_learner){
                    rbtn_third_third_gdex.setEnabled(true);
                    rbtn_third_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "gdex").commit();
                }
                else {
                    rbtn_third_third_learner.setEnabled(true);
                    rbtn_third_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "learner").commit();
                }
                break;

            case R.id.rbtn_3rd_sent_1st_learner:
                rbtn_third_second_learner.setEnabled(false);
                rbtn_third_third_learner.setEnabled(false);
                rbtn_third_second_book.setEnabled(true);
                rbtn_third_third_book.setEnabled(true);
                rbtn_third_second_gdex.setEnabled(true);
                rbtn_third_third_gdex.setEnabled(true);
                first_third_selected = rbtn_third_first_learner;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_first", "learner").commit();
                break;
            case R.id.rbtn_3rd_sent_2nd_learner:
                rbtn_third_third_learner.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_second", "learner").commit();
                if (first_third_selected == rbtn_third_first_book){
                    rbtn_third_third_gdex.setEnabled(true);
                    rbtn_third_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "gdex").commit();
                }
                else {
                    rbtn_third_third_book.setEnabled(true);
                    rbtn_third_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "book").commit();
                }
                break;

            case R.id.rbtn_3rd_sent_1st_gdex:
                rbtn_third_second_gdex.setEnabled(false);
                rbtn_third_third_gdex.setEnabled(false);
                rbtn_third_second_learner.setEnabled(true);
                rbtn_third_third_learner.setEnabled(true);
                rbtn_third_second_book.setEnabled(true);
                rbtn_third_third_book.setEnabled(true);
                first_third_selected = rbtn_third_first_gdex;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_first", "gdex").commit();
                break;
            case R.id.rbtn_3rd_sent_2nd_gdex:
                rbtn_third_third_gdex.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_third_second", "gdex").commit();
                if (first_third_selected == rbtn_third_first_learner){
                    rbtn_third_third_book.setEnabled(true);
                    rbtn_third_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "book").commit();
                }
                else {
                    rbtn_third_third_learner.setEnabled(true);
                    rbtn_third_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_third_third", "learner").commit();
                }
                break;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putBoolean("rbtn_third_first_book", rbtn_third_first_book.isChecked());
        shEditor.putBoolean("rbtn_third_second_book", rbtn_third_second_book.isChecked());
        shEditor.putBoolean("rbtn_third_third_book", rbtn_third_third_book.isChecked());
        shEditor.putBoolean("rbtn_third_first_learner", rbtn_third_first_learner.isChecked());
        shEditor.putBoolean("rbtn_third_second_learner", rbtn_third_second_learner.isChecked());
        shEditor.putBoolean("rbtn_third_third_learner", rbtn_third_third_learner.isChecked());
        shEditor.putBoolean("rbtn_third_first_gdex", rbtn_third_first_gdex.isChecked());
        shEditor.putBoolean("rbtn_third_second_gdex", rbtn_third_second_gdex.isChecked());
        shEditor.putBoolean("rbtn_third_third_gdex", rbtn_third_third_gdex.isChecked());
        shEditor.commit();
    }

    public void onHintRadioButtonClicked(View view) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbtn_hint_sent_1st_book:
                rbtn_hint_second_book.setEnabled(false);
                rbtn_hint_third_book.setEnabled(false);
                rbtn_hint_second_learner.setEnabled(true);
                rbtn_hint_third_learner.setEnabled(true);
                rbtn_hint_second_gdex.setEnabled(true);
                rbtn_hint_third_gdex.setEnabled(true);
                first_hint_selected = rbtn_hint_first_book;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_first", "book").commit();
                break;
            case R.id.rbtn_hint_sent_2nd_book:
                rbtn_hint_third_book.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_second", "book").commit();
                if (first_hint_selected == rbtn_hint_first_learner){
                    rbtn_hint_third_gdex.setEnabled(true);
                    rbtn_hint_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "gdex").commit();
                }
                else {
                    rbtn_hint_third_learner.setEnabled(true);
                    rbtn_hint_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "learner").commit();
                }
                break;

            case R.id.rbtn_hint_sent_1st_learner:
                rbtn_hint_second_learner.setEnabled(false);
                rbtn_hint_third_learner.setEnabled(false);
                rbtn_hint_second_book.setEnabled(true);
                rbtn_hint_third_book.setEnabled(true);
                rbtn_hint_second_gdex.setEnabled(true);
                rbtn_hint_third_gdex.setEnabled(true);
                first_hint_selected = rbtn_hint_first_learner;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_first", "learner").commit();
                break;
            case R.id.rbtn_hint_sent_2nd_learner:
                rbtn_hint_third_learner.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_second", "learner").commit();
                if (first_hint_selected == rbtn_hint_first_book){
                    rbtn_hint_third_gdex.setEnabled(true);
                    rbtn_hint_third_gdex.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "gdex").commit();
                }
                else {
                    rbtn_hint_third_book.setEnabled(true);
                    rbtn_hint_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "book").commit();
                }
                break;

            case R.id.rbtn_hint_sent_1st_gdex:
                rbtn_hint_second_gdex.setEnabled(false);
                rbtn_hint_third_gdex.setEnabled(false);
                rbtn_hint_second_learner.setEnabled(true);
                rbtn_hint_third_learner.setEnabled(true);
                rbtn_hint_second_book.setEnabled(true);
                rbtn_hint_third_book.setEnabled(true);
                first_hint_selected = rbtn_hint_first_gdex;
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_first", "gdex").commit();
                break;
            case R.id.rbtn_hint_sent_2nd_gdex:
                rbtn_hint_third_gdex.setEnabled(false);
                PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                        .putString("kontext_hint_second", "gdex").commit();
                if (first_hint_selected == rbtn_hint_first_learner){
                    rbtn_hint_third_book.setEnabled(true);
                    rbtn_hint_third_book.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "book").commit();
                }
                else {
                    rbtn_hint_third_learner.setEnabled(true);
                    rbtn_hint_third_learner.setChecked(true);
                    PreferenceManager.getDefaultSharedPreferences(SettingKontexttest.this).edit()
                            .putString("kontext_hint_third", "learner").commit();
                }
                break;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putBoolean("rbtn_hint_first_book", rbtn_hint_first_book.isChecked());
        shEditor.putBoolean("rbtn_hint_second_book", rbtn_hint_second_book.isChecked());
        shEditor.putBoolean("rbtn_hint_third_book", rbtn_hint_third_book.isChecked());
        shEditor.putBoolean("rbtn_hint_first_learner", rbtn_hint_first_learner.isChecked());
        shEditor.putBoolean("rbtn_hint_second_learner", rbtn_hint_second_learner.isChecked());
        shEditor.putBoolean("rbtn_hint_third_learner", rbtn_hint_third_learner.isChecked());
        shEditor.putBoolean("rbtn_hint_first_gdex", rbtn_hint_first_gdex.isChecked());
        shEditor.putBoolean("rbtn_hint_second_gdex", rbtn_hint_second_gdex.isChecked());
        shEditor.putBoolean("rbtn_hint_third_gdex", rbtn_hint_third_gdex.isChecked());
        shEditor.commit();
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
//                Intent intent_help = new Intent(SettingKontexttest.this, Help.class);
//                startActivity(intent_help);
//                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(SettingKontexttest.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(SettingKontexttest.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
