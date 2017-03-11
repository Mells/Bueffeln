package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.Context;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.List;

/**
 * Implementation of a learning model using the ACT-R learn model system
 */
public class ACTRModel implements LearnModel
{

    private Context c;

    /**
     * Private constructor, use static constructor instead.
     */
    private ACTRModel()
    {

    }

    /**
     * Static constructor, creates a new ACT-R model object and returns it.
     *
     * @return  The newly constructed ACTRModel object.
     */
    public static ACTRModel build()
    {
        return new ACTRModel();
    }



    @Override
    public VocObject getNextWord()
    {
        return null;
    }

    @Override
    public void initialize()
    {

    }

    @Override
    public void recalculateActivation()
    {

    }

    @Override
    public List<VocObject> getAllWords()
    {
        return null;
    }

    @Override
    public void pushToRemote(Runnable onPostExecute)
    {

    }

    @Override
    public void pullFromRemote(Runnable onPostExecute)
    {

    }
}
