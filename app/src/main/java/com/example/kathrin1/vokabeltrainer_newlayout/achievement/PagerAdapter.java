package com.example.kathrin1.vokabeltrainer_newlayout.achievement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
 
    @Override
    public Fragment getItem(int position) {
 
        switch (position) {
            case 0:
                AchievementGeneral gen = new AchievementGeneral();
                return gen;
            case 1:
                AchievementBook1 b1 = new AchievementBook1();
                return b1;
            case 2:
                AchievementBook2 b2 = new AchievementBook2();
                return b2;
            case 3:
                AchievementBook3 b3 = new AchievementBook3();
                return b3;
            default:
                return null;
        }
    }
 
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}