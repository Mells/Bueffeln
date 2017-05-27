package com.example.kathrin1.vokabeltrainer_newlayout.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.Help;
import com.example.kathrin1.vokabeltrainer_newlayout.MainActivity;
import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.Settings;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.List;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class Status extends AppCompatActivity {

    private DatabaseManager dbManager = null;
    private List<VocObject> allVocabulary;
    private String[] book = {"I", "II", "III"};
    private String[] chapter = {"Welcome", "1", "2", "3", "4", "5", "6"};
    private String[] unit = {"A", "B", "C"};
    private Integer[] level ={0,1,2,3,4,5,6,7};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        TextView txt_status_answer = (TextView) findViewById(R.id.txt_status_answer);
        dbManager = DatabaseManager.build(Status.this);

        String information = "";
        for (String b : book) {
            information += "Du hast in <b>Buch " + b +"</b>:<br>";
            for (String c : chapter) {
                information += "&nbsp;&nbsp;&nbsp;&nbsp;in <b>Kapitel: " + c +"</b>:<br>";
                for (int l : level) {
                    information += "&nbsp;&nbsp;&nbsp;&nbsp;in <b>Level: " + l +"</b>:<br>";
                    int numberOfWords = (dbManager.getWordsByBookChapterLevel(b, c, l)).size();
                    information += "&nbsp;&nbsp;&nbsp;&nbsp;" + numberOfWords + " Vokabeln.<br>";
                }
            }
            information += "<br>";
        }

        txt_status_answer.setText(fromHtml(information));

    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
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
                Intent intent_help = new Intent(Status.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Status.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(Status.this, Settings.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
