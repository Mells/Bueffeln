package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;

import java.text.ParseException;
import java.util.Date;

/**
 * This class describes all relevant information relating to an interaction with a word presentation.
 */
public class InterxObject
{
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILURE = "FAILURE";

    private final int id;
    private final int wordId;
    private VocObject word;
    private final int latency; // In milliseconds
    private final Date timestamp;
    private final String result;

    /**
     * Private constructor, use static constructor instead.
     */
    private InterxObject(int id, VocObject word, int latency, Date timestamp, String result)
    {
        this.id = id;
        this.word = word;
        this.wordId = word.getId();
        this.latency = latency;
        this.timestamp = timestamp;
        this.result = result;
    }

    /**
     * Private constructor, use static constructor instead.
     */
    private InterxObject(int id, int wordId, int latency, Date timestamp, String result)
    {
        this.id = id;
        this.word = null;
        this.wordId = wordId;
        this.latency = latency;
        this.timestamp = timestamp;
        this.result = result;
    }

    /**
     * Static constructor, creates a new word interaction object and returns it.
     *
     * @param id        Local database ID of the interaction
     * @param word      The word that was presented
     * @param latency   The measured reaction time for the interaction
     * @param timestamp The timestamp of the moment the word was presented
     * @param result    String describing the result of the interaction
     * @return The newly constructed InterxObject object.
     */
    public static InterxObject build(int id, VocObject word, int latency, Date timestamp, String result)
    {
        return new InterxObject(id, word, latency, timestamp, result);
    }

    /**
     * Static constructor, creates a new word interaction object object and returns it.  This takes
     * the ID of the word, but does not link to the corresponding word data.  This link can be
     * later established with {@link InterxObject#setWord(VocObject)} or
     * {@link InterxObject#link(DatabaseManager)}.
     *
     * @param id        Local database ID of the interaction
     * @param wordId    The ID word that was presented
     * @param latency   The measured reaction time for the interaction
     * @param timestamp The timestamp of the moment the word was presented
     * @param result    String describing the result of the interaction
     * @return The newly constructed InterxObject object.
     */
    public static InterxObject buildWithoutLink(int id, int wordId, int latency, Date timestamp, String result)
    {
        return new InterxObject(id, wordId, latency, timestamp, result);
    }


    /**
     * Static constructor, constructs a new word interaction object by reading the data pointed
     * to by the given cursor object.
     *
     * @param cursor Database cursor at the position of the interaction object to instantiate.
     * @param manager Database manager to allow retrieval of word data by ID.
     * @return The newly constructed InterxObject object.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public static InterxObject build(Cursor cursor, DatabaseManager manager) throws IllegalArgumentException
    {
        return buildWithoutLink(cursor).link(manager);
    }

    /**
     * Static constructor, constructs a new word interaction object by reading the data pointed
     * to by the given cursor object. This takes
     * the ID of the word, but does not link to the corresponding word data.  This link can be
     * later established with {@link InterxObject#setWord(VocObject)} or
     * {@link InterxObject#link(DatabaseManager)}.
     *
     * @param cursor Database cursor at the position of the interaction object to instantiate.
     * @return The newly constructed InterxObject object.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public static InterxObject buildWithoutLink(Cursor cursor) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating VocObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating VocObject.");

        // Extract interaction ID
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.INTERX_ID));

        // Extract word by reference
        int wordId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.INTERX_WORD_DBID));

        // Extract latency value
        int latency = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.INTERX_LATENCY));

        // Extract and attempt to parse timestamp
        String timeString = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.INTERX_TIME));
        Date timestamp;
        try
        {
            timestamp = DBHandler.SQL_DATE.parse(timeString);
        } catch (ParseException e)
        {
            throw new IllegalArgumentException("Error while parsing timestamp.");
        }

        // Extract result
        String result = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.INTERX_RESULT));

        // Build the interaction object
        return buildWithoutLink(id, wordId, latency, timestamp, result);
    }

    public int getId()
    {
        return id;
    }

    public VocObject getWord()
    {
        return word;
    }

    public int getWordId()
    {
        return wordId;
    }

    public int getLatency()
    {
        return latency;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public String getResult()
    {
        return result;
    }

    /**
     * Setter for the word associated with this interaction.  Return this object in order
     * to facilitate chaining.
     *
     * @param word The word to associate with this interaction.
     * @return This InterxObject.
     */
    public InterxObject setWord(VocObject word)
    {
        this.word = word;
        return this;
    }

    /**
     * Identifies the word object in the database associated with the word ID stored in this object.
     * Return this object in order to facilitate chaining.
     *
     * @param manager Database manager to allow retrieval of word data by ID.
     * @return This InterxObject.
     */
    public InterxObject link(DatabaseManager manager)
    {
        word = manager.getWordPairById(wordId);
        return this;
    }
}
