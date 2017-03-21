package com.example.kathrin1.vokabeltrainer_newlayout.network;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        } catch (JSONException e) {
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

            return jObj;

        } catch (JSONException e) {
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

        } catch (JSONException e) {
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

        } catch (JSONException e) {
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

        } catch (JSONException e) {
            // TODO:  Handle exception
            return null;
        }
    }





}
