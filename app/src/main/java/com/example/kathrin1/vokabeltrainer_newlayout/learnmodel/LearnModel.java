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
     * Allows the model to choose the next word to present based on the model's current state,
     * returning the chosen word.
     *
     * @return The word most optimal to present, according to the model.
     */
    VocObject getNextWord();

    /**
     * Initializes the model, extracting word data from the local database and recalculating
     * activation for all words.
     */
    void initialize();

    /**
     * Recalculates the activation value for all words.
     */
    void recalculateActivation();

    /**
     * Gets all words tracked by the model.
     */
    List<VocObject> getAllWords();

    /**
     * Pushes all local data to the remote database.
     *
     * @param onPostExecute Code to run after the asynchronous task completes.
     */
    void pushToRemote(Runnable onPostExecute);

    /**
     * Pulls all data from the remote database into the local database.
     *
     * @param onPostExecute Code to run after the asynchronous task completes.
     */
    void pullFromRemote(Runnable onPostExecute);


}
