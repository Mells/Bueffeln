package com.example.kathrin1.vokabeltrainer_newlayout.buch;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.dictionary.Lektion;
import com.wunderlist.slidinglayer.SlidingLayer;

import net.qiujuer.genius.ui.widget.SeekBar;


public class Book3 extends Fragment {

    private SlidingLayer mSlidingLayer;
    private Lektion parent;
    private View view;
    private SeekBar mBar;
    private AppCompatActivity a;

    private Button btn_chap0;
    private Button btn_chap1;
    private Button btn_chap2;
    private Button btn_chap3;
    private Button btn_chap4;
    private Button btn_chap5;
    private Button btn_chap6;

    private Button now = null;
    private Button previous = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.book_book, container, false);

        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_chap0 = (Button) view.findViewById(R.id.btn_chap0);
        btn_chap0.setVisibility(view.INVISIBLE);
        btn_chap1 = (Button) view.findViewById(R.id.btn_chap1);
        btn_chap2 = (Button) view.findViewById(R.id.btn_chap2);
        btn_chap3 = (Button) view.findViewById(R.id.btn_chap3);
        btn_chap4 = (Button) view.findViewById(R.id.btn_chap4);
        btn_chap5 = (Button) view.findViewById(R.id.btn_chap5);
        btn_chap6 = (Button) view.findViewById(R.id.btn_chap6);

        final CheckBox cb_A = (CheckBox) view.findViewById(R.id.cb_A);
        final CheckBox cb_B = (CheckBox) view.findViewById(R.id.cb_B);
        final CheckBox cb_C = (CheckBox) view.findViewById(R.id.cb_C);

        final SeekBar seek = (SeekBar) view.findViewById(R.id.seek_level);

        btn_chap1.setText("On the move");
        btn_chap2.setText("Welcome to Wales!");
        btn_chap3.setText("Famous Brits");
        btn_chap4.setText("Keep me posted");
        btn_chap5.setText("The German exchange");
        btn_chap6.setText("The Great Outdoors");

        if (seek != null) {
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Toast.makeText(getActivity(), (String.format("Min:%s, Max:%s, Value:%s", mBar.getMin(), mBar.getMax(), mBar.getProgress())), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Toast.makeText(getActivity(), (String.format("Min:%s, Max:%s, Value:%s", mBar.getMin(), mBar.getMax(), mBar.getProgress())), Toast.LENGTH_LONG).show();
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putInt("unit", seek.getProgress()).commit();
                }
            });
        }

        int pref_level = PreferenceManager.getDefaultSharedPreferences(a)
                .getInt("unit", 0);
        seek.setProgress(pref_level);

//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mSlidingLayer.closeLayer(true);
//            }
//        });

//        btn_chap0.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                now = btn_chap0;
//                now.setBackgroundResource(R.drawable.ic_button_rot);
//                if(previous != null && previous != now){
//                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
//                    previous = now;
//                }
//                else{
//                    previous = btn_chap0;
//                }
//                PreferenceManager.getDefaultSharedPreferences(a).edit()
//                        .putString("chapter", "Welcome").commit();
//            }
//        });

        btn_chap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap1;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap1;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "1").commit();
            }
        });

        btn_chap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap2;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap2;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "2").commit();
            }
        });

        btn_chap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap3;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap3;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "3").commit();
            }
        });

        btn_chap4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap4;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap4;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "4").commit();
            }
        });

        btn_chap5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap5;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap5;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "5").commit();
            }
        });

        btn_chap6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = btn_chap6;
                now.setBackgroundResource(R.drawable.ic_button_rot);
                if(previous != null && previous != now){
                    previous.setBackgroundResource(R.drawable.ic_button_exercise);
                    previous = now;
                }
                else{
                    previous = btn_chap6;
                }
                PreferenceManager.getDefaultSharedPreferences(a).edit()
                        .putString("chapter", "6").commit();
            }
        });

        String pref_chapter = PreferenceManager.getDefaultSharedPreferences(a)
                .getString("chapter", "0");

        now = stringToButton(pref_chapter);
        now.setBackgroundResource(R.drawable.ic_button_rot);
        if(previous != null && previous != now) {
            previous.setBackgroundResource(R.drawable.ic_button_exercise);
            previous = now;
        }
        else {
            previous = now;
        }

        cb_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_A.isChecked()){
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_A", true).commit();
                }else {
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_A", false).commit();
                }
            }
        });

        cb_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_B.isChecked()){
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_B", true).commit();
                }else {
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_B", false).commit();
                }
            }
        });

        cb_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_C.isChecked()){
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_C", true).commit();
                }else {
                    PreferenceManager.getDefaultSharedPreferences(a).edit()
                            .putBoolean("unit_C", false).commit();
                }
            }
        });

        Boolean checked = PreferenceManager.getDefaultSharedPreferences(a)
                .getBoolean("unit_A", true);
        cb_A.setChecked(checked);

        checked = PreferenceManager.getDefaultSharedPreferences(a)
                .getBoolean("unit_B", false);
        cb_B.setChecked(checked);

        checked = PreferenceManager.getDefaultSharedPreferences(a)
                .getBoolean("unit_C", false);
        cb_C.setChecked(checked);

        return view;
    }

    private Button stringToButton(String s_btn){
        switch (s_btn){
            case "1": return btn_chap1;
            case "2": return btn_chap2;
            case "3": return btn_chap3;
            case "4": return btn_chap4;
            case "5": return btn_chap5;
            case "6": return btn_chap6;
        }
        return btn_chap1;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //AppCompatActivity a;
        if (context instanceof AppCompatActivity) {
            a = (AppCompatActivity) context;
        }
    }
}