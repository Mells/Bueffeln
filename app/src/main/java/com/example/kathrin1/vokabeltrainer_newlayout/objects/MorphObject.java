package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

public class MorphObject {

    private int id;
    private String word;
    private String lemma;
    private String reading1;
    private String reading2;
    private String reading3;
    private String reading4;
    private String reading5;
    private String reading6;
    private String reading7;
    private String reading8;

    public MorphObject(int id, String word, String lemma, String reading1, String reading2,
                       String reading3, String reading4, String reading5, String reading6,
                       String reading7, String reading8) {
        this.id = id;
        this.word = word;
        this.lemma = lemma;
        this.reading1 = reading1;
        this.reading2 = reading2;
        this.reading3 = reading3;
        this.reading4 = reading4;
        this.reading5 = reading5;
        this.reading6 = reading6;
        this.reading7 = reading7;
        this.reading8 = reading8;

    }

    /**
     * Reads in the values at the current position in the given cursor to generate a new
     * MorphObject.
     *
     * @param cursor Database cursor at the position of the word to instantiate.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public MorphObject(Cursor cursor) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating VocObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating VocObject.");

        id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.MORPH_ID));
        word = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_WORD));
        lemma = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_LEMMA));
        reading1 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING1));
        reading2 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING2));
        reading3 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING3));
        reading4 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING4));
        reading5 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING5));
        reading6 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING6));
        reading7 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING7));
        reading8 = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.MORPH_READING8));
        //activation = !cursor.isNull(cursor.getColumnIndexOrThrow(DBHandler.WORD_ACTIVATION))
        //        ? cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_ACTIVATION))
        //        : null;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getLemma() { return lemma; }

    public String getReading1() { return reading1; }

    public String getReading2() { return reading2; }

    public String getReading3() { return reading3; }

    public String getReading4() { return reading4; }

    public String getReading5() { return reading5; }

    public String getReading6() { return reading6; }

    public String getReading7() { return reading7; }

    public String getReading8() { return reading8; }

    public String getAllReadings() {return reading1 + ";" + reading2 + ";" + reading3 + ";" + reading4
            + ";" + reading5 + ";" + reading6 + ";" + reading7 + ";" + reading8;}

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MorphObject vocObject = (MorphObject) o;

        return id == vocObject.id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "MorphObject{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", lemma='" + lemma + '\'' +
                ", reading1='" + reading1 + '\'' +
                ", reading2='" + reading2 + '\'' +
                ", reading3='" + reading3 + '\'' +
                ", reading4='" + reading4 + '\'' +
                ", reading5='" + reading5 + '\'' +
                ", reading6='" + reading6 + '\'' +
                ", reading7='" + reading7 + '\'' +
                ", reading8='" + reading8 + '\'' +
               '}';
    }
}