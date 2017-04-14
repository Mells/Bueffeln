package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * An object used to store information about any errors that occur while performing network
 * operations.
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
     *
     * @param httpStatus  The HTTP status code of this error
     * @param code        The internal error code for this error
     * @param message     A short message describing the error code
     * @param description A longer description of the error code
     * @param details     Further details relating to the error
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError build(int httpStatus, int code, String message, String description, String details)
    {
        return new NetworkError(httpStatus, code, message, description, details);
    }

    /**
     * Static constructor, creates a new network error object and returns it.
     *
     * @param httpStatus The HTTP status code of this error
     * @param jObj       JSON object containing 'code', 'message', 'description' and 'details fields to
     *                   fill in this network error object.
     * @return The newly constructed NetworkError object.
     */
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

    /**
     * Static constructor, creates a new network error object and returns it.
     *
     * @param httpStatus The HTTP status code of this error
     * @param jsonString String describing a JSON object containing 'code', 'message',
     *                   'description' and 'details fields to fill in this network error object.
     * @return The newly constructed NetworkError object.  If the given JSON string is not properly
     * formatted, returns a generic error.
     */
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

    /**
     * Static constructor, creates a new network error object and returns it.
     *
     * @param httpStatus The HTTP status code of this error
     * @param rawBytes Byte array describing a JSON object containing 'code', 'message',
     *                   'description' and 'details fields to fill in this network error object.
     * @return The newly constructed NetworkError object.  If the given JSON object is not properly
     * formatted, returns a generic error.
     */
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

    /**
     * Static constructor, constructs a new network error object and returns it.  Generates a
     * generic error indicating that a local error occurred, using the given throwable as the
     * details to the error.
     *
     * @param throwable Throwable object to use as the details for the error
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError buildFromThrowable(Throwable throwable)
    {
        return build(-1, 0, LOCAL_ERROR, "Some non-network related error occurred while" +
                                         "performing network operations.", throwable.getMessage());
    }

    /**
     * Static constructor, constructs a new network error object and returns it.  Generates an
     * error object that consists of multiple other network errors, consolidating them all into
     * a single error.
     *
     * @param errors The errors to consolidate.
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError buildMultiError(NetworkError... errors)
    {
        return build(-1, 0, "MULTIPLE ERRORS", "Multiple errors while performing network operations.",
                     TextUtils.join("\n", errors));
    }

    /**
     * Static constructor, constructs a new network error object and returns it.  Generates an
     * error object that consists of multiple other network errors, consolidating them all into
     * a single error.
     *
     * @param errors The errors to consolidate.
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError buildMultiError(List<NetworkError> errors)
    {
        return build(-1, 0, "MULTIPLE ERRORS", "Multiple errors while performing network operations.",
                     TextUtils.join("\n", errors));
    }

    /**
     * Static constructor, constructs a new network error object and returns it.  Generates an
     * error object that indicates that no work was done, due to the requested work being
     * unnecessary.
     *
     * @return The newly constructed NetworkError object.
     */
    public static NetworkError buildNoWorkDoneError()
    {
        return build(-1, 1, NO_WORK_DONE, "Attempted to perform network action that was unnecessary.",
                     "");
    }

    /**
     * Returns whether this network error is a "no work done" error, as generated by
     * {@link NetworkError#buildNoWorkDoneError()}.
     *
     * @return Returns true if this error is a "no work done" error, false otherwise.
     */
    public boolean isNoWorkDoneError()
    {
        return message.equals(NO_WORK_DONE);
    }

    /**
     * Returns whether this network error is a local error, as generated by
     * {@link NetworkError#buildFromThrowable(Throwable)}.
     *
     * @return Returns true if this error is a local error, false otherwise.
     */
    public boolean isLocalError()
    {
        return message.equals(LOCAL_ERROR);
    }

    /**
     * {@inheritDoc}
     */
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
