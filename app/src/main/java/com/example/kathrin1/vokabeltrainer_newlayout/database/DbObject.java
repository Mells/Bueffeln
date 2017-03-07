package com.example.kathrin1.vokabeltrainer_newlayout.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbObject {

    private static VocabularyDatabase dbHelper;
    private SQLiteDatabase db;

    public DbObject(Context context) {
        dbHelper = new VocabularyDatabase(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDbConnection(){
        return this.db;
    }

    public void closeDbConnection(){
        if(this.db != null){
            this.db.close();
        }
    }

}

