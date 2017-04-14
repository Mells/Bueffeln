package com.example.kathrin1.vokabeltrainer_newlayout;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.kathrin1.vokabeltrainer_newlayout.buch.PagerAdapter;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.Lektion;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.VocabularyDictionary;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.AufgabeAuswahl;
import com.example.kathrin1.vokabeltrainer_newlayout.exercise.ExerciseUtils;
import com.wunderlist.slidinglayer.SlidingLayer;

public class MainActivity extends AppCompatActivity {

    private SlidingLayer mSlidingLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------TABS---------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Book 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Book 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(setCurrentBook());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ExerciseUtils.updateBook(MainActivity.this, tab);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // --------------------------------------------

        // ----------------SLIDER---------------------

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        // --------------------------------------------

        Button btn_dictionary = (Button) findViewById(R.id.btn_dict);
        Button btn_lektion = (Button) findViewById(R.id.btn_lektion);
        Button btn_aufgabe = (Button) findViewById(R.id.btn_aufgabe);
        Button btn_status = (Button) findViewById(R.id.btn_status);
        Button btn_auswahl = (Button) findViewById(R.id.btn_auswahl);

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
                Intent intent = new Intent(MainActivity.this, AufgabeAuswahl.class);
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

        btn_auswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        mSlidingLayer.openLayer(true);
            }
        });
    }

    private int setCurrentBook() {
        int tab = 0;
        String pref_book = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("book", "I");
        switch (pref_book){
            case "I": tab = 0;
                break;
            case "II": tab = 1;
                break;
            case "III": tab = 2;
                break;
        }
        return tab;
    }

    public SlidingLayer getSlidingLayer(){
        return mSlidingLayer;
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
                Intent intent_help = new Intent(MainActivity.this, Help.class);
                startActivity(intent_help);
                return (true);
            case R.id.item_home:
                Intent intent_home = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent_home);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
