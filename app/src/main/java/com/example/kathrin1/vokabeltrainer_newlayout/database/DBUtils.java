package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for housing static utility methods relating to accessing and manipulating database values.
 */
public abstract class DBUtils
{
    /**
     * Creates a string array out of the given string which is formatted as a comma-separated,
     * bracket-enclosed list of elements.
     * // TODO:  Right now, this is very coarse.  The power of quotes is ignored, and so all spaces,
     * // TODO:  brackets, commas, and apostrophes are removed whether or not they are quoted or escaped.
     * // TODO:  This should eventually be improved... feel free
     *
     * @param list The formatted string containing list values
     * @return An array containing the listed values
     */
    public static String[] splitListString(String list)
    {
        // Removes brackets, spaces, and apostrophes
        list = list.replaceAll("[\\[\\]\\s']", "");

        return list.split(",");
    }

    /**
     * Takes the given string which is formatted for describing tags and generates a map
     * // TODO:  Better description here.. don't fully understand what the tags are about
     *
     * @param sentence The string to extract tags from
     * @return The map of tags
     */
    public static Map<String, List<String>> stringOfTagsToMap(String sentence){
        String punctuations = ".,:;?";
        Map<String, List<String>> smap = new HashMap<>();
        // {'car': ['car'], 'A': ['A'], '.': ['.']}
        String[] pma = sentence.replaceAll("\\]\\}|[\\{\\[\\s']","").split("],");
        // car:car,car    -   A:A    -   .:.
        //Log.d("errorSent", sentence);
        //Log.d("errorPma", Arrays.toString(pma));
        for (String item : pma){
            // ,: ,, ,, ,, ,]
            String[] keyVal = item.split(":");
            if (keyVal.length != 2 || punctuations.contains(keyVal[0])){
                continue;
            }
            //Log.d("error", Arrays.toString(keyVal));
            //Log.d("error", keyVal[0]+" - "+keyVal[1]);
            smap.put(keyVal[0], Arrays.asList(keyVal[1].split(",")));
        }

        Log.d("The map", smap.toString());

        return smap;
    }


}
