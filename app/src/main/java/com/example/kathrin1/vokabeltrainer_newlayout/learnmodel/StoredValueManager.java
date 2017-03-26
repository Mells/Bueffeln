package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.UserObject;

import java.text.ParseException;
import java.util.Date;

/**
 * For retrieving and storing values stored locally.
 */

public class StoredValueManager
{
    public static final String PREFS_FILE = "ModelPrefs";

    public static final String LAST_UPDATE = "lastUpdate";
    public static final String LAST_SUBMISSION = "lastSubmission";

    public static final String USER_OBJECTID = "user_objectid";
    public static final String USER_ANONID = "user_anonid";
    public static final String USER_SESSION_TOKEN = "user_sessiontoken";

    public static final String CONST_THRESHOLD = "const_threshold";
    public static final String CONST_ALPHA_D = "const_slpha_d";
    public static final String CONST_DECAY_SCALAR = "const_decay_scalar";
    public static final String CONST_RT_SCALAR = "const_rt_scalar";
    public static final String CONST_RECALL_NOISE = "const_prob_noise_reduction";
    public static final String CONST_TRAIN_FACTOR = "const_train_factor";
    public static final String CONST_TEST_FACTOR = "const_test_factor";
    public static final String CONST_LOOKAHEAD = "const_lookahead_time";
    public static final String CONST_PSYCH_TIME = "const_psych_time_scalar";
    public static final String CONST_CHAR_SCALAR = "const_rt_char_discount_scalar";
    public static final String CONST_CHAR_INTERCEPT = "const_rt_char_discount_intercept";
    public static final String CONST_RT_MIN = "const_rt_min";
    public static final String CONST_BETA_S = "const_beta_s";
    public static final String CONST_ALPHA_ITERS = "const_alpha_convergence_iterations";
    public static final String CONST_RT_MAX_MULT = "const_rt_max_mult";


    public static final String LOG_TAG = "[StoredValueManager]";

    private final Context c;
    private final SharedPreferences prefs;


    /**
     * Private constructor, use static constructor instead.
     */
    private StoredValueManager(Context c)
    {
        this.c = c;
        prefs = c.getSharedPreferences(PREFS_FILE, 0);
    }

    /**
     * Static constructor, creates a new stored value manager object and returns it.
     *
     * @return The newly constructed StoredValueManager object.
     */
    public static StoredValueManager build(Context c)
    {
        return new StoredValueManager(c);
    }


    /**
     * Retrieve the timestamp of the latest remote update.
     *
     * @return The timestamp of the latest remote update.  If no such timestamp exists, or if it
     * cannot be parsed, returns null.
     */
    public Date getLastUpdate()
    {
        return getDate(LAST_UPDATE);
    }

    /**
     * Retrieve the timestamp of the latest remote data submission.
     *
     * @return The timestamp of the latest remote data submission.  If no such timestamp exists,
     * or if it cannot be parsed, returns null.
     */
    public Date getLastSubmission()
    {
        return getDate(LAST_SUBMISSION);
    }

    /**
     * Gets a stored preference that contains a timestamp, and converts that timestamp string into
     * a {@link Date} object.
     *
     * @param prefName The preference to retrieve
     * @return The retrieved preference converted into a date.  May return null if the preference
     * does not exist, or if an error occurred while parsing it.
     */
    private Date getDate(String prefName)
    {
        String update = prefs.getString(prefName, null);

        if (update == null)
            return null;

        try
        {
            return DBHandler.ISO_DATE.parse(update);
        } catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * Stores the timestamp of the latest update.
     * This value commit is performed asynchronously.
     *
     * @param update The timestamp to store.
     */
    public void storeLastUpdate(Date update)
    {
        prefs.edit()
             .putString(LAST_UPDATE, DBHandler.ISO_DATE.format(update))
             .apply();
    }

    /**
     * Stores the timestamp of the latest submission of data.
     * This value commit is performed asynchronously.
     *
     * @param update The timestamp to store.
     */
    public void storeLastSubmission(Date update)
    {
        prefs.edit()
             .putString(LAST_SUBMISSION, DBHandler.ISO_DATE.format(update))
             .apply();
    }

    /**
     * Stores the timestamp of the latest submission of data.
     * This is done in a static manner, so that a StoredValueManager object needn't exist in memory
     * This value commit is performed asynchronously.
     *
     * @param c Context within which to perform the operation.
     * @param update The timestamp to store.
     */
    public static void storeLastSubmissionStatic(Context c, Date update)
    {
        SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE, 0);
        prefs.edit()
             .putString(LAST_SUBMISSION, DBHandler.ISO_DATE.format(update))
             .apply();
    }

    /**
     * Retrieves all stored user info as a UserObject.
     *
     * @return The stored user info.  Returns null if no such info exists.
     */
    public UserObject getUserInfo()
    {
        String objectId = prefs.getString(USER_OBJECTID, null);
        String anonId = prefs.getString(USER_ANONID, null);
        String sessionToken = prefs.getString(USER_SESSION_TOKEN, null);

        if (objectId == null || anonId == null || sessionToken == null)
            return null;

        return UserObject.build(objectId, anonId, sessionToken);
    }

    /**
     * Stores the given user info.
     * This value commit is performed asynchronously.
     *
     * @param user The user info to store.
     */
    public void storeUserInfo(UserObject user)
    {
        if (user == null)
            return;

        Log.d(LOG_TAG, "Storing user: " + user.toString());
        prefs.edit()
             .putString(USER_OBJECTID, user.getObjectId())
             .putString(USER_ANONID, user.getAnonId())
             .putString(USER_SESSION_TOKEN, user.getSessionToken())
             .apply();
    }

    /**
     * Loads all stored model parameters into the static variables of ModelMath.
     */
    public void loadConstants()
    {
        ModelMath.THRESHOLD = prefs.getFloat(CONST_THRESHOLD, ModelMath.THRESHOLD);
        ModelMath.ALPHA_DEFAULT = prefs.getFloat(CONST_ALPHA_D, ModelMath.ALPHA_DEFAULT);
        ModelMath.DECAY_SCALAR = prefs.getFloat(CONST_DECAY_SCALAR, ModelMath.DECAY_SCALAR);
        ModelMath.RT_SCALAR = prefs.getFloat(CONST_RT_SCALAR, ModelMath.RT_SCALAR);
        ModelMath.RECALL_PROB_NOISE_REDUCTION = prefs.getFloat(CONST_RECALL_NOISE, ModelMath.RECALL_PROB_NOISE_REDUCTION);
        ModelMath.TRAIN_FACTOR = prefs.getFloat(CONST_TRAIN_FACTOR, ModelMath.TRAIN_FACTOR);
        ModelMath.TEST_FACTOR = prefs.getFloat(CONST_TEST_FACTOR, ModelMath.TEST_FACTOR);
        ModelMath.LOOKAHEAD_TIME = prefs.getInt(CONST_LOOKAHEAD, ModelMath.LOOKAHEAD_TIME);
        ModelMath.PSYCH_TIME_SCALAR = prefs.getFloat(CONST_PSYCH_TIME, ModelMath.PSYCH_TIME_SCALAR);
        ModelMath.RT_CHAR_DISCOUNT_SCALAR = prefs.getFloat(CONST_CHAR_SCALAR, ModelMath.RT_CHAR_DISCOUNT_SCALAR);
        ModelMath.RT_CHAR_DISCOUNT_INTERCEPT = prefs.getFloat(CONST_CHAR_INTERCEPT, ModelMath.RT_CHAR_DISCOUNT_INTERCEPT);
        ModelMath.RT_MIN = prefs.getFloat(CONST_RT_MIN, ModelMath.RT_MIN);
        ModelMath.BETA_S = prefs.getFloat(CONST_BETA_S, ModelMath.BETA_S);
        ModelMath.ALPHA_CONVERGENCE_ITERATIONS = prefs.getFloat(CONST_ALPHA_ITERS, ModelMath.ALPHA_CONVERGENCE_ITERATIONS);
        ModelMath.RT_MAX_MULT = prefs.getFloat(CONST_RT_MAX_MULT, ModelMath.RT_MAX_MULT);
    }

    /**
     * Saves all model parameters.
     * This value commit is performed asynchronously.
     */
    public void saveConstants()
    {
        prefs.edit()
             .putFloat(CONST_THRESHOLD, ModelMath.THRESHOLD)
             .putFloat(CONST_ALPHA_D, ModelMath.ALPHA_DEFAULT)
             .putFloat(CONST_DECAY_SCALAR, ModelMath.DECAY_SCALAR)
             .putFloat(CONST_RT_SCALAR, ModelMath.RT_SCALAR)
             .putFloat(CONST_RECALL_NOISE, ModelMath.RECALL_PROB_NOISE_REDUCTION)
             .putFloat(CONST_TRAIN_FACTOR, ModelMath.TRAIN_FACTOR)
             .putFloat(CONST_TEST_FACTOR, ModelMath.TEST_FACTOR)
             .putInt(CONST_LOOKAHEAD, ModelMath.LOOKAHEAD_TIME)
             .putFloat(CONST_PSYCH_TIME, ModelMath.PSYCH_TIME_SCALAR)
             .putFloat(CONST_CHAR_SCALAR, ModelMath.RT_CHAR_DISCOUNT_SCALAR)
             .putFloat(CONST_CHAR_INTERCEPT, ModelMath.RT_CHAR_DISCOUNT_INTERCEPT)
             .putFloat(CONST_RT_MIN, ModelMath.RT_MIN)
             .putFloat(CONST_BETA_S, ModelMath.BETA_S)
             .putFloat(CONST_ALPHA_ITERS, ModelMath.ALPHA_CONVERGENCE_ITERATIONS)
             .putFloat(CONST_RT_MAX_MULT, ModelMath.RT_MAX_MULT)
             .apply();
    }
}
