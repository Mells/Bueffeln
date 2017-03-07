package com.example.kathrin1.vokabeltrainer_newlayout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by kathrin1 on 01.03.17.
 */

public class Help extends AppCompatActivity{
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
                Intent intent_help = new Intent(Help.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(Help.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
