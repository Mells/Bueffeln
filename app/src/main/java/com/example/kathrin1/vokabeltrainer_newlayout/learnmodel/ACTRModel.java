package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.exceptions.ModelNotInitializedException;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final List<SessionObject> sessions;
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
        sessions = new ArrayList<>();
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

    /**
     * Checks whether the given word meets the restrictions set by book, chapter, and unit.
     */
    private boolean meetsRestrictions(VocObject word)
    {
        return (book == null || word.getBook().equals(book))
               && (chapter == null || word.getChapter().startsWith(chapter))
               && (unit == null || word.getChapter().contains("/" + unit));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject getNextWord(VocObject... ignoreWords)
    {
        // Convert the given array to a set, for O(1) .contains() checks
        // May actually perform worse, as the list is likely to be very small
        // TODO:  Evaluate this
        Set<VocObject> ignoreSet = new HashSet<>(Arrays.asList(ignoreWords));

        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        VocObject min = null;
        VocObject newWord = null;

        for (VocObject word : interactions.keySet())
        {
            // If the word does not meet the requirements for presentation, ignore it
            if (!meetsRestrictions(word) || ignoreSet.contains(word))
                continue;

            // If the word has no alpha value, then it is a new word.  Note the first new word
            // encountered, in case a new word needs to be presented.
            if (word.getActivation() == null)
            {
                if (newWord == null)
                    newWord = word;
                continue;
            }

            if (min == null || word.getActivation() < min.getActivation())
                min = word;
        }

        // Selection criteria:
        // 1. Lowest word below activation threshold
        // 2. If no words below threshold, then a new word
        // 3. If there are no new words, the word with the lowest activation

        if (min == null) // Should only happen if no words meet the requirements
            return null;
        else if (min.getActivation() < ModelMath.THRESHOLD || newWord == null)
            return min;
        else
            return newWord;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject calculateNextWord(VocObject... ignoreWords)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalculateActivation();
        return getNextWord(ignoreWords);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateNextWordASync(final WordSelectionListener listener,
                                       final VocObject... ignoreWords)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalculateActivationASync(new CalcListener()
        {
            @Override
            public void onCompletion()
            {
                if (listener != null)
                    listener.onSelection(getNextWord(ignoreWords));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize()
    {
        try
        {
            // .get() forces this thread to wait until the asynctask completes,
            // essentially making it synchronous.
            initTask(null).execute().get();
        } catch (Exception e)
        {
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
        initTask(listener).execute();
    }

    /**
     * Constructs a new ASyncTask used to initialize the model asynchronously, and returns it.
     * This task will not yet have been executed.
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

                    if (word.getActivation() == null)
                        word.setAlpha(ModelMath.ALPHA_DEFAULT);
                }

                // Retrieves all interactions from the database without automatically linking,
                // allowing for manual linking here without the need for further database querying.
                for (InterxObject interx : dbManager.getAllInteractionsWithoutLinking())
                {
                    VocObject word = wordIdMap.get(interx.getWordId());

                    // If for some reason there is no word matching the interaction's word ID,
                    // skip this interaction
                    if (word == null)
                        continue;

                    interx.setWord(word);
                    interactions.get(word).add(interx);
                }

                // Retrieves all study sessions from the database
                sessions.addAll(dbManager.getAllStudySessions());

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
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        try
        {
            // .get() forces this thread to wait until the asynctask completes,
            // essentially making it synchronous.
            recalcTask(null).execute().get();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Error occurred while recalculating activation of items in model.", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateActivationASync(CalcListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalcTask(listener).execute();
    }


    /**
     * Constructs a new ASyncTask used to recalculate all activation values asynchronously,
     * and returns it.
     * This task will not yet have been executed.
     *
     * // TODO:  This also stores activation values in the database as soon as they are calculated.
     * // TODO:  May not want to do this, and force local sync manually later instead.
     *
     * @param listener The listener object to call upon completion.  May be left null.
     * @return The constructed ASyncTask, which has not yet been executed.
     */
    private AsyncTask<Void, Void, Void> recalcTask(final CalcListener listener)
    {
        // The meat occurs inside the doInBackground() method of this object
        return new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                Date time = new Date();

                for (Map.Entry<VocObject, List<InterxObject>> entry : interactions.entrySet())
                {
                    float activation = ModelMath.activation(time, entry.getKey(), entry.getValue(),
                                                            sessions, entry.getKey().getAlpha());
                    entry.getKey().setActivation(activation);

                    ContentValues vals = new ContentValues();
                    vals.put(DBHandler.WORD_ACTIVATION, activation);
                    dbManager.getDatabase()
                             .update(DBHandler.WORD_TABLENAME, vals,
                                     DBHandler.WORD_ID + "=" + entry.getKey().getId(),
                                     null);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (listener != null)
                    listener.onCompletion();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewInteraction(InterxObject interx)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        try
        {
            // .get() forces this thread to wait until the asynctask completes,
            // essentially making it synchronous.
            addInterxTask(null).execute(interx).get();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Error occurred while adding interaction to the model.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewInteractionASync(InterxObject interx, CalcListener listener)
    {
        addInterxTask(listener).execute(interx);
    }

    /**
     * Constructs a new ASyncTask used to add an interaction to the model and the database, and
     * recalculate the alpha value for the presented item.
     * This task will not yet have been executed.
     *
     * @param listener The listener object to call upon completion.  May be left null.
     * @return The constructed ASyncTask, which has not yet been executed.
     */
    private AsyncTask<InterxObject, Void, Void> addInterxTask(final CalcListener listener)
    {
        return new AsyncTask<InterxObject, Void, Void>()
        {
            @Override
            protected Void doInBackground(InterxObject... params)
            {
                for (InterxObject interx : params)
                {
                    // If a word is not linked by the interaction, establish the link
                    if (interx.getWord() == null)
                    {
                        VocObject wordFromId = wordIdMap.get(interx.getWordId());

                        // If there is no linked word, and the ID is not in this model, ignore
                        // this interaction and move on.
                        if (wordFromId == null)
                            continue;
                        else
                            interx.setWord(wordFromId);
                    }

                    VocObject word = interx.getWord();

                    // If the word is not yet in the model, add it
                    if (!interactions.containsKey(word))
                        interactions.put(word, new ArrayList<InterxObject>());

                    // Add the interaction to the model
                    interactions.get(word).add(interx);

                    // Insert the interaction into the database and get it's new ID
                    long id = dbManager.updateInteraction(interx);
                    // Set the ID of the interaction
                    interx.setId(id);

                    float newAlpha = ModelMath.newAlpha(interx, interactions.get(word), sessions);

                    word.setAlpha(newAlpha);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (listener != null)
                    listener.onCompletion();
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VocObject> getAllWords()
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        return new ArrayList<>(interactions.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUnknownParseIDs(ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushToRemote(ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        // TODO:  Implement

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullFromRemote(ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();


        // TODO:  Implement

    }
}
