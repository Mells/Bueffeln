package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class NetworkError
{
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";
    public static final String DETAILS = "details";

    public static final String LOG_TAG = "[NetworkError]";


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

    public static NetworkError build(int httpStatus, JSONObject jObj)
    {
        if (jObj == null)
            return null;

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
        return build(httpStatus, JSONHandler.parseJSON(jsonString));
    }

    public static NetworkError build(int httpStatus, byte[] rawBytes)
    {
        return build(httpStatus, JSONHandler.parseJSON(rawBytes));
    }

    public static NetworkError buildFromThrowable(Throwable throwable)
    {
        return build(-1, 0, "LOCAL ERROR", "Some non-network related error occurred while" +
                                           "performing network operations.", throwable.getMessage());
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
