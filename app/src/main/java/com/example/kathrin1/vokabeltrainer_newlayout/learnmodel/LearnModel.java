package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

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
     * @param book The book to restrict to.
     * @param chapter The chapter to restrict to.
     * @param unit The unit to restrict to.
     */
    void setRestrictions(String book, String chapter, String unit);

    /**
     * Allows the model to choose the next word to present based on the current state of the model.
     *
     * <br/><br/>
     * This does NOT force a recalculation of activation values, and should only be used after
     * doing so for the selection to be meaningful.
     *
     * @return The word most optimal to present, according to the model.
     */
    VocObject getNextWord();


    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * returning the chosen word.
     *
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#calculateNextWordASync(WordSelectionListener)} instead.
     *
     * @return The word most optimal to present, according to the model.
     */
    VocObject calculateNextWord();

    /**
     * Recalculates all activation values, and allows the model to choose the next word to present,
     * calling the {@link WordSelectionListener#onSelection(VocObject)} method of the given
     * listener with the chosen word as an argument.
     *
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
     void calculateNextWordASync(WordSelectionListener listener);

    /**
     * Initializes the model, extracting word data from the local database and setting up the
     * model for future use.
     *
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
     *
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
     *
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * SYNCHRONOUSLY.  If the calculations take very long, this will lag the UI thread.  In such
     * an instance, use {@link LearnModel#recalculateActivationASync(CalcListener)} instead.
     */
    void recalculateActivation();


    /**
     * Recalculates the activation value for all words, calling the
     * {@link CalcListener#onCompletion()} method of the given listener upon completion.
     *
     * <br/><br/>
     * This method forces a full recalculation of all activation values, and does so
     * ASYNCHRONOUSLY.  This will not hold up the UI thread, allowing the UI to update whenever
     * the calculation completes.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void recalculateActivationASync(CalcListener listener);

    /**
     * Gets all words tracked by the model.
     */
    List<VocObject> getAllWords();

    /**
     * Identifies all words in the database that do not have a Parse ID, submits them to the Parse
     * database, receiving their Parse IDs as a response, storing them in the local database.
     * Calls the {@link ParseResponseListener#onResponse(ParseError)} method of the given listener
     * upon completion.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void getUnknownParseIDs(ParseResponseListener listener);

    /**
     * Pushes all local data to the remote database.
     * Calls the {@link ParseResponseListener#onResponse(ParseError)} method of the given listener
     * upon completion.
     *
     * @param listener Listener that is invoked upon completion.  May be left null.
     */
    void pushToRemote(ParseResponseListener listener);

    /**
     * Pulls all data from the remote database into the local database.
     * Calls the {@link ParseResponseListener#onResponse(ParseError)} method of the given listener
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
         * then the given {@link ParseError} argument will describe the error.  Otherwise,
         * the argument will be null.
         *
         * @param error Description of an error that occurred while communicating with the Parse
         *              server, or null if no such error occurred.
         */
        void onResponse(ParseError error);
    }

}
