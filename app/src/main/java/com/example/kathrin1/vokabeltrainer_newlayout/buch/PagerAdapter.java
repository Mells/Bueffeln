package com.example.kathrin1.vokabeltrainer_newlayout.buch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wunderlist.slidinglayer.SlidingLayer;


 
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    SlidingLayer mSlide;
 
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
 
    @Override
    public Fragment getItem(int position) {
 
        switch (position) {
            case 0:
                Book1 tab1 = new Book1();
                return tab1;
            case 1:
                Book2 tab2 = new Book2();
                return tab2;
            case 2:
                Book3 tab3 = new Book3();
                return tab3;
            default:
                return null;
        }
    }
 
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}