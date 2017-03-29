package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 */
public class NetworkError
{
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";
    public static final String DETAILS = "details";

    public static final String LOG_TAG = "[NetworkError]";
    public static final String NO_WORK_DONE = "NO WORK DONE";
    public static final String LOCAL_ERROR = "LOCAL ERROR";


    public final int httpStatus;
    public final int code;
    public final String message, description, details;

    /**
     * Private constructor, use static constructor instead.
     */
    private NetworkError(int httpStatus, int code, String message, String description, String details)
    {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.description = description;
        this.details = details;
    }

    /**
     * Static constructor, creates a new network error object and returns it.
     * <p>
     * // TODO:  Doc
     *
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError build(int httpStatus, int code, String message, String description, String details)
    {
        return new NetworkError(httpStatus, code, message, description, details);
    }

    public static NetworkError buildFromJSON(int httpStatus, JSONObject jObj)
    {
        if (jObj == null)
            return build(httpStatus, 999, "UNKNOWN ERROR", "No idea what happened.", "");

        try
        {
            int code = jObj.getInt(CODE);
            String message = jObj.getString(MESSAGE);
            String description = jObj.getString(DESCRIPTION);

            String details = jObj.get(DETAILS) instanceof JSONObject
                             ? jObj.getJSONObject(DETAILS).toString()
                             : jObj.getString(DETAILS);

            return build(httpStatus, code, message, description, details);
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Could not extract relevant error details from JSON object: "
                           + jObj.toString(), e);
            return null;
        }
    }

    public static NetworkError build(int httpStatus, String jsonString)
    {
        try
        {
            return buildFromJSON(httpStatus, JSONHandler.parseJSON(jsonString));
        } catch (JSONException e)
        {
            return buildFromJSON(httpStatus, null);
        }
    }

    public static NetworkError build(int httpStatus, byte[] rawBytes)
    {
        try
        {
            return buildFromJSON(httpStatus, JSONHandler.parseJSON(rawBytes));
        } catch (JSONException e)
        {
            return buildFromJSON(httpStatus, null);
        }
    }

    public static NetworkError buildFromThrowable(Throwable throwable)
    {
        return build(-1, 0, LOCAL_ERROR, "Some non-network related error occurred while" +
                                         "performing network operations.", throwable.getMessage());
    }

    public static NetworkError buildMultiError(NetworkError... errors)
    {
        return build(-1, 0, "MULTIPLE ERRORS", "Multiple errors while performing network operations.",
                     TextUtils.join("\n", errors));
    }

    public static NetworkError buildMultiError(List<NetworkError> errors)
    {
        return build(-1, 0, "MULTIPLE ERRORS", "Multiple errors while performing network operations.",
                     TextUtils.join("\n", errors));
    }

    public static NetworkError buildNoWorkDoneError()
    {
        return build(-1, 1, NO_WORK_DONE, "Attempted to perform network action that was unnecessary.",
                     "");
    }

    public boolean isNoWorkDoneError()
    {
        return message.equals(NO_WORK_DONE);
    }

    public boolean isLocalError()
    {
        return message.equals(LOCAL_ERROR);
    }

    @Override
    public String toString()
    {
        return "NetworkError{" +
               "httpStatus=" + httpStatus +
               ", code=" + code +
               ", message='" + message + '\'' +
               ", description='" + description + '\'' +
               ", details='" + details + '\'' +
               '}';
    }
}
