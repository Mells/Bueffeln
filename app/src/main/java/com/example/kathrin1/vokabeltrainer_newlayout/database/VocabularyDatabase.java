package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class VocabularyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAMES = "vok8-map.db";
    private static final int DATABASE_VERSION = 1;

    public VocabularyDatabase(Context context) {
        super(context, DATABASE_NAMES, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

}
