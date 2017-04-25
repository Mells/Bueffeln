package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Describes the accessible methods of a learning model, to make things clear and allow for
 * multiple possible model implementations
 */
public interface LearnModel
{

    /**
     * Sets the restrictions for which words to present, based on the currently selected
     * book, chapter, and unit of material.  Any field may be left null in order to ignore
     * that field.  By default before calling this method, there are no restrictions.
     *
     * @param book    The book to restrict to.
     * @param chapter The chapter to restrict to.
     * @param unit    The unit to restrict to.
     */
    void setRestrictions(String book, String chapter, String unit);

    /**
     * Allows the model to choose the next word to present based on the current state of the model.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This does NOT force a recalculation of activation values, and should only be used after
     * doing so for the selection to be meaningful.
     *
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @return The word most optimal to present, according to the model.
     */
    VocObject getNextWord(Collection<VocObject> ignoreWords);

    /**
     * Allows the model to choose the next word to present based on the current state of the model.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This does NOT force a recalculation of activation values, and should only be used after
     * doing so for the selection to be meaningful.
     *
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @return The word most optimal to present, according to the model.
     */
    VocObject getNextWord(VocObject... ignoreWords);


    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * returning the chosen word.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete. Their
     * activation values will still be computed, but they cannot be selected.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#calculateNextWordASync(Date, WordSelectionListener,
     * VocObject...)} instead.
     *
     * @param time        The time to use as the 'current' time when calculating activation.
     *                    If left null, uses the current time.
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @return The word most optimal to present, according to the model.
     */
    VocObject calculateNextWord(Date time, VocObject... ignoreWords);

    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * returning the chosen word.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete. Their
     * activation values will still be computed, but they cannot be selected.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#calculateNextWordASync(Date, WordSelectionListener,
     * Collection)} instead.
     *
     * @param time        The time to use as the 'current' time when calculating activation.
     *                    If left null, uses the current time.
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @return The word most optimal to present, according to the model.
     */
    VocObject calculateNextWord(Date time, Collection<VocObject> ignoreWords);

    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * calling the {@link WordSelectionListener#onSelection(VocObject)} method of the given
     * listener with the chosen word as an argument.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete.  Their
     * activation values will still be computed, but they cannot be selected.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param time        The time to use as the 'current' time when calculating activation.
     *                    If left null, uses the current time.
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @param listener    Listener that is invoked upon completion.  May be left null.
     */
    void calculateNextWordASync(Date time, WordSelectionListener listener, VocObject... ignoreWords);

    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * calling the {@link WordSelectionListener#onSelection(VocObject)} method of the given
     * listener with the chosen word as an argument.
     * <p>
     * The given words are ignored and will not be selected.  This is to allow moving onto the
     * next word without having to wait for the post-interaction computation to complete.  Their
     * activation values will still be computed, but they cannot be selected.  This
     * argument is entirely optional.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param time        The time to use as the 'current' time when calculating activation.
     *                    If left null, uses the current time.
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @param listener    Listener that is invoked upon completion.  May be left null.
     */
    void calculateNextWordASync(Date time, WordSelectionListener listener,
                                Collection<VocObject> ignoreWords);

    /**
     * Initializes the model, extracting word data from the local database and setting up the
     * model for future use.
     * <p>
     * <br/><br/>
     * This method forces a full load of all word and word interaction data, and does so
     * SYNCHRONOUSLY.  If this retrieval take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#initializeASync(CalcListener)} instead.
     */
    void initialize();


    /**
     * Initializes the model, extracting word data from the local database and setting up the
     * model for future use.
     * Upon completion, calls the {@link CalcListener#onCompletion()} method of the given listener.
     * <p>
     * <br/><br/>
     * This method forces a full load of all word and word interaction data, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void initializeASync(CalcListener listener);

    /**
     * Returns whether the model is currently initialized or not.  If this method returns true, that
     * means any calls to model functions would result in a
     * {@link com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.exceptions.ModelNotInitializedException}.
     *
     * @return Returns true if the model has been initialized, false otherwise.
     */
    boolean isInitialized();

    /**
     * Destroys the model, freeing all resources and closing all open connections.
     */
    void destroy();

    /**
     * Recalculates the activation value for all words.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#recalculateActivationASync(Date, CalcListener)} instead.
     *
     * @param time The time to use as the 'current' time when calculating activation.
     *             If left null, uses the current time.
     */
    void recalculateActivation(Date time);


    /**
     * Recalculates the activation value for all words, calling the
     * {@link CalcListener#onCompletion()} method of the given listener upon completion.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param time     The time to use as the 'current' time when calculating activation, plus
     *                 the default lookahead time.
     *                 If left null, uses the current time.
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void recalculateActivationASync(Date time, CalcListener listener);


    /**
     * Recalculates the activation value for a single word at a moment of the given time, plus
     * the default lookahead time.  This does NOT set the activation value for the word, simply
     * returns the value.
     *
     * @param time  The time to recalculate activation at
     * @param word  The word to recalculate activation for
     * @param alpha The alpha to use for the purposes of recalculation
     * @return The calculated activation value
     */
    float recalcSingleActivation(Date time, VocObject word, float alpha);

    /**
     * Adds the given interaction to the model and the database, and uses it to adjust the
     * alpha value of the presented word.
     * This method will also set the 'postAlpha' value of the given interaction with the newly
     * calculated alpha.
     * <p>
     * <br/><br/>
     * This method performs a complete recalculation of an item's optimal alpha, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#addNewInteractionASync(InterxObject, CalcListener)} instead.
     *
     * @param interx The interaction to add.
     */
    void addNewInteraction(InterxObject interx);

    /**
     * Adds the given interaction to the model and the database, and uses it to adjust the
     * alpha value of the presented word, calling the
     * {@link CalcListener#onCompletion()} method of the given listener upon completion.
     * This method will also set the 'postAlpha' value of the given interaction with the newly
     * calculated alpha.
     * <p>
     * <br/><br/>
     * This method performs a complete recalculation of an item's optimal alpha, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param interx   The interaction to add.
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void addNewInteractionASync(InterxObject interx, CalcListener listener);


    /**
     * Adds the given session to the model.  This session is not written to the database until it
     * is considered finished and {@link LearnModel#saveToDatabase(boolean)},
     * {@link LearnModel#saveToDatabaseASync(boolean, CalcListener)}, or
     * {@link LearnModel#saveSessions()} is called to commit it.
     *
     * @param session The session to add to the model
     */
    void addNewSession(SessionObject session);

    /**
     * Saves all sessions in this model into the local database.  Sanitizes all sessions first to
     * ensure the sessions are sensible and properly formatted.
     */
    void saveSessions();

    /**
     * Saves all values currently tracked by the model into the LOCAL database.
     * <p>
     * <br/><br/>
     * This method operates SYNCHRONOUSLY.  If the operations take very long, this will lag the
     * UI thread.  In such an instance, use {@link LearnModel#saveToDatabaseASync(boolean, CalcListener)} instead.
     *
     * @param forceSaveAll If true, forces a full save of all database items.  Otherwise, only saves
     *                     items related to unsaved sessions.
     */
    void saveToDatabase(boolean forceSaveAll);

    /**
     * Saves all values currently tracked by the model into the LOCAL database.
     * <p>
     * <br/><br/>
     * This method operates ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the
     * UI to update whenever the calculation completes.
     *
     * @param forceSaveAll If true, forces a full save of all database items.  Otherwise, only saves
     *                     items related to unsaved sessions.
     * @param listener     Actions to perform upon completion
     */
    void saveToDatabaseASync(boolean forceSaveAll, CalcListener listener);


    /**
     * Gets all words tracked by the model.
     */
    List<VocObject> getAllWords();

    /**
     * Identifies all words in the database that do not have a Parse ID, submits them to the Parse
     * database, receiving their Parse IDs as a response, storing them in the local database.
     * Calls the {@link ParseResponseListener#onResponse(NetworkError)} method of the given listener
     * upon completion.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void getUnknownParseIDs(ParseResponseListener listener);

    /**
     * Pushes all local data to the remote database.
     * Calls the {@link ParseResponseListener#onResponse(NetworkError)} method of the given listener
     * upon completion.
     * <p>
     * <p>
     * In addition, a timestamp is stored of the time of the last successful data submission,
     * which is used to pre-populate the list of objects to update whenever the model is next
     * initialized.
     * </p>
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void pushToRemote(ParseResponseListener listener);

    /**
     * Pushes all local data to the remote database whenever it is most convenient for the device
     * to do so.  This occurs at an indeterminate point in the future, and there is no callback
     * to report success or failure.
     * <p>
     * <p>
     * A timestamp is stored of the time of the last successful data submission, which is used to
     * pre-populate the list of objects to update whenever the model is next initialized.
     * </p>
     */
    void pushToRemoteAndForget();

    /**
     * Pulls all data from the remote database into the local database.
     * Calls the {@link ParseResponseListener#onResponse(NetworkError)} method of the given listener
     * upon completion.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void pullFromRemote(ParseResponseListener listener);




    /*
     * LISTENER INTERFACES
     * =====================================================================
     */


    /**
     * Listener used for indicating when an asynchronous calculation completes.
     */
    interface CalcListener
    {
        /**
         * Called when the asynchronous calculation completes.
         */
        void onCompletion();
    }

    /**
     * Listener used for indicating when an asynchronous word-selection operation completes.
     */
    interface WordSelectionListener
    {
        /**
         * Called when the asynchronous word-selection completes, giving the selected word
         * as an argument.
         *
         * @param vocObject The selected word.
         */
        void onSelection(VocObject vocObject);
    }

    /**
     * Listener used for indicating when an interaction with the Parse server completes.
     */
    interface ParseResponseListener
    {
        /**
         * Called when an interaction with the Parse server completes.  If an error occurred,
         * then the given {@link NetworkError} argument will describe the error.  Otherwise,
         * the argument will be null.
         *
         * @param error Description of an error that occurred while communicating with the Parse
         *              server, or null if no such error occurred.
         */
        void onResponse(NetworkError error);
    }


}
