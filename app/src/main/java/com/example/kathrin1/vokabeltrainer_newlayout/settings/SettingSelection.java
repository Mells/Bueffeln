package com.example.kathrin1.vokabeltrainer_newlayout.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.Translation;

import static android.R.attr.id;

public class SettingSelection extends AppCompatActivity{

    private DatabaseManager dbManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_selection);

        Button btn_setting_wordtrain = (Button) findViewById(R.id.btn_setting_wordtrain);
        Button btn_setting_kontexttrain = (Button) findViewById(R.id.btn_setting_kontexttrain);
        Button btn_setting_reset = (Button) findViewById(R.id.btn_setting_reset);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btn_setting_wordtrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingSelection.this, SettingWordtest.class);
                startActivity(intent);
            }
        });

        btn_setting_kontexttrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingSelection.this, SettingKontexttest.class);
                startActivity(intent);
            }
        });

        btn_setting_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                dbManager = DatabaseManager.build(SettingSelection.this);
                                for (int i = 1; i <= 3024; i++) {
                                    dbManager.updateTested(0, i);
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                builder.setMessage("Bist Du sicher das Du all Deinen Fortschritt löschen möchtest?\n(Benötigt ein Paar Sekunden)").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
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
                Intent intent_help = new Intent(SettingSelection.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(SettingSelection.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(SettingSelection.this, SettingSelection.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
