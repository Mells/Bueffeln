package com.example.kathrin1.vokabeltrainer_newlayout.achievement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kathrin1.vokabeltrainer_newlayout.R;


/**
 * Created by kathrin1 on 08.07.17.
 */

public class AchievementGeneral extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.achievement_general, container, false);

        return view;
    }

}
