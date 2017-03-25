package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

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
     * an instance, use {@link LearnModel#calculateNextWordASync(WordSelectionListener,
     * VocObject...)} instead.
     *
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @return The word most optimal to present, according to the model.
     */
    VocObject calculateNextWord(VocObject... ignoreWords);

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
     * @param ignoreWords Words to ignore (not select), even if they are optimal.  Optional.
     * @param listener    Listener that is invoked upon completion.  May be left null.
     */
    void calculateNextWordASync(WordSelectionListener listener, VocObject... ignoreWords);

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
     * Recalculates the activation value for all words.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#recalculateActivationASync(CalcListener)} instead.
     */
    void recalculateActivation();


    /**
     * Recalculates the activation value for all words, calling the
     * {@link CalcListener#onCompletion()} method of the given listener upon completion.
     * <p>
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void recalculateActivationASync(CalcListener listener);

    /**
     * Adds the given interaction to the model and the database, and uses it to adjust the
     * alpha value of the presented word.
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
     * is considered finished and {@link LearnModel#saveToDatabase()} or
     * {@link LearnModel#saveToDatabaseASync(CalcListener)} is called to commit it.
     *
     * @param session The session to add to the model
     */
    void addNewSession(SessionObject session);

    /**
     * Saves all values currently tracked by the model into the LOCAL database.
     * <p>
     * <br/><br/>
     * This method operates SYNCHRONOUSLY.  If the operations take very long, this will lag the
     * UI thread.  In such an instance, use {@link LearnModel#saveToDatabaseASync(CalcListener)} instead.
     */
    void saveToDatabase();

    /**
     * Saves all values currently tracked by the model into the LOCAL database.
     * <p>
     * <br/><br/>
     * This method operates ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the
     * UI to update whenever the calculation completes.
     */
    void saveToDatabaseASync(CalcListener listener);


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
     *
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
     *
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
