package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
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
    public static final String LOG_TAG = "[JSONHandler]";


    public static JSONObject getUserWordInfoJSON(VocObject word)
    {
        try
        {
            JSONObject jObj = new JSONObject();

            if (!word.getUserInfoParseId().equals(""))
                jObj.put(FIELD_OBJECTID, word.getUserInfoParseId());

            if (word.getParseId() != null && !word.getParseId().equals(""))
                jObj.put(FIELD_UWINFO_WORDID, word.getParseId());

            jObj.put(FIELD_UWINFO_WORD, word.getVoc());
            jObj.put(FIELD_UWINFO_ALPHA, word.getAlpha());
            jObj.put(FIELD_UWINFO_BETA_SI, word.getBeta_si());

            return jObj;

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }
    }

    public static JSONObject getWordInfoJSON(VocObject word)
    {
        try
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

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }
    }

    public static JSONObject getWordInfoJSONArray(List<VocObject> words)
    {
        try
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

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }

    }


    public static JSONObject getSessionJSON(SessionObject session, List<InterxObject> interactions)
    {
        try
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

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }
    }


    public static JSONObject getInteractionJSON(InterxObject interx)
    {
        try
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

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }
    }


    public static JSONObject getUserInfoJSON()
    {
        try
        {
            JSONObject jObj = new JSONObject();

            jObj.put(FIELD_USER_BETA_S, ModelMath.BETA_S);

            return jObj;

        } catch (JSONException e)
        {
            // TODO:  Handle exception
            return null;
        }
    }


    public static VocObject parseWord(JSONObject jObj, DatabaseManager dm)
    {
        String label = parseWordLabel(jObj);

        if (label == null)
            return null;

        VocObject wordObj = dm.getWordPairByLabel(label);

        return parseWordInto(jObj, wordObj);
    }

    public static String parseWordLabel(JSONObject jObj)
    {
        try
        {
            return jObj.getString(FIELD_WORD_LABEL);
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not extract word data from JSON object: " + jObj.toString(), e);
            return null;
        }

    }

    public static VocObject parseWordInto(JSONObject jObj, VocObject into)
    {
        try
        {
            String objectId = jObj.getString(FIELD_OBJECTID);
            String label = jObj.getString(FIELD_WORD_LABEL);

            if (!label.equals(into.getLabel()))
                throw new IllegalArgumentException(String.format("Mismatched word labels: %s and %s",
                                                                 label, into.getLabel()));

            if (jObj.has(FIELD_WORD_BETA_I) && jObj.has(FIELD_WORD_SIGMA))
            {
                float beta_i = (float) jObj.getDouble(FIELD_WORD_BETA_I);
                float sigma_i = (float) jObj.getDouble(FIELD_WORD_SIGMA);
                // float alpha_d = (float) jObj.getDouble(FIELD_WORD_ALPHA_D);
                into.setParameters(into.getBeta_si(), beta_i, into.getAlpha(), sigma_i);
            }

            into.setParseId(objectId);

            return into;

        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not extract word data from JSON object: " + jObj.toString(), e);
            return null;
        }
    }


    public static List<JSONObject> extractResults(JSONObject jObj)
    {
        List<JSONObject> list = new ArrayList<>();
        try
        {
            JSONArray results = jObj.getJSONArray("results");

            for (int i = 0; i < results.length(); i++)
                list.add(results.getJSONObject(i));

        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not extract results array from: " + jObj.toString(), e);
        }

        return list;
    }


    public static JSONObject parseJSONOrThrow(String jsonString) throws JSONException
    {
        return new JSONObject(jsonString);
    }

    public static JSONObject parseJSON(String jsonString)
    {
        try
        {
            return parseJSONOrThrow(jsonString);
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not parse JSON string: " + jsonString, e);
            return null;
        }
    }

    public static JSONObject parseJSONOrThrow(byte[] rawBytes) throws JSONException
    {
        if (rawBytes == null)
            throw new NullPointerException("Null byte array cannot be parsed to JSON.");

        return parseJSONOrThrow(new String(rawBytes));
    }

    public static JSONObject parseJSON(byte[] rawBytes)
    {
        if (rawBytes == null)
            return null;

        return parseJSON(new String(rawBytes));
    }

}
