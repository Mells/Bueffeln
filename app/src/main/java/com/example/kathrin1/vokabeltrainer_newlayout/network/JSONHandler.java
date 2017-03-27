package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.UserObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * For converting received JSON into Java objects, and vice versa.
 */

public abstract class JSONHandler
{
    public static final String OBJECT_WORD = "Word";
    public static final String OBJECT_UWINFO = "UserWordInfo";
    public static final String OBJECT_SESSION = "StudySession";
    public static final String OBJECT_INTERX = "WordPresentation";
    public static final String OBJECT_USER = "_User";

    public static final String FIELD_OBJECTID = "objectId";

    public static final String FIELD_WORD_ALPHA_D = "alpha_d";
    public static final String FIELD_WORD_WORD = "word";
    public static final String FIELD_WORD_SIGMA = "sigma_i";
    public static final String FIELD_WORD_LABEL = "label";
    public static final String FIELD_WORD_BETA_I = "beta_i";
    public static final String FIELD_WORD_STORE_ARRAY = "words";

    public static final String FIELD_UWINFO_WORDID = "wordId";
    public static final String FIELD_UWINFO_ALPHA = "alpha_i";
    public static final String FIELD_UWINFO_WORD = "word";
    public static final String FIELD_UWINFO_BETA_SI = "beta_s_i";

    public static final String FIELD_SESSION_START = "start";
    public static final String FIELD_SESSION_END = "end";
    public static final String FIELD_SESSION_PRESENTATIONS = "presentations";

    public static final String FIELD_INTERX_WORDID = "wordId";
    public static final String FIELD_INTERX_WORD = "word";
    public static final String FIELD_INTERX_EXERCISE = "exerciseType";
    public static final String FIELD_INTERX_CHARCOUNT = "charCount";
    public static final String FIELD_INTERX_TIMESTAMP = "timestamp";
    public static final String FIELD_INTERX_RESULT = "result";
    public static final String FIELD_INTERX_LATENCY = "latency";

    public static final String FIELD_USER_BETA_S = "beta_s";
    public static final String FIELD_USER_ANONID = "anonId";
    public static final String FIELD_USER_SESSION_TOKEN = "sessionToken";

    public static final String FIELD_CONST_NAME = "name";
    public static final String FIELD_CONST_VALUE = "value";

    public static final String CONST_THRESHOLD = "tau";
    public static final String CONST_ALPHA_D = "alpha_default";
    public static final String CONST_DECAY_SCALAR = "c";
    public static final String CONST_RT_SCALAR = "F";
    public static final String CONST_RECALL_NOISE = "s";
    public static final String CONST_TRAIN_FACTOR = "b_j_train";
    public static final String CONST_TEST_FACTOR = "b_j_test";
    public static final String CONST_LOOKAHEAD = "t_lookahead";
    public static final String CONST_PSYCH_TIME = "psychtime_mult";
    public static final String CONST_CHAR_SCALAR = "f_char_mult";
    public static final String CONST_CHAR_INTERCEPT = "f_char_add";
    public static final String CONST_RT_MIN = "f_min";
    public static final String CONST_BETA_S = "beta_s";
    public static final String CONST_ALPHA_ITERS = "alpha_convergence_iters";
    public static final String CONST_RT_MAX_MULT = "rt_max_mult";


    public static final String LOG_TAG = "[JSONHandler]";
    public static final String FIELD_RESULTS = "results";


    /**
     * Constructs a new JSON object representing the parts of the given word object that pertain
     * to a particular user
     *
     * @param word The word to convert into a JSON object
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getUserWordInfoJSON(VocObject word) throws JSONException
    {
        JSONObject jObj = new JSONObject();

        if (!word.getUserInfoParseId().equals(""))
            jObj.put(FIELD_OBJECTID, word.getUserInfoParseId());

        if (word.getParseId() != null && !word.getParseId().equals(""))
            jObj.put(FIELD_UWINFO_WORDID, word.getParseId());

        jObj.put(FIELD_UWINFO_WORD, word.getVoc());
        // TODO:  Is this negative infinity check necessary?  What's really going on here?
        jObj.put(FIELD_UWINFO_ALPHA, word.getAlpha() == Float.NEGATIVE_INFINITY
                                     ? ModelMath.ALPHA_DEFAULT : word.getAlpha());
        jObj.put(FIELD_UWINFO_BETA_SI, word.getBeta_si());

        return jObj;
    }

    /**
     * Constructs a new JSON object representing the parts of the given word object that are
     * independent from individual users
     *
     * @param word The word to convert into a JSON object
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getWordInfoJSON(VocObject word) throws JSONException
    {
        JSONObject jObj = new JSONObject();

        if (word.getParseId() != null && !word.getParseId().equals(""))
            jObj.put(FIELD_OBJECTID, word.getParseId());

        jObj.put(FIELD_WORD_WORD, word.getVoc());
        jObj.put(FIELD_WORD_SIGMA, word.getSigma());
        jObj.put(FIELD_WORD_LABEL, word.getLabel());
        jObj.put(FIELD_WORD_BETA_I, word.getBeta_i());
        jObj.put(FIELD_WORD_ALPHA_D, 0);  // TODO:  REMOVE

        return jObj;
    }

    /**
     * Constructs a new JSON object containing a JSON array of objects created by
     * {@link JSONHandler#getWordInfoJSON(VocObject)}
     *
     * @param words The words to convert into a JSON object
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getWordInfoJSONArray(List<VocObject> words) throws JSONException
    {
        JSONObject jObj = new JSONObject();

        JSONArray jArray = new JSONArray();

        for (VocObject word : words)
        {
            JSONObject wordInfoJSON = getWordInfoJSON(word);
            if (wordInfoJSON != null)
                jArray.put(wordInfoJSON);
        }

        jObj.put(FIELD_WORD_STORE_ARRAY, jArray);

        return jObj;

    }

    /**
     * Constructs a new JSON object containing a JSON array of objects created by
     * {@link JSONHandler#getUserWordInfoJSON(VocObject)}
     *
     * @param words The words to convert into a JSON object
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getUserWordInfoJSONArray(List<VocObject> words) throws JSONException
    {
        JSONObject jObj = new JSONObject();

        JSONArray jArray = new JSONArray();

        for (VocObject word : words)
        {
            JSONObject userWordInfoJSON = getUserWordInfoJSON(word);
            if (userWordInfoJSON != null)
                jArray.put(userWordInfoJSON);
        }

        jObj.put(FIELD_WORD_STORE_ARRAY, jArray);

        return jObj;

    }

    /**
     * Constructs a new JSON object representing a study session and all relevant word interactions
     *
     * @param session      The session to convert into a JSON object
     * @param interactions The interactions to include in the session data
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getSessionJSON(SessionObject session, List<InterxObject> interactions)
            throws JSONException
    {
        JSONObject jObj = new JSONObject();

        if (session.getParseId() != null && !session.getParseId().equals(""))
            jObj.put(FIELD_OBJECTID, session.getParseId());

        jObj.put(FIELD_SESSION_START, DBHandler.ISO_DATE.format(session.getStart()));
        jObj.put(FIELD_SESSION_END, DBHandler.ISO_DATE.format(session.getEnd()));

        JSONArray array = new JSONArray();

        for (InterxObject interx : interactions)
            array.put(getInteractionJSON(interx));

        jObj.put(FIELD_SESSION_PRESENTATIONS, array);

        return jObj;
    }

    /**
     * Constructs a new JSON object representing a word interaction
     *
     * @param interx The interaction to convert into a JSON object
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getInteractionJSON(InterxObject interx) throws JSONException
    {
        JSONObject jObj = new JSONObject();

        jObj.put(FIELD_INTERX_CHARCOUNT, interx.getCharCount());
        jObj.put(FIELD_INTERX_EXERCISE, interx.getExerciseType());
        jObj.put(FIELD_INTERX_LATENCY, interx.getLatency());
        jObj.put(FIELD_INTERX_RESULT, interx.getResult());
        jObj.put(FIELD_INTERX_TIMESTAMP, interx.getTimestamp());
        jObj.put(FIELD_INTERX_WORD, interx.getWord().getVoc());
        if (interx.getWord().getParseId() != null && !interx.getWord().getParseId().equals(""))
            jObj.put(FIELD_INTERX_WORDID, interx.getWord().getParseId());

        return jObj;
    }

    /**
     * Constructs a new JSON object representing parameters that are unique to a particular user
     *
     * @return A JSON object ready to submit to remote server
     * @throws JSONException Throws exception if an error occurs while creating JSON object
     */
    public static JSONObject getUserInfoJSON() throws JSONException
    {
        JSONObject jObj = new JSONObject();

        jObj.put(FIELD_USER_BETA_S, ModelMath.BETA_S);

        return jObj;
    }


    /**
     * Parses relevant data out of the given JSON object, and uses it to query the local database
     * for a matching word, replacing with values found in the JSON object.
     *
     * @param jObj The JSON object to parse
     * @param dm   The database manager for retrieving words by label
     * @return A word object containing the values contained in the JSON object
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static VocObject parseWord(JSONObject jObj, DatabaseManager dm) throws JSONException
    {
        String label = parseWordLabel(jObj);

        if (label == null)
            return null;

        VocObject wordObj = dm.getWordPairByLabel(label);

        parseWordInto(jObj, wordObj);

        return wordObj;
    }

    /**
     * Extracts the label field from the given JSON object.
     *
     * @param jObj The JSON object to parse
     * @return The label value contained in the JSON object
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static String parseWordLabel(JSONObject jObj) throws JSONException
    {
        return jObj.getString(FIELD_WORD_LABEL);
    }

    /**
     * Parses relevant data out of the given JSON object, and inserts its values into the given
     * word object.
     *
     * @param jObj The JSON object to parse
     * @param into The word object to insert values into
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static void parseWordInto(JSONObject jObj, VocObject into) throws JSONException
    {
        String objectId = jObj.getString(FIELD_OBJECTID);


        if (jObj.has(FIELD_WORD_BETA_I) && jObj.has(FIELD_WORD_SIGMA))
        {
            float beta_i = (float) jObj.getDouble(FIELD_WORD_BETA_I);
            float sigma_i = (float) jObj.getDouble(FIELD_WORD_SIGMA);
            // float alpha_d = (float) jObj.getDouble(FIELD_WORD_ALPHA_D);

            into.setParameters(into.getBeta_si(), beta_i, into.getAlpha(), sigma_i);
            into.setParseId(objectId);
        }
        else if (jObj.has(FIELD_UWINFO_ALPHA) && jObj.has(FIELD_UWINFO_BETA_SI))
        {
            float beta_si = (float) jObj.getDouble(FIELD_UWINFO_BETA_SI);
            float alpha_i = (float) jObj.getDouble(FIELD_UWINFO_ALPHA);

            into.setParameters(beta_si, into.getBeta_i(), alpha_i, into.getSigma());
            into.setUserInfoParseId(objectId);
        }
        else
        {
            if (jObj.has(FIELD_UWINFO_WORDID))
                into.setUserInfoParseId(objectId);
            else
                into.setParseId(objectId);
        }

    }


    /**
     * Parses user info from the given JSON object.
     *
     * @param jObj JSON object to parse.
     * @return The extracted user info.  Returns null if an error occurred while parsing.
     */
    public static UserObject parseUser(JSONObject jObj)
    {
        try
        {
            return parseUserOrThrow(jObj);
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not extract user data from JSON object: " + jObj.toString(), e);
            return null;
        }
    }

    /**
     * Parses user info from the given JSON object.  Throws a JSONException if an error occurs
     * while parsing.
     *
     * @param jObj JSON object to parse.
     * @return The extracted user info.  Returns null if an error occurred while parsing.
     * @throws JSONException If object could not be parsed into user info, throws JSONException.
     */
    public static UserObject parseUserOrThrow(JSONObject jObj) throws JSONException
    {
        String objectId = jObj.getString(FIELD_OBJECTID);
        String anonId = jObj.getString(FIELD_USER_ANONID);
        String sessionToken = jObj.getString(FIELD_USER_SESSION_TOKEN);

        return UserObject.build(objectId, anonId, sessionToken);
    }


    /**
     * Parses the given JSON object for all constants contained in a results array.
     *
     * @param jObj JSON object with a 'results' array of constants to update
     * @throws JSONException If object could not be parsed, throws JSONException
     */
    public static void parseConstantsAndUpdate(JSONObject jObj) throws JSONException
    {
        for (JSONObject param : extractResults(jObj))
        {
            switch (param.getString(FIELD_CONST_NAME))
            {
                case CONST_ALPHA_ITERS:
                    ModelMath.ALPHA_CONVERGENCE_ITERATIONS = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_PSYCH_TIME:
                    ModelMath.PSYCH_TIME_SCALAR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_RT_MAX_MULT:
                    ModelMath.RT_MAX_MULT = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_RT_MIN:
                    ModelMath.RT_MIN = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_RECALL_NOISE:
                    ModelMath.RECALL_PROB_NOISE_REDUCTION = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_CHAR_INTERCEPT:
                    ModelMath.RT_CHAR_DISCOUNT_INTERCEPT = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_CHAR_SCALAR:
                    ModelMath.RT_CHAR_DISCOUNT_SCALAR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_LOOKAHEAD:
                    ModelMath.LOOKAHEAD_TIME = param.getInt(FIELD_CONST_VALUE);
                    break;
                case CONST_DECAY_SCALAR:
                    ModelMath.DECAY_SCALAR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_RT_SCALAR:
                    ModelMath.RT_SCALAR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_THRESHOLD:
                    ModelMath.THRESHOLD = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_ALPHA_D:
                    ModelMath.ALPHA_DEFAULT = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_TRAIN_FACTOR:
                    ModelMath.TRAIN_FACTOR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
                case CONST_TEST_FACTOR:
                    ModelMath.TEST_FACTOR = (float) param.getDouble(FIELD_CONST_VALUE);
                    break;
            }
        }
    }

    /**
     * Extracts an list of JSON objects from a JSON array in the "results" field of the given
     * JSON object.
     *
     * @param jObj JSON object to extract results from
     * @return List of JSON objects contained in the results array of the given object
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static List<JSONObject> extractResults(JSONObject jObj) throws JSONException
    {
        List<JSONObject> list = new ArrayList<>();
        JSONArray results = jObj.getJSONArray(FIELD_RESULTS);

        for (int i = 0; i < results.length(); i++)
            list.add(results.getJSONObject(i));


        return list;
    }


    /**
     * Parses the given string into a JSON object.
     *
     * @param jsonString The string to parse
     * @return The parsed JSON object
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static JSONObject parseJSON(String jsonString) throws JSONException
    {
        return new JSONObject(jsonString);
    }

    /**
     * Parses the given byte array into a JSON object.
     *
     * @param rawBytes The bytes to parse
     * @return The parsed JSON object
     * @throws JSONException Throws exception if an error occurs while parsing JSON object
     */
    public static JSONObject parseJSON(byte[] rawBytes) throws JSONException
    {
        if (rawBytes == null)
            throw new IllegalArgumentException("Null byte array cannot be parsed to JSON.");

        return parseJSON(new String(rawBytes));
    }

}
