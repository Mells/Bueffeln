package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

/**
 * Created by kathrin1 on 30.01.17.
 */

public class SentObject
{

    private int id;
    private String book;
    private String chapter;
    private String sentence;
    private String tagged;
    private String mapped;
    private String lemma;

    private boolean isEmpty = false;

    public SentObject(int id, String book, String chapter, String sentence, String tagged, String mapped, String lemma)
    {

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
        return new SentObject(-1, "-", "-", c.getString(R.string.Sent_Missing), "", "", "")
                       .setEmpty(true);
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
        return new SentObject(-1, "-", "-", ":SENTENCE MISSING:", "", "", "")
                       .setEmpty(true);
    }

    /**
     * Builds a sentence object from the given string, assuming it is a single word.  This is used
     * as a last resort to display as a "sentence" in the case that no other suitable sentence
     * has been found for a word, rather than displaying "SENTENCE MISSING" message.
     *
     * @param word The word to build into a sentence object.
     * @return The newly constructed sentence object.
     */
    public static SentObject buildForSingleWord(String word)
    {
        if (word.contains(" "))
            Log.e("[SentObject]", "WARNING:  Method 'buildForSingleWord()' should only be given a " +
                                  "single word.  Otherwise, may result in undefined behavior.");

        return new SentObject(-1, "-", "-", word,
                              String.format("[('%s', 'UNK')]", word),
                              String.format("{'%s': ['%s']}", word, word),
                              String.format("['%s']", word));
    }

    public int getId()
    {
        return id;
    }

    public String getBook()
    {
        return book;
    }

    public String getChapter()
    {
        return chapter;
    }

    public String getSentence()
    {
        return sentence;
    }

    public String getTagged()
    {
        return tagged;
    }

    public String getMapped()
    {
        return mapped;
    }

    public String getLemma()
    {
        return lemma;
    }

    private SentObject setEmpty(boolean empty)
    {
        isEmpty = empty;
        return this;
    }

    public boolean isEmpty()
    {
        return isEmpty;
    }
}
