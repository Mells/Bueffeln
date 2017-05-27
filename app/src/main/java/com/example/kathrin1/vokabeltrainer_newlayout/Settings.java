package com.example.kathrin1.vokabeltrainer_newlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Settings extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        TextView txt_explanation = (TextView) findViewById(R.id.txt_explanation);
        TextView txt_book_sent = (TextView) findViewById(R.id.txt_book_sent);
        TextView txt_gdex_sent = (TextView) findViewById(R.id.txt_gdex_sent);
        TextView txt_learner_sent = (TextView) findViewById(R.id.txt_learner_sent);

        TextView txt_explanation_translation  = (TextView) findViewById(R.id.txt_explanation_translation);
        TextView txt_explanation_kontext  = (TextView) findViewById(R.id.txt_explanation_kontext);
        TextView txt_explanation_kontext_hint  = (TextView) findViewById(R.id.txt_explanation_kontext_hint);

        txt_explanation.setText(fromHtml("Hello! Hier kannst du einstellen welche Art von Sätzen " +
                "du gern als Beispiele hättest"));
        txt_book_sent.setText(fromHtml("<b>Buch Sätze</b>: Dies sind Sätze die aus deinem " +
                "Englischbuch extrahiert wurden. Diese Sätze müsstest du verstehen wenn du deine " +
                "Vokabeln gut gelernt hast. Leider gibt es nicht zu jeder Vokabel ein Satz."));
        txt_gdex_sent.setText(fromHtml("<b>GDEX Sätze</b>: Diese Sätze kommen nicht in deinem " +
                "Englischbuch vor. Sie wurden aus einem Corpus extrahiert. Es sind gute " +
                "Beispielsätze aber es kann sein das du sie nicht verstehst."));
        txt_learner_sent.setText(fromHtml("<b>Schüler Sätze</b>: Diese Sätze kommen nicht in deinem " +
                "Englischbuch vor. Sie wurden aus einem Corpus extrahiert. Diese Sätze wuden so " +
                "ausgesucht das du sie hoffentlich verstehst."));

        txt_explanation_translation.setText(fromHtml("Bitte wähle was für einem Beispielsatz du für " +
                "Übersetzung haben möchtest und welche wenn nichts vorhanden ist."));
        txt_explanation_kontext.setText(fromHtml(""));
        txt_explanation_kontext_hint.setText(fromHtml(""));

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
                Intent intent_help = new Intent(Settings.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Settings.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
            case R.id.item_settings:
                Intent intent_setting = new Intent(Settings.this, Settings.class);
                startActivity(intent_setting);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
