package com.example.kathrin1.vokabeltrainer_newlayout.achievement;

/**
 * Created by kathrin1 on 08.07.17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kathrin1.vokabeltrainer_newlayout.R;


public class AchievementBook3 extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.achievement_general, container, false);

        return view;
    }

}
