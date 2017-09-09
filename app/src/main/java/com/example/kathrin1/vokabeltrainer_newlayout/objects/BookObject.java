package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

/**
 * Created by kathrin1 on 30.01.17.
 */

public class BookObject
{

    private int id;
    private String book;
    private String chapter;
    private String sentence;
    private String wordTag;
    private String lemmaToken;
    private String lemmaTag;
    private String lemma;

    private boolean isEmpty = false;

    public BookObject(int id, String book, String chapter, String sentence, String wordTag,
                      String lemmaToken, String lemmaTag, String lemma)
    {

        this.id = id;
        this.book = book;
        this.chapter = chapter;
        this.sentence = sentence;
        this.wordTag = wordTag;
        this.lemmaToken = lemmaToken;
        this.lemmaTag = lemmaTag;
        this.lemma = lemma;
    }

    /**
     * Reads in the values at the current position in the given cursor to generate a new
     * BookObject.
     *
     * @param cursor Database cursor at the position of the word to instantiate.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public BookObject(Cursor cursor, String tableName) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating BookObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating BookObject.");

        if (tableName.equals("sentences_book")) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.BOOK_ID));
            book = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_BOOK));
            chapter = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_CHAPTER));
            sentence = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_SENTENCE));
            wordTag = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_TAGGED));
            lemmaToken = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_LEMMA_TOKEN));
            lemmaTag = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_LEMMA_TAG));
            lemma = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.BOOK_LEMMA));
        }
        else {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.CORPUS_ID));
            sentence = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.CORPUS_SENTENCE));
            lemmaToken = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.CORPUS_MAPPED));
            lemmaTag = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.CORPUS_TAGGED));
        }
    }

    /**
     * Generates an empty sentence to use as a non-null placeholder in case of errors.
     *
     * @param c Context within which to perform the operation.  Used to access string resources.
     * @return A generic, empty sentence object
     */
    public static BookObject emptySentence(Context c)
    {
        return new BookObject(-1, "-", "-", "", "", "", "", "")
                       .setEmpty(true);
    }

    /**
     * Generates an empty sentence to use as a non-null placeholder in case of errors.
     * This uses hardcoded string instead of translatable string resource.
     * Use emptySentence(Context c) to utilize string resources.
     *
     * @return A generic, empty sentence object
     */
    public static BookObject emptySentence()
    {
        return new BookObject(-1, "-", "-", "", "", "", "", "")
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
    public static BookObject buildForSingleWord(String word)
    {
        if (word.contains(" "))
            Log.e("[BookObject]", "WARNING:  Method 'buildForSingleWord()' should only be given a " +
                                  "single word.  Otherwise, may result in undefined behavior.");

        return new BookObject(-1, "-", "-", word, word,
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

    public String getWordTag()
    {
        return wordTag;
    }

    public String getLemmaToken()
    {
        return lemmaToken;
    }

    public String getLemmaTag()
    {
        return lemmaTag;
    }

    public String getLemma()
    {
        return lemma;
    }

    private BookObject setEmpty(boolean empty)
    {
        isEmpty = empty;
        return this;
    }

    public boolean isEmpty()
    {
        return isEmpty;
    }
}
