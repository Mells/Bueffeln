package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;
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
    public static final int NEW_INTERX_ID = -1;

    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILURE = "FAILURE";
    public static final String EXERCISE_TRAIN = "TRAIN";
    public static final String EXERCISE_TEST = "TEST";

    private long id;
    private final int wordId;
    private VocObject word;
    private final int latency; // In milliseconds
    private final Date timestamp;
    private final String result;
    private final int charCount;
    private final String exerciseType;
    private final float preAlpha, preActivation;
    private Float postAlpha;
    private Long session;

    private Float activation; // Used for storing activation during activation calculations,
    // but is not stored in database.

    /**
     * Private constructor, use static constructor instead.
     */
    private InterxObject(long id, VocObject word, int latency, Date timestamp, String result,
                         int charCount, String exerciseType, float preAlpha,
                         float preActivation)
    {
        this.id = id;
        this.word = word;
        this.wordId = word.getId();
        this.latency = latency;
        this.timestamp = timestamp;
        this.result = result;
        this.charCount = charCount;
        this.exerciseType = exerciseType;
        this.preAlpha = preAlpha;
        this.preActivation = preActivation;
    }

    /**
     * Private constructor, use static constructor instead.
     */
    private InterxObject(long id, int wordId, int latency, Date timestamp, String result,
                         int charCount, String exerciseType, float preAlpha,
                         float preActivation)
    {
        this.id = id;
        this.word = null;
        this.wordId = wordId;
        this.latency = latency;
        this.timestamp = timestamp;
        this.result = result;
        this.charCount = charCount;
        this.exerciseType = exerciseType;
        this.preAlpha = preAlpha;
        this.preActivation = preActivation;
    }

    /**
     * Static constructor, creates a new word interaction object and returns it.
     *
     * @param id            Local database ID of the interaction.  If this is a new interaction
     *                      not yet in the database, this should be set to
     *                      {@link InterxObject#NEW_INTERX_ID}.
     * @param word          The word that was presented
     * @param latency       The measured reaction time for the interaction
     * @param timestamp     The timestamp of the moment the word was presented
     * @param result        String describing the result of the interaction
     * @param charCount     The number of characters in the context sentence for the presentation.
     * @param exerciseType  The type of exercise used for the presentation.
     * @param preAlpha      The alpha value of the word before the interaction
     * @param preActivation The activation of the item before the interaction
     * @return The newly constructed InterxObject object.
     */
    public static InterxObject build(long id, VocObject word, int latency, Date timestamp,
                                     String result, int charCount, String exerciseType,
                                     float preAlpha, float preActivation)
    {
        return new InterxObject(id, word, latency, timestamp, result, charCount, exerciseType,
                                preAlpha, preActivation);
    }

    /**
     * Static constructor, creates a new word interaction object object and returns it.  This takes
     * the ID of the word, but does not link to the corresponding word data.  This link can be
     * later established with {@link InterxObject#setWord(VocObject)} or
     * {@link InterxObject#link(DatabaseManager)}.
     *
     * @param id           Local database ID of the interaction.  If this is a new interaction
     *                     not yet in the database, this should be set to
     *                     {@link InterxObject#NEW_INTERX_ID}.
     * @param wordId       The ID word that was presented
     * @param latency      The measured reaction time for the interaction
     * @param timestamp    The timestamp of the moment the word was presented
     * @param result       String describing the result of the interaction
     * @param charCount    The number of characters in the context sentence for the presentation.
     * @param exerciseType The type of exercise used for the presentation.
     * @param preAlpha      The alpha value of the word before the interaction
     * @param preActivation The activation of the item before the interaction
     * @return The newly constructed InterxObject object.
     */
    public static InterxObject buildWithoutLink(long id, int wordId, int latency, Date timestamp,
                                                String result, int charCount, String exerciseType,
                                                float preAlpha, float preActivation)
    {
        return new InterxObject(id, wordId, latency, timestamp, result, charCount, exerciseType,
                                preAlpha, preActivation);
    }


    /**
     * Static constructor, constructs a new word interaction object by reading the data pointed
     * to by the given cursor object.
     *
     * @param cursor  Database cursor at the position of the interaction object to instantiate.
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

        // Extract character count
        int charCount = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.INTERX_CHARCOUNT));

        // Extract exercise type
        String exerciseType = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.INTERX_EXERCISE_TYPE));

        // Extract the session reference
        Long sessionId = cursor.isNull(cursor.getColumnIndexOrThrow(DBHandler.INTERX_SESSION))
                         ? null
                         : cursor.getLong(cursor.getColumnIndexOrThrow(DBHandler.INTERX_SESSION));

        float preAlpha = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.INTERX_PREALPHA));
        float postAlpha = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.INTERX_POSTALPHA));
        float preActivation = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.INTERX_PREACTIVATION));


        // Build the interaction object
        return buildWithoutLink(id, wordId, latency, timestamp, result, charCount, exerciseType,
                                preAlpha, preActivation)
                       .setSessionId(sessionId);
    }

    public long getId()
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

    public int getCharCount()
    {
        return charCount;
    }

    public String getExerciseType()
    {
        return exerciseType;
    }

    public Long getSessionId()
    {
        return session;
    }

    public float getPreAlpha()
    {
        return preAlpha;
    }

    public Float getPostAlpha()
    {
        return postAlpha;
    }

    public InterxObject setPostAlpha(Float postAlpha)
    {
        this.postAlpha = postAlpha;
        return this;
    }

    public float getPreActivation()
    {
        return preActivation;
    }

    /**
     * Sets the session reference for this interaction.  Returns this InterxObject in order
     * to facilitate chaining.
     *
     * @param session The session to reference.  May be left null.
     * @return This InterxObject.
     */
    public InterxObject setSessionId(Long session)
    {
        this.session = session;
        return this;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public ContentValues getContentVals()
    {
        ContentValues vals = new ContentValues();

        if (id > 0)
            vals.put(DBHandler.INTERX_ID, id);
        vals.put(DBHandler.INTERX_WORD_DBID, wordId);
        vals.put(DBHandler.INTERX_LATENCY, latency);
        vals.put(DBHandler.INTERX_TIME, DBHandler.SQL_DATE.format(timestamp));
        vals.put(DBHandler.INTERX_RESULT, result);
        vals.put(DBHandler.INTERX_CHARCOUNT, charCount);
        vals.put(DBHandler.INTERX_EXERCISE_TYPE, exerciseType);
        if (session != null)
            vals.put(DBHandler.INTERX_SESSION, session);

        return vals;
    }

    /*
     * ===================================================================================
     * The following 4 methods are for caching activation values the instant of this
     * interaction.  These values are only temporary for the current activation calculation, and
     * only serve to prevent duplicated math.  These values will change as the word's alpha
     * fluctuates, so there is no value in permanently storing this.
     * ===================================================================================
     */

    /**
     * Stores a value of activation to attribute to this interaction.  This value is only
     * temporarily stored, and is not written to the database.
     *
     * @param activation The activation value to set.
     */
    public void storeActivationValue(float activation)
    {
        this.activation = activation;
    }

    /**
     * Gets the temporarily stored activation value.  Throws a NullPointerException if no
     * value has been stored.  Use {@link InterxObject#hasActivation()} first to avoid this.
     *
     * @return The stored activation value
     */
    public float retrieveActivation()
    {
        if (activation == null)
            throw new NullPointerException("Activation value not set for this InterxObject.");

        return activation;
    }

    /**
     * Returns whether or not this interaction object is temporarily storing an activation value.
     *
     * @return Returns true is this object is storing an activation value, false otherwise.
     */
    public boolean hasActivation()
    {
        return (activation != null);
    }

    /**
     * Removes the temporarily held activation value for this interaction.
     */
    public void clearActivation()
    {
        activation = null;
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
