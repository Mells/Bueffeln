package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.exceptions;

/**
 * General class for handling errors with the model
 */
public class ModelException extends RuntimeException
{
    private static final String DEFAULT_MESSAGE = "Error occurred while handling the learning model.";

    public ModelException()
    {
        super(DEFAULT_MESSAGE);
    }

    public ModelException(String message)
    {
        super(message);
    }
}
