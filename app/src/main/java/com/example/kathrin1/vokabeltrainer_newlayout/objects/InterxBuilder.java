package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;

import java.util.Date;

/**
 * Used for progressively building up an interaction object, {@link InterxObject}
 */
public class InterxBuilder
{

    private VocObject word;
    private int latency = -1; // In milliseconds
    private Date timestamp;
    private String result;
    private int charCount = -1;
    private String exerciseType;


    /**
     * Private constructor, use static constructor instead.
     */
    public InterxBuilder()
    {
    }

    /**
     * Uses the current parameters to build a new interaction object, inserting it into the database
     * referenced by the given database manager.
     *
     * @param manager The manager for the database to insert the interaction into.
     * @return The newly constructed InterxObject.
     */
    public InterxObject build(DatabaseManager manager)
    {
        if (!isReadyToBuild())
            throw new IllegalStateException("Not all interaction fields were initialized in the InterxBuilder.");

        // Insert the information into the database

        ContentValues vals = new ContentValues();

        vals.put(DBHandler.INTERX_WORD, word.getVoc());
        vals.put(DBHandler.INTERX_WORD_DBID, word.getId());
        vals.put(DBHandler.INTERX_LATENCY, latency);
        vals.put(DBHandler.INTERX_TIME, DBHandler.SQL_DATE.format(timestamp));
        vals.put(DBHandler.INTERX_RESULT, result);
        vals.put(DBHandler.INTERX_CHARCOUNT, charCount);
        vals.put(DBHandler.INTERX_EXERCISE_TYPE, exerciseType);

        int id = (int)manager.getDatabase().insert(DBHandler.INTERX_TABLENAME, null, vals);

        return InterxObject.build(id, word, latency, timestamp, result, charCount, exerciseType);
    }

    /**
     * Uses the current parameters to build a new interaction object, but does not insert it into
     * the database.  The returned InterxObject will always have an ID of -1.
     *
     * @return The newly constructed InterxObject.
     */
    public InterxObject buildWithoutInserting()
    {
        if (!isReadyToBuild())
            throw new IllegalStateException("Not all interaction fields were initialized in the InterxBuilder.");

        return InterxObject.build(-1, word, latency, timestamp, result, charCount, exerciseType);
    }

    /**
     * Tests if all fields are initialized, and thus the builder is ready to build.
     *
     * @return Returns true if the builder is ready to build, false otherwise.
     */
    private boolean isReadyToBuild()
    {
        return !(word == null || latency < 0 || timestamp == null || result == null ||
                charCount < 0 || exerciseType == null);
    }


    /**
     * Sets the word value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param word The word value to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setWord(VocObject word)
    {
        this.word = word;
        return this;
    }

    /**
     * Sets the latency value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param latency The latency value to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setLatency(int latency)
    {
        this.latency = latency;
        return this;
    }

    /**
     * Sets the timestamp value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param timestamp The timestamp value to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Sets the result value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param result The result value to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setResult(String result)
    {
        this.result = result;
        return this;
    }

    /**
     * Sets the character count value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param charCount The character count value to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setCharCount(int charCount)
    {
        this.charCount = charCount;
        return this;
    }

    /**
     * Sets the exercise type value for the InterxObject to build.  Returns this builder in order
     * to facilitate chaining.
     *
     * @param exerciseType The word exercise type to set.
     * @return This InterxBuilder object.
     */
    public InterxBuilder setExerciseType(String exerciseType)
    {
        this.exerciseType = exerciseType;
        return this;
    }
}