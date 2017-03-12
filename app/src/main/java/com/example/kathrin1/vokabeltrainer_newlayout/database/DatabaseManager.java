package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class serves as the middleman between the database and any code that requires
 * data from the database, handling any queries, additions, or modifications.
 */
public class DatabaseManager
{
    public static final String LOG_TAG = "[DBManager]";

    private DBHandler handler;
    private SQLiteDatabase db;
    private Context c;

    /**
     * Private constructor, use static constructor instead.
     */
    private DatabaseManager(Context c)
    {
        this.c = c;
        handler = new DBHandler(c);
        db = handler.getWritableDatabase();
    }

    /**
     * Static constructor, creates a new database manager object and returns it.
     *
     * @return The newly constructed DatabaseManager object.
     */
    public static DatabaseManager build(Context c)
    {
        return new DatabaseManager(c);
    }

    /**
     * Destroys this database manager, cleanly closing the associated database.
     */
    public void destroy()
    {
        db.close();
    }

    /**
     * Retrieves a list of all English words in the dictionary.
     *
     * @return List of English words in the dictionary.
     */
    public List<String> dictionaryEnglishWords()
    {
        return getStringsInField(DBHandler.WORD_TABLENAME, DBHandler.WORD_WORD);
    }

    /**
     * Retrieves a list of all German words in the dictionary.
     *
     * @return List of German words in the dictionary.
     */
    public List<String> dictionaryGermanWords()
    {
        return getStringsInField(DBHandler.WORD_TABLENAME, DBHandler.WORD_TRANSLATION);
    }

    /**
     * Gets all string entries in the given field (column) of the given table.
     *
     * @param table     Name of the table to query.
     * @param fieldName Name of the field (column) to retrieve results from.
     * @return List of all strings found in the given field of the given table.
     */
    public List<String> getStringsInField(String table, String fieldName)
    {
        List<String> words = new ArrayList<>();

        Cursor cursor = db.query(table, new String[]{fieldName},
                                 null, null, null, null, fieldName);

        while (cursor.moveToNext())
            words.add(cursor.getString(cursor.getColumnIndexOrThrow(fieldName)));

        cursor.close();

        return words;
    }

    /**
     * Gets a random example sentence for the given word object.  If no sentence is found,
     * returns an empty error sentence.
     *
     * @param word The word to get an example sentence for
     * @return Example sentence for the given word, or an empty sentence if no example sentence
     *          is found
     */
    public SentObject getExampleSentence(VocObject word)
    {
        String listString = word.getSentences();

        List<String> sentenceList = DBUtils.splitListString(listString);

        Log.d("DE-SentenceList", sentenceList.toString());
        Log.d("DE-SentenceSize", Integer.toString(sentenceList.size()));
        // TODO - take gdex not random

        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sentenceList.size());

        try
        {
            return getSentence(Integer.parseInt(sentenceList.get(index)));
        } catch (NumberFormatException e)
        {
            Log.e(LOG_TAG,
                  String.format("Error parsing integer string (%s).  Returning default sentence instead.",
                                sentenceList.get(index)));

            return SentObject.emptySentence(c);
        }
    }


    /**
     * Retrieves the sentence with the given ID from the sentence database, with the ID given as a
     * string.  Attempts to parse the string as an integer, and returns an error sentence if the
     * string could not be successfully parsed.
     *
     * @param id ID of the sentence to retrieve.
     * @return The retrieved sentence.
     */
    public SentObject getSentence(String id)
    {
        try {
            return getSentence(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG,
                  String.format("Error parsing integer string (%s).  Returning default sentence instead.",
                                id));

            return SentObject.emptySentence(c);
        }
    }

    /**
     * Retrieves the sentence with the given ID from the sentence database.  If the ID is not found
     * in the database, returns an error sentence.
     *
     * @param id ID of the sentence to retrieve.
     * @return The retrieved sentence.
     */
    public SentObject getSentence(int id)
    {
        Cursor cursor = db.query(DBHandler.SENT_TABLENAME, DBHandler.SENT_COLUMNS,
                                 DBHandler.SENT_ID + " = " + id,
                                 null, null, null, null);

        if (cursor.getCount() != 1)
        {
            Log.e(LOG_TAG, "Sentence with the given id [" + id + "] not found.  " +
                           "Returning error sentence instead.");
            return SentObject.emptySentence(c);
        }

        cursor.moveToFirst();

        SentObject returnObj = new SentObject(cursor);

        cursor.close();

        return returnObj;
    }

    /**
     * Retrieves the word with the given ID from the sentence database.
     *
     * @param id ID of the word to retrieve.
     * @return The retrieved word.
     */
    public VocObject getWordPairById(int id)
    {
        Cursor cursor = db.query(DBHandler.WORD_TABLENAME, DBHandler.WORD_COLUMNS,
                                 DBHandler.WORD_ID + " = " + id,
                                 null, null, null, null);

        if (cursor.getCount() != 1)
            throw new NoSuchElementException("Word with the given id [" + id + "] not found.");

        cursor.moveToFirst();

        VocObject returnObj = new VocObject(cursor);

        cursor.close();

        return returnObj;
    }

    /**
     * Gets all words in the database that match the given book, chapter, unit, and level.
     *
     * @param book    Book to filter words by
     * @param chapter Chapter to filter words by
     * @param unit    String of space-separated units to filter words by
     * @param level   Level of the word to filter by
     * @return List of words that meet the given filtering criteria
     */
    public List<VocObject> getWordsByBookChapterLevel(String book, String chapter, String unit,
                                                      int level)
    {
        List<VocObject> vocab = new ArrayList<>();

        chapter = formatChapterForQuery(chapter, unit);

        String query = String.format("select * from %s where %s = '%s' and (%s like %s) and %s = %d",
                                     DBHandler.WORD_TABLENAME,
                                     DBHandler.WORD_BOOK, book,
                                     DBHandler.WORD_CHAPTER, chapter,
                                     DBHandler.WORD_LEVEL, level);

        Cursor cursor = db.rawQuery(query, null);

        if (!cursor.isAfterLast())
        {
            while (cursor.moveToNext())
                vocab.add(new VocObject(cursor));
        }

        cursor.close();

        return vocab;
    }

    /**
     * Gets all words in the database that match the given book, chapter, and unit.
     *
     * @param book    Book to filter words by
     * @param chapter Chapter to filter words by
     * @param unit    String of space-separated units to filter words by
     * @return List of words that meet the given filtering criteria
     */
    public List<VocObject> getWordsByBookChapter(String book, String chapter, String unit)
    {
        List<VocObject> vocab = new ArrayList<>();

        chapter = formatChapterForQuery(chapter, unit);

        String query = String.format("select * from %s where %s = '%s' and (%s like %s)",
                                     DBHandler.WORD_TABLENAME,
                                     DBHandler.WORD_BOOK, book,
                                     DBHandler.WORD_CHAPTER, chapter);

        Cursor cursor = db.rawQuery(query, null);

        if (!cursor.isAfterLast())
        {
            while (cursor.moveToNext())
                vocab.add(new VocObject(cursor));
        }

        cursor.close();

        return vocab;
    }

    /**
     * Updates the tested count for the given word id with the given value
     * TODO:  Eventually get rid of this
     *
     * @param value Value to set
     * @param id    ID of the word to update
     */
    public void updateTested(int value, int id)
    {
        ContentValues data = new ContentValues();
        data.put("Tested", value);
        db.update(DBHandler.WORD_TABLENAME, data, DBHandler.WORD_ID + " = " + id, null);
    }


    /**
     * Converts the given chapter and unit strings into a format for inserting into SQL query
     */
    private String formatChapterForQuery(String chapter, String unit)
    {
        if (!chapter.equals("Welcome"))
        {
            String[] units = unit.split(" ");
            String chapterUnit = "";
            for (String u : units)
            {
                chapterUnit += String.format("'%s/%s%%' or %s like ",
                                             chapter, u, DBHandler.WORD_CHAPTER);
            }
            chapterUnit = chapterUnit.substring(0, chapterUnit.lastIndexOf("'") + 1);
            Log.d("CHAPTER ", chapterUnit);

            return chapterUnit;
        }
        else
            return String.format("'%s'", chapter);
    }

}
