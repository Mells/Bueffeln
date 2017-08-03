package com.example.kathrin1.vokabeltrainer_newlayout.achievement;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kathrin1.vokabeltrainer_newlayout.R;


/**
 * Created by kathrin1 on 09.07.17.
 */

public class AchievementMain extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_main);
        // ----------------TABS---------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Allgemein"));
        tabLayout.addTab(tabLayout.newTab().setText("Buch 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Buch 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Buch 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final com.example.kathrin1.vokabeltrainer_newlayout.achievement.PagerAdapter adapter = new com.example.kathrin1.vokabeltrainer_newlayout.achievement.PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(ExerciseUtils.setCurrentBook(AchievementMain.this));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Log.d("TAB", String.valueOf(tab.getText()));
                //ExerciseUtils.updateBook(AchievementMain.this, tab);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
