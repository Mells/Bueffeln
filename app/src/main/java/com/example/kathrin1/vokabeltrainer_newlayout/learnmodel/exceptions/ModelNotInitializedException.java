package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.exceptions;

/**
 * Reports when a method was called on the model before it was initialized
 */
public class ModelNotInitializedException extends ModelException
{
    private static final String DEFAULT_MESSAGE =
            "Model must be initialized before usage.  " +
            "See LearnModel.initialize() and LearnModel.initializeASync().";

    public ModelNotInitializedException()
    {
        super(DEFAULT_MESSAGE);
    }

    public ModelNotInitializedException(String message)
    {
        super(message);
    }
}
