package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.opencsv.CSVWriter;

import java.io.IOException;

/**
 * For holding information about a simulation interaction
 */
public class SimInterxObject
{
    private final InterxObject interx;
    private final float targetAlpha;

    /**
     * Private constructor, use static constructor instead.
     */
    private SimInterxObject(InterxObject interx, float targetAlpha)
    {
        this.interx = interx;
        this.targetAlpha = targetAlpha;
    }

    /**
     * Static constructor, creates a new sim interx object object and returns it.
     *
     * @return The newly constructed SimInterxObject object.
     */
    public static SimInterxObject build(InterxObject interx, float targetAlpha)
    {
        return new SimInterxObject(interx, targetAlpha);
    }

    public void writeToCSV(CSVWriter writer) throws IOException
    {
        String[] line = new String[]{interx.getWord().getLabel(),
                                     interx.getWord().getVoc(),
                                     Float.toString(interx.getPreActivation()),
                                     Float.toString(interx.getWord().getSigma()),
                                     DBHandler.ISO_DATE.format(interx.getTimestamp()),
                                     interx.getExerciseType(),
                                     Integer.toString(interx.getCharCount()),
                                     Integer.toString(interx.getLatency()),
                                     interx.getResult(),
                                     Float.toString(interx.getPreAlpha()),
                                     Float.toString(interx.getPostAlpha()),
                                     Float.toString(targetAlpha)};

        writer.writeNext(line);
    }
}
