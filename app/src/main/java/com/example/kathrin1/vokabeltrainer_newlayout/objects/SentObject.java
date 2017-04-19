package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.Context;
import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

/**
 * Created by kathrin1 on 30.01.17.
 */

public class SentObject {

    private int id;
    private String book;
    private String chapter;
    private String sentence;
    private String tagged;
    private String mapped;
    private String lemma;

    public SentObject(int id, String book, String chapter, String sentence, String tagged, String mapped, String lemma) {

        this.id = id;
        this.book = book;
        this.chapter = chapter;
        this.sentence = sentence;
        this.tagged = tagged;
        this.mapped = mapped;
        this.lemma = lemma;
    }

    /**
     * Reads in the values at the current position in the given cursor to generate a new
     * SentObject.
     *
     * @param cursor Database cursor at the position of the word to instantiate.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public SentObject(Cursor cursor) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating SentObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating SentObject.");

        id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.SENT_ID));
        book = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_BOOK));
        chapter = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_CHAPTER));
        sentence = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_SENTENCE));
        tagged = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_TAGGED));
        mapped = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_MAPPED));
        lemma = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SENT_LEMMA));
    }

    /**
     * Generates an empty sentence to use as a non-null placeholder in case of errors.
     *
     * @param c Context within which to perform the operation.  Used to access string resources.
     * @return A generic, empty sentence object
     */
    public static SentObject emptySentence(Context c)
    {
        return new SentObject(-1, "-", "-", c.getString(R.string.Sent_Missing), "", "", "");
    }

    /**
     * Generates an empty sentence to use as a non-null placeholder in case of errors.
     * This uses hardcoded string instead of translatable string resource.
     * Use emptySentence(Context c) to utilize string resources.
     *
     * @return A generic, empty sentence object
     */
    public static SentObject emptySentence()
    {
        return new SentObject(-1, "-", "-", ":SENTENCE MISSING:", "", "", "");
    }

    public int getId() {
        return id;
    }

    public String getBook() {
        return book;
    }

    public String getChapter() {
        return chapter;
    }

    public String getSentence() { return sentence; }

    public String getTagged() { return tagged; }

    public String getMapped() { return mapped; }

    public String getLemma() { return lemma; }

}
