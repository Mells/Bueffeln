package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.exceptions.ModelNotInitializedException;
import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.network.RequestManager;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.GenericUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.NetworkFailureListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.SuccessListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.UserUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.WordListSuccessListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.WordListUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.UserObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
    private final RequestManager requestManager;
    private final StoredValueManager svManager;

    private String book, chapter, unit;

    private UserObject user = null;

    /**
     * Private constructor, use static constructor instead.
     */
    private ACTRModel(Context c, DatabaseManager dm)
    {
        this.c = c;
        interactions = new HashMap<>();
        wordIdMap = new SparseArray<>();
        sessions = new ArrayList<>();
        dbManager = dm;
        requestManager = RequestManager.build(c, dbManager);
        svManager = StoredValueManager.build(c);
    }

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
        requestManager = RequestManager.build(c, dbManager);
        svManager = StoredValueManager.build(c);
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
     * Static constructor, creates a new ACT-R model object and returns it.
     *
     * @param c         The context for this model to operate within.
     * @param dbManager The database manager to use with this model.
     * @return The newly constructed ACTRModel object.
     */
    public static ACTRModel build(Context c, DatabaseManager dbManager)
    {
        return new ACTRModel(c, dbManager);
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
        String unitRegex = ".*[" + unit.replaceAll("\\s", "") + "].*";
        return (book == null || word.getBook().equals(book))
               && (chapter == null || word.getChapter().startsWith(chapter))
               && (unit == null || word.getChapter().matches(unitRegex));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject getNextWord(VocObject... ignoreWords)
    {
        return getNextWord(new HashSet<>(Arrays.asList(ignoreWords)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject getNextWord(Collection<VocObject> ignoreWords)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        VocObject min = null;
        VocObject newWord = null;

        for (VocObject word : interactions.keySet())
        {
            // If the word does not meet the requirements for presentation, ignore it
            if (!meetsRestrictions(word) || ignoreWords.contains(word))
                continue;
            if (word.getActivation() != null && word.getActivation() > 0)
                continue;

            // If the word has no activation value, then it is a new word.  Note the first new word
            // encountered, in case a new word needs to be presented.
            if (word.isNew())
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
            return newWord == null ? null : newWord;  // Redundant
        else if (min.getActivation() < ModelMath.THRESHOLD || newWord == null)
            return min;
        else
            return newWord;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject calculateNextWord(Date time, VocObject... ignoreWords)
    {
        return calculateNextWord(time, new HashSet<>(Arrays.asList(ignoreWords)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VocObject calculateNextWord(Date time, Collection<VocObject> ignoreWords)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalculateActivation(time);
        return getNextWord(ignoreWords);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateNextWordASync(Date time, WordSelectionListener listener,
                                       VocObject... ignoreWords)
    {
        calculateNextWordASync(time, listener, new HashSet<>(Arrays.asList(ignoreWords)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateNextWordASync(Date time, final WordSelectionListener listener,
                                       final Collection<VocObject> ignoreWords)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalculateActivationASync(time, new CalcListener()
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
        // Attempt to log in.
        // The log-in attempt occurs asynchronously, and initialization does not depend
        // on it or its success.

        // logIn(null);

        try
        {
            // .get() forces this thread to wait until the asynctask completes,
            // essentially making it synchronous.
            initTask(null).execute().get();
            initialized = true;
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
        // Attempt to log in.
        // The log-in attempt occurs asynchronously, and initialization does not depend
        // on it or its success.

        // logIn(null);

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
                cleanSessions();

                // Load all stored parameter values into ModelMath
                svManager.loadConstants();


                initialized = true;


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
    public void destroy()
    {
        SessionObject lastSession = sessions.get(sessions.size() - 1);
        if (!lastSession.isFinished())
        {
            lastSession.finish(new Date());
            dbManager.updateSession(lastSession);
        }
        dbManager.destroy();
        requestManager.destroy();
        interactions.clear();
        wordIdMap.clear();
        sessions.clear();

        initialized = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateActivation(Date time)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        try
        {
            // .get() forces this thread to wait until the asynctask completes,
            // essentially making it synchronous.
            recalcTask(time, null).execute().get();
        } catch (Exception e)
        {
            Log.e(LOG_TAG, "Error occurred while recalculating activation of items in model.", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateActivationASync(Date time, CalcListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        recalcTask(time, listener).execute();
    }


    /**
     * Constructs a new ASyncTask used to recalculate all activation values asynchronously,
     * and returns it.
     * This task will not yet have been executed.
     * <p>
     *
     * @param time     The time to use as the 'current' time when calculating activation.
     *                 If left null, uses the current time.
     * @param listener The listener object to call upon completion.  May be left null.
     * @return The constructed ASyncTask, which has not yet been executed.
     */
    private AsyncTask<Void, Void, Void> recalcTask(final Date time, final CalcListener listener)
    {
        // The meat occurs inside the doInBackground() method of this object
        return new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                Date eTime = time == null ? new Date() : time;

                for (Map.Entry<VocObject, List<InterxObject>> entry : interactions.entrySet())
                {
                    float activation = ModelMath.activation(eTime, entry.getKey(), entry.getValue(),
                                                            sessions, entry.getKey().getAlpha());
                    entry.getKey().setActivation(activation);

                    /*
                    ContentValues vals = new ContentValues();
                    vals.put(DBHandler.WORD_ACTIVATION, activation);
                    dbManager.getDatabase()
                             .update(DBHandler.WORD_TABLENAME, vals,
                                     DBHandler.WORD_ID + "=" + entry.getKey().getId(),
                                     null);
                                     */
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
    public float recalcSingleActivation(Date time, VocObject word, float alpha)
    {
        return ModelMath.activation(time, word, interactions.get(word),
                                    sessions, alpha);
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

                    /*
                    // Insert the interaction into the database and get it's new ID
                    long id = dbManager.updateInteraction(interx);
                    // Set the ID of the interaction
                    interx.setId(id);
                    */

                    if (interactions.get(word).size() != 1)
                    {
                        float newAlpha = ModelMath.newAlpha(interx, interactions.get(word), sessions);

                        word.setAlpha(newAlpha);
                        interx.setPostAlpha(newAlpha);
                    }
                    else
                        interx.setPostAlpha(interx.getPreAlpha());
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
    public void addNewSession(SessionObject session)
    {
        for (int i = 0; i < sessions.size(); i++)
        {
            SessionObject existingSession = sessions.get(i);
            if (existingSession.getStart().equals(session.getStart()))
            {
                Log.e(LOG_TAG, "Attempted to add session with starting time that matches an " +
                               "existing session.");
                return;
            }
        }

        sessions.add(session);

        cleanSessions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSessions()
    {
        cleanSessions();
        saveSessionsWithoutCleaning();
    }

    /**
     * FOR DEBUGGING ONLY
     */
    public void forceAddSessions(List<SessionObject> newSessions)
    {
        for (List<InterxObject> interxList : interactions.values())
            interxList.clear();
        sessions.clear();
        sessions.addAll(newSessions);
        saveSessionsWithoutCleaning();

        HashSet<VocObject> toRemove = new HashSet<>();
        for (VocObject word : interactions.keySet())
            if (!meetsRestrictions(word))
                toRemove.add(word);
        for (VocObject word : toRemove)
            interactions.remove(word);
    }

    /**
     * Saves all sessions into the local database without sanitizing the sessions first.  Use
     * with caution.
     */
    private void saveSessionsWithoutCleaning()
    {
        for (SessionObject sesh : sessions)
        {
            long id = dbManager.updateSession(sesh);
            sesh.setId(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToDatabase(boolean forceSaveAll)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        saveTask(forceSaveAll).run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToDatabaseASync(boolean forceSaveAll, CalcListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        performASync(saveTask(forceSaveAll), listener);
    }

    private Runnable saveTask(final boolean forceSaveAll)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                // Update all words and interactions

                // Mark the time to use as the update time upon completion
                Date updateTime = new Date();

                Log.d(LOG_TAG, "Saving database...");

                if (forceSaveAll)
                {
                    for (Map.Entry<VocObject, List<InterxObject>> entry : interactions.entrySet())
                    {
                        dbManager.updateWordData(entry.getKey());
                        for (InterxObject interx : entry.getValue())
                            dbManager.updateInteraction(interx);
                    }

                    // Update all sessions

                    for (SessionObject session : sessions)
                        dbManager.updateSession(session);
                }
                else
                {
                    Date lastSave = svManager.getLastDatabaseSave();

                    Map<SessionObject, List<InterxObject>> sessionMap =
                            getSessionsAfterDate(lastSave, false);

                    for (Map.Entry<SessionObject, List<InterxObject>> entry : sessionMap.entrySet())
                    {
                        entry.getKey().setId(dbManager.updateSession(entry.getKey()));
                        for (InterxObject interx : entry.getValue())
                            interx.setId(dbManager.updateInteraction(interx));
                    }

                    // Update all words

                    for (VocObject word : getWordsFromSessions(sessionMap))
                    {
                        dbManager.updateWordData(word);
                    }

                    // Store the last save date
                    svManager.storeLastDatabaseSave(updateTime);
                }

                if (user != null)
                    svManager.storeUserInfo(user); // Probably unnecessary

                svManager.saveConstants();

                Log.d(LOG_TAG, "Database saved.");
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
     * Attempts to log the user in.  If unsuccessful, the 'user' member variable will be set to
     * null.  Otherwise, it will contain the current login info for the user.
     *
     * @param listener Actions to perform upon completion.  May be left null.
     */
    private void logIn(final UserUpdateListener listener)
    {
        user = null;

        requestManager.logInUser(svManager.getUserInfo(), new UserUpdateListener()
        {
            @Override
            public void onSuccess(UserObject userInfo)
            {
                user = userInfo;
                svManager.storeUserInfo(user);

                if (listener != null)
                    listener.onSuccess(userInfo);
            }

            @Override
            public void onRemoteFailure(NetworkError error)
            {
                Log.e(LOG_TAG, "Error while logging user in.  User was not logged in.\n" +
                               error.toString());

                if (listener != null)
                    listener.onRemoteFailure(error);
            }

            @Override
            public void onLocalFailure(Throwable error)
            {
                Log.e(LOG_TAG, "Error while logging user in.  User was not logged in.\n" +
                               error.getMessage());

                if (listener != null)
                    listener.onLocalFailure(error);
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void getUnknownParseIDs(final ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        List<NetworkError> errorList = new ArrayList<>();

        final CountDownLatch latch = new CountDownLatch(1);

        getMissingWordParseIDs(errorList, latch);

        waitForActionsTask(latch, errorList, new ParseResponseListener()
        {
            @Override
            public void onResponse(NetworkError error)
            {
                if (error == null)
                {
                    saveToDatabaseASync(true, new CalcListener()
                    {
                        @Override
                        public void onCompletion()
                        {
                            if (listener != null)
                                listener.onResponse(null);
                        }
                    });
                }
                else if (listener != null)
                {
                    if (error.isNoWorkDoneError())
                        listener.onResponse(null);
                    else
                        listener.onResponse(error);
                }
            }
        }).execute();
    }


    /**
     * Finds all words that are still missing a Parse ID, and queries the remote database to
     * retrieve them.
     *
     * @param errors List of errors to keep track of
     * @param latch  Latch to decrement whenever the request completes
     */
    private void getMissingWordParseIDs(final List<NetworkError> errors,
                                        final CountDownLatch latch)
    {
        // Perform this operation asynchronously
        new AsyncTask<Void, Void, List<VocObject>>()
        {

            @Override
            protected List<VocObject> doInBackground(Void... params)
            {
                List<VocObject> wordsWithoutParseId = new ArrayList<>();

                // Find all words that are still missing Parse IDs
                for (VocObject word : interactions.keySet())
                    if (!word.hasParseId())
                        wordsWithoutParseId.add(word);

                Log.d(LOG_TAG, "Getting Parse ID for " + wordsWithoutParseId.size() + " items.");

                return wordsWithoutParseId;
            }

            @Override
            protected void onPostExecute(List<VocObject> wordsWithoutParseId)
            {
                super.onPostExecute(wordsWithoutParseId);

                if (wordsWithoutParseId.size() == 0)
                {
                    latch.countDown();
                    Log.d(LOG_TAG, "Skipping network retrieval for unknown Parse IDs.");
                    errors.add(NetworkError.buildNoWorkDoneError());
                    return;
                }

                requestManager.pushDictionaryItems(
                        wordsWithoutParseId,
                        buildLatchingWordListListener(latch, errors, null, null,
                                                      "Missing parse ID retrieval"));
            }
        }.execute();


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushToRemote(final ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        // Check that a user is logged in.  If not, log in and try again.
        if (user == null)
        {
            logIn(new UserUpdateListener()
            {
                @Override
                public void onSuccess(UserObject user)
                {
                    pushToRemote(listener);
                }

                @Override
                public void onRemoteFailure(NetworkError error)
                {
                    if (listener != null)
                        listener.onResponse(error);
                }

                @Override
                public void onLocalFailure(Throwable error)
                {
                    if (listener != null)
                        listener.onResponse(NetworkError.buildFromThrowable(error));
                }
            });
            return;
        }

        // If update is successful, this timestamp is used as the reference for
        // the last submission after completion
        final Date updateTime = new Date();

        // Create a list to hold all accumulated errors across all async tasks.
        // This *SHOULD* be threadsafe without having to specifically define it, as it
        // should only be modified on the UI thread, in theory.  This is something to keep an eye on
        // Reference = http://stackoverflow.com/questions/31119277/is-android-asynct-http-library-coupled-with-ui-thread
        List<NetworkError> errors = new ArrayList<>();

        // Start a latch to track completion of all asynchronous tasks
        final CountDownLatch latch = new CountDownLatch(3);

        // Filter out sessions that occurred entirely before the last submission time, as well
        // as sessions that are not yet completed.
        Date lastSubmission = svManager.getLastSubmission();

        // Get all sessions and interactions, filtered by the last submission time
        Map<SessionObject, List<InterxObject>> sessionInteractionMap =
                getSessionsAfterDate(lastSubmission, true);

        // Get all words referred to by all of the interactions to submit
        Collection<VocObject> wordsToUpdate = getWordsFromSessions(sessionInteractionMap);


        // Push all session data that has been updated since the last data submission
        requestManager.pushSessions(user, sessionInteractionMap,
                                    buildLatchingListener(latch, errors, null, null,
                                                          "Session submission"));
        // Push all user word data that has been updated since the last data submission
        requestManager.pushUserWordInfo(user, wordsToUpdate,
                                        buildLatchingWordListListener(latch, errors, null, null,
                                                                      "User word info submission"));
        // Push all user data that has been updated since the last data submission
        requestManager.pushUserInfo(user, buildLatchingListener(latch, errors, null, null,
                                                                "User info submission"));

        // Start an ASyncTask that will wait on the countdown latch, then report success or failure
        waitForActionsTask(latch, errors, new ParseResponseListener()
        {
            @Override
            public void onResponse(NetworkError error)
            {
                svManager.storeLastSubmission(updateTime);
                Log.d(LOG_TAG, "Successfully pushed all info, storing new last submission time: " +
                               DBHandler.ISO_DATE.format(updateTime));
                if (listener != null)
                    listener.onResponse(error);
            }
        }).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushToRemoteAndForget()
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        throw new UnsupportedOperationException("This has not yet been implemented.");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void pullFromRemote(final ParseResponseListener listener)
    {
        // Check that the model has been initialized, otherwise throw runtime exception
        if (!initialized)
            throw new ModelNotInitializedException();

        // Check that a user is logged in.  If not, log in and try again.
        if (user == null)
        {
            logIn(new UserUpdateListener()
            {
                @Override
                public void onSuccess(UserObject user)
                {
                    pullFromRemote(listener);
                }

                @Override
                public void onRemoteFailure(NetworkError error)
                {
                    listener.onResponse(error);
                }

                @Override
                public void onLocalFailure(Throwable error)
                {
                    listener.onResponse(NetworkError.buildFromThrowable(error));
                }
            });
            return;
        }

        // If update is successful, this timestamp is used as the reference for update after
        // update completion
        final Date updateTime = new Date();

        // Create a list to hold all accumulated errors across all async tasks.
        // This *SHOULD* be threadsafe without having to specifically define it, as it
        // should only be modified on the UI thread, in theory.  This is something to keep an eye on
        // Reference = http://stackoverflow.com/questions/31119277/is-android-asynct-http-library-coupled-with-ui-thread
        List<NetworkError> errors = new ArrayList<>();

        // Start a latch to track completion of all 4 asynchronous tasks
        final CountDownLatch latch = new CountDownLatch(4);

        // Construct a listener to use whenever the completion of a request should result in an
        // update of the model's collection of word data
        WordListSuccessListener wordListUpdate = new WordListSuccessListener()
        {
            @Override
            public void onSuccess(Collection<VocObject> words)
            {
                // Replace all words in the word ID map and interactions map with the new
                // word objects
                for (VocObject word : words)
                {
                    wordIdMap.put(word.getId(), word);

                    // REMINDER:  VocObject.equals(), and thus the Map.containsKey() check,
                    // is based SOLELY on the word's database ID
                    if (interactions.containsKey(word))
                        interactions.put(word, interactions.get(word));
                    else
                        interactions.put(word, new ArrayList<InterxObject>());
                }
            }
        };

        Date lastUpdate = svManager.getLastUpdate();

        // Get all dictionary items that have been modified since the last update
        requestManager.getDictionaryItems(lastUpdate,
                                          buildLatchingWordListListener(latch, errors,
                                                                        wordListUpdate, null,
                                                                        "Dictionary info retrieval"));

        // Get all model parameters that have been modified since the last update
        requestManager.updateConstants(lastUpdate,
                                       buildLatchingListener(latch, errors, new SuccessListener()
                                       {
                                           @Override
                                           public void onSuccess()
                                           {
                                               svManager.saveConstants();
                                           }
                                       }, null, "Parameter retrieval"));

        // Get all user info
        requestManager.updateUserInfo(user, buildLatchingListener(latch, errors, null, null,
                                                                  "User info retrieval"));

        // Get all user word info that has been modified since the last update
        requestManager.updateUserWordInfo(
                lastUpdate, user,
                buildLatchingWordListListener(latch, errors, wordListUpdate, null,
                                              "Dictionary info retrieval"));


        // Start an ASyncTask that will wait on the countdown latch, then report success or failure
        waitForActionsTask(latch, errors, new ParseResponseListener()
        {
            @Override
            public void onResponse(NetworkError error)
            {
                svManager.storeLastUpdate(updateTime);
                Log.d(LOG_TAG, "Successfully pulled all info, storing new last update time: " +
                               DBHandler.ISO_DATE.format(updateTime));
                if (listener != null)
                    listener.onResponse(error);
            }
        }).execute();
    }


    /**
     * Generates a map that links all sessions to the interactions that occurred within them.
     *
     * @return The generated map.
     */
    private Map<SessionObject, List<InterxObject>> mapInteractionsToSessions()
    {
        Map<SessionObject, List<InterxObject>> returnMap = new HashMap<>();

        for (SessionObject session : sessions)
            returnMap.put(session, new ArrayList<InterxObject>());

        for (List<InterxObject> interxList : interactions.values())
        {
            for (InterxObject interx : interxList)
            {
                // If the interaction is already associated with a particular session, simply use it
                if (interx.getSessionId() != null && interx.getSessionId() > 0)
                {
                    // As sessions are hashed based solely on their ID, create a placeholder
                    // session from the interaction's session ID to identify it in the map
                    returnMap.get(SessionObject.buildPlaceholder(interx.getSessionId())).add(interx);
                    continue;
                }

                // Otherwise, the session that this interaction belongs to must be identified

                // Iterate through all sessions and identify which one starts before the
                // interaction's timestamp and ends after it.  If no such session is found, the
                // interaction is not added to the map.
                for (SessionObject session : sessions)
                {
                    // !after() and !before() are here to serve as >= and <= rather than > and <
                    // Also, unfinished sessions are ignored
                    if (!session.getStart().after(interx.getTimestamp()) &&
                        (session.isFinished() &&
                         (!session.getEnd().before(interx.getTimestamp()))))
                    {
                        returnMap.get(session).add(interx);
                        interx.setSessionId(session.getId());
                    }
                }
            }
        }


        return returnMap;
    }

    /**
     * Constructs a map of sessions to its corresponding interactions, filtering out any sessions
     * that occurred before the given date.  If the given date is null, returns all sessions.  If
     * 'filterSessionsWithParseId' is true, sessions with stored Parse IDs will also be filtered out.
     * <p>
     * // TODO:  Maybe make filtered unfinished sessions optional too?
     *
     * @param date                      The date to filter sessions by.  May be left null.
     * @param filterSessionsWithParseId Whether or not to filter out sessions with stored Parse IDs
     * @return The map of sessions to corresponding interactions, filtered by the given date.
     */
    private Map<SessionObject, List<InterxObject>> getSessionsAfterDate(Date date,
                                                                        boolean filterSessionsWithParseId)
    {
        // Get a map of all sessions to all interactions
        Map<SessionObject, List<InterxObject>> sessionInteractionMap =
                mapInteractionsToSessions();

        List<SessionObject> sessionsToRemove = new ArrayList<>();
        for (SessionObject session : sessionInteractionMap.keySet())
        {
            // Filter session out if one of the following is true:
            // 1.)  We're filtering out sessions with Parse IDs, and the session has a Parse ID
            // 2.)  The session isn't finished
            // 3.)  We're not filtering out sessions with Parse IDs, and the session ends before
            //      the given date (if filtering out sessions with Parse IDs, date is ignored,
            //      meaning that finished sessions without Parse IDs will ALWAYS be submitted).

            if ((filterSessionsWithParseId &&
                 session.getParseId() != null
                 && !session.getParseId().equals(""))
                || !session.isFinished()
                || (!filterSessionsWithParseId && date != null && session.getEnd().before(date)))
            {
                sessionsToRemove.add(session);
            }
        }
        for (SessionObject session : sessionsToRemove)
            sessionInteractionMap.remove(session);

        return sessionInteractionMap;
    }

    /**
     * Cleans up the list of sessions, ensuring that all uses of the session list work as intended.
     * <p>
     * This method will: <br/>
     * 1.)  Sort sessions into chronological order <br/>
     * 2.)  Remove any non-final unfinished sessions <br/>
     * 3.)  Combine any overlapping sessions <br/>
     * 4.)  Identify any orphaned interactions, and create new sessions to accommodate them <br/>
     * 5.)  // TODO:  Clean up empty sessions
     * </p>
     */
    private void cleanSessions()
    {
        //  1.)  Sort sessions into chronological order
        Collections.sort(sessions, new Comparator<SessionObject>()
        {
            @Override
            public int compare(SessionObject o1, SessionObject o2)
            {
                return (o1.getStart().before(o2.getStart())) ? -1 : 1;
            }
        });


        // 2.)  Remove any non-final unfinished sessions
        // 3.)  Combine any overlapping sessions

        for (int i = 0; i < sessions.size() - 1; i++)
        {
            SessionObject sesh = sessions.get(i);
            SessionObject nextSesh = sessions.get(i + 1);

            if (!sesh.isFinished())
            {
                dbManager.deleteSession(sesh, null);

                sessions.remove(i);
                i--;
                Log.d(LOG_TAG, "Removed non-final unfinished session while cleaning sessions.");
                continue;
            }

            if (!sesh.getEnd().before(nextSesh.getStart()))
            {
                nextSesh.setStart(sesh.getStart());

                dbManager.deleteSession(sesh, nextSesh.getId());

                sessions.remove(i);
                i--;
                Log.d(LOG_TAG, "Combined overlapping sessions while cleaning sessions.");
            }
        }


        // 4a.)  Identify any orphaned interactions...
        List<InterxObject> orphanedInteractions = new ArrayList<>();
        for (List<InterxObject> interxList : interactions.values())
        {
            for (InterxObject interx : interxList)
            {
                if (interx.getSessionId() == null)
                {
                    orphanedInteractions.add(interx);
                }
            }
        }

        // If no interactions are orphaned, stop here
        if (orphanedInteractions.size() == 0)
            return;

        // Sort the orphaned interactions chronologically
        Collections.sort(orphanedInteractions, new Comparator<InterxObject>()
        {
            @Override
            public int compare(InterxObject o1, InterxObject o2)
            {
                return (o1.getTimestamp().before(o2.getTimestamp())) ? -1 : 1;
            }
        });

        //4b.) ...and create new sessions to accommodate them

        // For grouping interactions inbetween existing sessions
        Map<Date, List<InterxObject>> pseudoSessions = new HashMap<>();

        // Start the first pseudo-session
        Date currPseudoSessionStart = new Date(orphanedInteractions.get(0).getTimestamp().getTime());
        for (InterxObject interx : orphanedInteractions)
        {
            for (SessionObject session : sessions)
            {
                if (session.getStart().after(interx.getTimestamp()))
                    break;

                // If the date marking the start of the current pseudo-session is before the
                // start of this session, but the timestamp of this interaction is after the start
                // of this session, then we need to start a new pseudo-session.
                if (session.getStart().after(currPseudoSessionStart)
                    && session.getStart().before(interx.getTimestamp()))
                {
                    currPseudoSessionStart = new Date(interx.getTimestamp().getTime());
                    break;
                }
            }

            if (!pseudoSessions.containsKey(currPseudoSessionStart))
                pseudoSessions.put(currPseudoSessionStart, new ArrayList<InterxObject>());
            pseudoSessions.get(currPseudoSessionStart).add(interx);
        }

        // Create new sessions out of the pseudo-sessions, using the timestamp of the first
        // interaction as the start and the timestamp of the last interaction as the end
        for (Map.Entry<Date, List<InterxObject>> entry : pseudoSessions.entrySet())
        {
            Date endTime = new Date(entry.getValue().get(entry.getValue().size() - 1).getTimestamp().getTime() + 1);
            sessions.add(SessionObject.build(-1, entry.getKey(), endTime));
        }

        // Sort sessions chronologically again
        Collections.sort(sessions, new Comparator<SessionObject>()
        {
            @Override
            public int compare(SessionObject o1, SessionObject o2)
            {
                return (o1.getStart().before(o2.getStart())) ? -1 : 1;
            }
        });

    }

    /**
     * Collects a set of all words that are referred to by the interactions in the given
     * session/interaction map.
     *
     * @param sessionMap A map of sessions to all corresponding interactions
     * @return A collection of all words referred to by the given sessions/interactions
     */
    private Collection<VocObject> getWordsFromSessions(Map<SessionObject, List<InterxObject>> sessionMap)
    {
        Set<VocObject> wordsToUpdate = new HashSet<>();
        for (List<InterxObject> interxList : sessionMap.values())
        {
            for (InterxObject interx : interxList)
            {
                wordsToUpdate.add(interx.getWord() == null
                                  ? dbManager.getWordPairById(interx.getWordId())
                                  : interx.getWord());
            }
        }

        return wordsToUpdate;
    }

    /**
     * Defines an asynchronous task designed to wait on the given latch, and then respond to
     * any errors in the given list (or lack thereof) through the given listener.
     *
     * @param latch    The countdown latch to wait on
     * @param errors   List of accumulated errors
     * @param listener Actions to perform upon completion
     * @return The constructed ASyncTask, which will not have yet been executed.
     */
    private AsyncTask<Void, Void, Void> waitForActionsTask(final CountDownLatch latch,
                                                           final List<NetworkError> errors,
                                                           final ParseResponseListener listener)
    {
        return new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    latch.await();
                } catch (InterruptedException e)
                {
                    errors.add(NetworkError.buildFromThrowable(e));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                if (listener == null)
                    return;

                if (errors.size() == 0)
                    listener.onResponse(null);
                else if (errors.size() == 1)
                    listener.onResponse(errors.get(0));
                else
                    listener.onResponse(NetworkError.buildMultiError(errors));
            }
        };
    }


    /**
     * Takes a runnable task and performs it on another thread, reporting results through the
     * given listener.
     *
     * @param runnable The code to run asynchronously.
     * @param listener Actions to perform upon completion.
     */
    private void performASync(final Runnable runnable, final CalcListener listener)
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                runnable.run();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (listener != null)
                    listener.onCompletion();
            }
        }.execute();
    }

    /**
     * Builds a basic listener that handles errors through the given NetworkFailureListener,
     * passes success to the given SuccessListener, and in all cases decrements the
     * given latch and adds errors the given list.
     *
     * @param latch   The latch to decrement upon response, whether success or failure.
     * @param errors  A list to collect errors in.
     * @param success Additional actions to perform upon success.  May be left null.
     * @param failure Additional actions to perform upon failure.  May be left null.
     * @param action  String describing action being performed, for logging.
     * @return The newly constructed listener.
     */
    private GenericUpdateListener buildLatchingListener(final CountDownLatch latch,
                                                        final List<NetworkError> errors,
                                                        final SuccessListener success,
                                                        final NetworkFailureListener failure,
                                                        final String action)
    {
        return new GenericUpdateListener()
        {
            @Override
            public void onRemoteFailure(NetworkError error)
            {
                if (failure != null)
                    failure.onRemoteFailure(error);
                latch.countDown();


                Log.e(LOG_TAG, String.format("%s error: %s", action, error.toString()));
                errors.add(error);
            }

            @Override
            public void onLocalFailure(Throwable error)
            {
                if (failure != null)
                    failure.onLocalFailure(error);
                latch.countDown();

                Log.e(LOG_TAG, String.format("%s error.", action), error);
                errors.add(NetworkError.buildFromThrowable(error));
            }

            @Override
            public void onSuccess()
            {
                if (success != null)
                    success.onSuccess();
                latch.countDown();

                Log.v(LOG_TAG, String.format("%s successfully completed.", action));
            }
        };
    }

    /**
     * Builds a basic listener that handles errors through the given NetworkFailureListener,
     * passes success to the given WordListSuccessListener, and in all cases decrements the
     * given latch and adds errors the given list.
     *
     * @param latch   The latch to decrement upon response, whether success or failure.
     * @param errors  A list to collect errors in.
     * @param success Additional actions to perform upon success.  May be left null.
     * @param failure Additional actions to perform upon failure.  May be left null.
     * @param action  String describing action being performed, for logging.
     * @return The newly constructed listener.
     */
    private WordListUpdateListener buildLatchingWordListListener(final CountDownLatch latch,
                                                                 final List<NetworkError> errors,
                                                                 final WordListSuccessListener success,
                                                                 final NetworkFailureListener failure,
                                                                 final String action)
    {
        return new WordListUpdateListener()
        {
            @Override
            public void onRemoteFailure(NetworkError error)
            {
                if (failure != null)
                    failure.onRemoteFailure(error);
                latch.countDown();


                Log.e(LOG_TAG, String.format("%s error: %s", action, error.toString()));
                errors.add(error);
            }

            @Override
            public void onLocalFailure(Throwable error)
            {
                if (failure != null)
                    failure.onLocalFailure(error);
                latch.countDown();

                Log.e(LOG_TAG, String.format("%s error.", action), error);
                errors.add(NetworkError.buildFromThrowable(error));
            }

            @Override
            public void onSuccess(Collection<VocObject> words)
            {
                if (success != null)
                    success.onSuccess(words);
                latch.countDown();

                Log.v(LOG_TAG, String.format("%s successfully completed.", action));
            }
        };
    }
}
