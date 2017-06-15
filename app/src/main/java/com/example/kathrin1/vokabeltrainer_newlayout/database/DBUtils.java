package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for housing static utility methods relating to accessing and manipulating database values.
 */
public abstract class DBUtils
{

    public static final String LOG_TAG = "[DBUtils]";

    /**
     * Creates a string array out of the given string which is formatted as a comma-separated,
     * bracket-enclosed list of elements.  This method ignores commas inside quotes, and also
     * handles escaped characters.  Any brackets/parentheses/braces encountered outside of the
     * first and last characters are treated literally.  Also, all whitespace outside of quotes
     * is ignored.
     *
     * This is probably totally overkill for what we need, but oh well.
     *
     * @param list The formatted string containing list values
     * @return An array containing the listed values
     */
    public static List<String> splitListString(String list)
    {
        List<String> split = new ArrayList<>();

        char quotingChar = '"'; // This changes between single and double quotes as needed
        boolean inQuotes = false; // Whether or not we're in quoting mode

        // The string builder for holding the strings we build as while iterating through input
        StringBuilder builder = new StringBuilder();

        // Ignore opening and closing brackets
        int start = list.startsWith("[") ? 1 : 0;
        int end = list.endsWith("]") ? list.length() - 1 : list.length();

        // Iterate through all characters in the string
        for (int i = start; i < end; i++)
        {
            // Grab the character at the index
            char c = list.charAt(i);

            // If this character is a backslash, we must escape the following character
            if (c == '\\')
            {
                // If there is no next character, break here and do nothing with the backslash
                if (i + 1 >= end)
                    break;

                // Grab the next character and build a string with a literal backslash from it
                char next = list.charAt(i + 1);
                String escapeString = "\\" + next;

                // If the current character is a 'u', then this is a unicode codepoint.
                // Grab the next 4 characters as well.
                if (next == 'u')
                {
                    escapeString += list.substring(i + 2, Math.min(i + 6, list.length()));
                    i += 4;
                }
                // Convert the string with the literal backslash into the proper escaped
                // unicode character, and add it to the string builder
                builder.append(StringEscapeUtils.escapeJava(escapeString));

                // Advance the index an additional time to account for the escaped character
                i++;

                // Skip to the next iteration
                continue;
            }

            // If we're currently inside quotes
            if (inQuotes)
            {
                // If we find the matching closing quote, stop quoting
                if (c == quotingChar)
                    inQuotes = false;

                    // Otherwise, add the character to the string builder
                else
                    builder.append(c);
            }
            // If we're not currently inside quotes
            else
            {
                // If we find a quoting character, enter quoting mode and note which
                // quote character was used
                if (c == '\'' || c == '"')
                {
                    inQuotes = true;
                    quotingChar = c;
                }
                // If we find a comma, add the contents of the string builder as a new entry in the
                // list and reset the string builder
                else if (c == ',')
                {
                    split.add(builder.toString());
                    builder = new StringBuilder();
                }
                // Otherwise, add the character to the string builder if it is not whitespace
                else if (!Character.isWhitespace(c))
                    builder.append(c);

            }
        }
        // Add the last element to the list (since there is no final comma)
        split.add(builder.toString());

        // Return the assembled list
        return split;
    }

    public static List<String> splitJSONListString(String listString)
    {
        List<String> split = new ArrayList<>();

        try {
            JSONArray jArr = new JSONArray(listString);

            for (int i=0; i < jArr.length(); i++)
            {
                Object elem = jArr.get(i);
                if (elem instanceof JSONArray)
                    split.addAll(splitJSONListString(jArr.getString(i)));
                else
                    split.add(jArr.getString(i));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing list string as JSON:  " + e.getMessage());
        }


        return split;
    }



    /**
     * Takes the given string which is formatted for describing tags and generates a map
     * // TODO:  Better description here.. don't fully understand what the tags are about
     *
     * @param sentence The string to extract tags from
     * @return The map of tags
     */
    public static Map<String, List<String>> stringOfTagsToMap(String sentence)
    {
        String punctuations = ".,:;?";
        Map<String, List<String>> smap = new HashMap<>();
        // {'car': ['car'], 'A': ['A'], '.': ['.']}
        String[] pma = sentence.replaceAll("\\]\\}|[\\{\\[\\s']", "").split("],");
        // car:car,car    -   A:A    -   .:.
        //Log.d("errorSent", sentence);
        //Log.d("errorPma", Arrays.toString(pma));
        for (String item : pma)
        {
            // ,: ,, ,, ,, ,]
            String[] keyVal = item.split(":");
            if (keyVal.length != 2 || punctuations.contains(keyVal[0]))
            {
                continue;
            }
            //Log.d("error", Arrays.toString(keyVal));
            //Log.d("error", keyVal[0]+" - "+keyVal[1]);
            smap.put(keyVal[0], Arrays.asList(keyVal[1].split(",")));
        }

        return smap;
    }

    public static Map<String, List<String>> stringOfJSONTagsToMap(String jsonString)
    {
        Map<String, List<String>> smap = new HashMap<>();

        try {
            JSONObject jObj = new JSONObject(jsonString);

            Iterator<?> keys = jObj.keys();

            while (keys.hasNext())
            {
                String key = (String)keys.next();
                if (jObj.get(key) instanceof JSONArray)
                {
                    JSONArray jArr = jObj.getJSONArray(key);
                    List<String> tagList = new ArrayList<>();

                    for (int i=0; i < jArr.length(); i++)
                        tagList.add(jArr.getString(i));


                    smap.put(key.toLowerCase(), tagList);
                }
                else
                {
                    Log.e(LOG_TAG, String.format("Malformed JSON tag string:  Key '%s' does not" +
                                                 " have an array value (%s).",
                                                 key, jObj.getString(key)));
                }
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Error parsing map string as JSON:  " + e.getMessage());
        }

        return smap;
    }
}
