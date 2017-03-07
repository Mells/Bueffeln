package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;

public class DatabaseQuery extends DbObject{

    private SQLiteDatabase db;


    public DatabaseQuery(Context context) {
        super(context);
        db = getDbConnection();
    }

    public String[] dictionaryEnglishWords() {
        //String query = "Select Vokabel, id from Vokabelliste";
        String query = "Select Vokabel, _id from test";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        ArrayList<String> wordTerms = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndexOrThrow("Vokabel"));
                wordTerms.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();

        String[] dictionaryWords = new String[wordTerms.size()];
        dictionaryWords = wordTerms.toArray(dictionaryWords);
        return dictionaryWords;
    }

    public String[] dictionaryGermanWords(){
        ArrayList<String> wordTerms = new ArrayList<String>();
        //String query = "Select Vokabel, id from Vokabelliste";
        //String query = "Select Vokabel, id from vocabulary";
        //Cursor cursor = this.getDbConnection().rawQuery(query, null);
        //query = "Select * from Vokabelliste";
        String query = "Select * from test";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String word = cursor.getString(cursor.getColumnIndexOrThrow("Uebersetzung"));
                wordTerms.add(word);
            }while(cursor.moveToNext());
        }
        cursor.close();

        String[] dictionaryWords = new String[wordTerms.size()];
        dictionaryWords = wordTerms.toArray(dictionaryWords);
        return dictionaryWords;
    }

    public SentObject getSentence(int id){
        SentObject sentObject = null;

        String query = "Select * from sentences where _id = " + id;
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                //String id = cursor.getString(cursor.getColumnIndexOrThrow("Id"));
                String book = cursor.getString(cursor.getColumnIndexOrThrow("Book"));
                String chapter = cursor.getString(cursor.getColumnIndexOrThrow("Chapter"));
                String sentence = cursor.getString(cursor.getColumnIndexOrThrow("Sentence"));
                String tagged = cursor.getString(cursor.getColumnIndexOrThrow("Tagged"));
                String lemma = cursor.getString(cursor.getColumnIndexOrThrow("Lemma"));
                sentObject = new SentObject(id, book, chapter, sentence, tagged, lemma);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return sentObject;
    }

    public VocObject getWordPairById(int id){

        VocObject vocObject = null;
        //String query = "select * from Vokabelliste where id = " + id;
        String query = "select * from test where _id = " + id;
        Log.d("QUERY", query);
        Cursor cursor = this.getDbConnection().rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                String vocabulary = cursor.getString(cursor.getColumnIndexOrThrow("Vokabel"));
                String lemma = cursor.getString(cursor.getColumnIndexOrThrow("VocLemma"));
                String translation = cursor.getString(cursor.getColumnIndexOrThrow("Uebersetzung"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
                String book = cursor.getString(cursor.getColumnIndexOrThrow("Band"));
                String chapter = cursor.getString(cursor.getColumnIndexOrThrow("Fundstelle"));
                String pos = cursor.getString(cursor.getColumnIndexOrThrow("Wortart"));
                String sentences = cursor.getString(cursor.getColumnIndexOrThrow("SentId"));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow("Tested"));

                vocObject = new VocObject(id, vocabulary, lemma, translation, status, book, chapter, pos, sentences, level);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return vocObject;
    }

    public ArrayList getWordsByBookChapterLevel(String book, String chapter, String unit, int level){
        ArrayList<VocObject> allVocabulary = new ArrayList<VocObject>();
        VocObject vocObject;
        if (!chapter.equals("Welcome")){
            String[] units = unit.split(" ");
            String chapterUnit = "";
            for (String u : units){
                chapterUnit += chapter + "/" + u + " ";
            }
            chapterUnit = chapterUnit.substring(0, chapterUnit.length()-1);
            Log.d("CHAPTER ", chapterUnit);
            chapter = chapterUnit.replaceAll(" ", "\" and Fundstelle = \"");
        }

        //String query = "select * from Vokabelliste where id = " + id;
        String query = "select * from test where Band = \"" + book + "\" and Fundstelle = \""
                + chapter + "\" and Tested = " + level;
        Cursor cursor = this.getDbConnection().rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String vocabulary = cursor.getString(cursor.getColumnIndexOrThrow("Vokabel"));
                String lemma = cursor.getString(cursor.getColumnIndexOrThrow("VocLemma"));
                String translation = cursor.getString(cursor.getColumnIndexOrThrow("Uebersetzung"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
                //String book = cursor.getString(cursor.getColumnIndexOrThrow("Band"));
                //String chapter = cursor.getString(cursor.getColumnIndexOrThrow("Fundstelle"));
                String pos = cursor.getString(cursor.getColumnIndexOrThrow("Wortart"));
                String sentences = cursor.getString(cursor.getColumnIndexOrThrow("SentId"));
                //int tested = cursor.getInt(cursor.getColumnIndexOrThrow("Tested"));


                vocObject = new VocObject(id, vocabulary, lemma, translation, status, book, chapter, pos, sentences, level);
                allVocabulary.add(vocObject);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return allVocabulary;
    }

    public ArrayList getWordsByBookChapter(String book, String chapter, String unit){
        ArrayList<VocObject> allVocabulary = new ArrayList<VocObject>();
        VocObject vocObject;
        if (!chapter.equals("Welcome")){
            String[] units = unit.split(" ");
            String chapterUnit = "";
            for (String u : units){
                chapterUnit += chapter + "/" + u + " ";
            }
            chapterUnit = chapterUnit.substring(0, chapterUnit.length()-1);
            Log.d("CHAPTER ", chapterUnit);
            chapter = chapterUnit.replaceAll(" ", "\" and Fundstelle = \"");
        }

        //String query = "select * from Vokabelliste where id = " + id;
        String query = "select * from test where Band = \"" + book + "\" and Fundstelle = \""
                + chapter + "\"" ;
        Cursor cursor = this.getDbConnection().rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String vocabulary = cursor.getString(cursor.getColumnIndexOrThrow("Vokabel"));
                String lemma = cursor.getString(cursor.getColumnIndexOrThrow("VocLemma"));
                String translation = cursor.getString(cursor.getColumnIndexOrThrow("Uebersetzung"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
                //String book = cursor.getString(cursor.getColumnIndexOrThrow("Band"));
                //String chapter = cursor.getString(cursor.getColumnIndexOrThrow("Fundstelle"));
                String pos = cursor.getString(cursor.getColumnIndexOrThrow("Wortart"));
                String sentences = cursor.getString(cursor.getColumnIndexOrThrow("SentId"));
                int tested = cursor.getInt(cursor.getColumnIndexOrThrow("Tested"));


                vocObject = new VocObject(id, vocabulary, lemma, translation, status, book, chapter, pos, sentences, tested);
                allVocabulary.add(vocObject);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return allVocabulary;
    }

    public void updateTested(int value, int id){
        ContentValues data = new ContentValues();
        data.put("Tested", value);
        db.update("test", data, "_id=" + id, null);
    }
}
