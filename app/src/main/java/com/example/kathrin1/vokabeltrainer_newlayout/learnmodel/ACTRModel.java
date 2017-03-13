package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a learning model using the ACT-R learn model system
 */
public class ACTRModel implements LearnModel
{
    private static final String LOG_TAG = "[ACTRModel]";

    private boolean initialized = false;
    private final Context c;
    private final Map<VocObject, List<InterxObject>> interactions;
    private final SparseArray<VocObject> wordIdMap;
    private final DatabaseManager dbManager;

    private String book, chapter, unit;

    /**
     * Private constructor, use static constructor instead.
     */
    private ACTRModel(Context c)
    {
        this.c = c;
        interactions = new HashMap<>();
        wordIdMap = new SparseArray<>();
        dbManager = DatabaseManager.build(c);
    }

    /**
     * Static constructor, creates a new ACT-R model object and returns it.
     *
     * @param c The context for this model to operate within.
     * @return The newly constructed ACTRModel object.
     */
    public static ACTRModel build(Context c)
    {
        return new ACTRModel(c);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setRestrictions(String book, String chapter, String unit)
    {
        this.book = book;
        this.chapter = chapter;
        this.unit = unit;
    }

    private boolean meetsRestrictions(VocObject word)
    {
        // TODO:  Implement
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject getNextWord()
    {
        VocObject min = null;
        VocObject newWord = null;

        for (VocObject word : interactions.keySet())
        {
            // If the word does not meet the requirements for presentation, ignore it
            if (!meetsRestrictions(word))
                continue;

            // If the word has no alpha value, then it is a new word.  Note the first new word
            // encountered, in case a new word needs to be presented.
            if (word.getAlpha() == null)
            {
                if (newWord == null)
                    newWord = word;
                continue;
            }

            if (min == null || word.getAlpha() < min.getAlpha())
                min = word;
        }

        // Selection criteria:
        // 1. Lowest word below activation threshold
        // 2. If no words below threshold, then a new word
        // 3. If there are no new words, the word with the lowest activation

        if (min == null) // Should only happen if no words meet the requirements
            return null;
        else if (min.getAlpha() < Params.THRESHOLD || newWord == null)
            return min;
        else
            return newWord;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject calculateNextWord()
    {
        // TODO:  Implement
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateNextWordASync(WordSelectionListener listener)
    {
        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize()
    {
        try
        {
            initTask(null).execute().get();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error occurred while initializing model.  Could not initialize.", e);
            initialized = false; // Probably redundant
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeASync(CalcListener listener)
    {
        initTask(listener);
    }

    /**
     * Constructs a new ASyncTask used to initialize the model asynchronously, and returns it.
     *
     * @param listener The listener object to call upon completion.  May be left null.
     * @return The constructed ASyncTask, which has not yet been executed.
     */
    private AsyncTask<Void, Void, Void> initTask(final CalcListener listener)
    {
        // The meat occurs inside the doInBackground() method of this object
        return new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                // Mapping of word IDs to corresponding words is maintained so that database
                // does not need to be queried again to link interactions to words.

                // Retrieve all words from the database, adding them to the ID map and the
                // interactions map.
                for (VocObject word : dbManager.getAllWords())
                {
                    interactions.put(word, new ArrayList<InterxObject>());
                    wordIdMap.put(word.getId(), word);
                }

                // Retrieves all interactions from the database without automatically linking,
                // allowing for manual linking here without the need for further database querying.
                for (InterxObject interx : dbManager.getAllInteractionsWithoutLinking())
                {
                    interx.setWord(wordIdMap.get(interx.getWordId()));
                    interactions.get(interx.getWord()).add(interx);
                }

                return null;
            }



            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (listener != null)
                    listener.onCompletion();

                initialized = true;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateActivation()
    {
        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateActivationASync(CalcListener listener)
    {
        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VocObject> getAllWords()
    {
        // TODO:  Implement
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUnknownParseIDs(ParseResponseListener listener)
    {
        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushToRemote(ParseResponseListener listener)
    {
        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullFromRemote(ParseResponseListener listener)
    {
        // TODO:  Implement

    }
}
