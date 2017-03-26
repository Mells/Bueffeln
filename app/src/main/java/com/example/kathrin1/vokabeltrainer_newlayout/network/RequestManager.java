package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.content.Context;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.GenericUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.JSONReceivedListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.NetworkFailureListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.UserUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.WordListUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.InterxObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SessionObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.UserObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Handles all requests to the remote database
 */
public class RequestManager
{
    public static final String APP_ID = "asdf";
    public static final int PORT = 1449;
    public static final String URL = "http://rhodos.sfs.uni-tuebingen.de:" + PORT;

    public static final String APPLICATION_JSON = "application/json";

    public static final String HEADER_APP_ID = "x-parse-application-id";
    public static final String HEADER_OBJECT_ID = "x-parse-object-id";
    public static final String HEADER_SESSION_TOKEN = "x-parse-session-token";

    public static final String PARAM_LAST_UPDATE = "lastUpdate";
    public static final String PARAM_ANON_ID = "anonId";

    public static final String URL_DATA_DICTIONARY = URL + "/data/dictionary";
    public static final String URL_LOGIN = URL + "/user/login/anon";
    public static final String URL_VALIDATION = URL + "/parse/users/me";
    public static final String URL_CONSTANTS = URL + "/data/params";
    public static final String URL_USER_INFO = URL + "/user/info";
    public static final String URL_USER_WORD_INFO = URL + "/user/words";
    public static final String URL_USER_SESSION = "/user/session";


    public static final String LOG_TAG = "[RequestManager]";

    public static final int BATCH_SIZE = 50;


    private AsyncHttpClient client = new AsyncHttpClient();
    private final Context c;
    private final DatabaseManager dm;

    /**
     * Private constructor, use static constructor instead.
     */
    private RequestManager(Context c, DatabaseManager dm)
    {
        this.c = c;
        this.dm = dm;
        client.addHeader(HEADER_APP_ID, APP_ID);
    }

    /**
     * Static constructor, creates a new request manager object and returns it.
     *
     * @param c The context within which to perform all operations
     * @return The newly constructed RequestManager object.
     */
    public static RequestManager build(Context c, DatabaseManager dm)
    {
        return new RequestManager(c, dm);
    }


    /**
     * Retrieves all dictionary items stored in the database.
     *
     * @param lastUpdate      The timestamp to filter results by.  Only words that have been updated
     *                        since this date will be retrieved.  May be left null.
     * @param responseHandler Listener for possible response outcomes.
     */
    public void getDictionaryItems(Date lastUpdate, final WordListUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        // Add the last update parameter, if one was given
        RequestParams params = new RequestParams();
        if (lastUpdate != null)
            params.put(PARAM_LAST_UPDATE, DBHandler.ISO_DATE.format(lastUpdate));

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    List<VocObject> words = new ArrayList<>();
                    for (JSONObject wordObj : JSONHandler.extractResults(jObj))
                    {
                        VocObject word = JSONHandler.parseWord(wordObj, dm);
                        if (word != null)
                            words.add(word);
                    }

                    responseHandler.onSuccess(words);

                } catch (Exception e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.get(URL_DATA_DICTIONARY, params, loggingWrapper(handler));
    }

    /**
     * Pushes all given words to the remote database.  If any of the words are missing a Parse ID,
     * the server's response is used to identify the parse ID for the word.
     *
     * @param words           The words to push to the remote database.
     * @param responseHandler Actions to perform upon completion.
     */
    public void pushDictionaryItems(List<VocObject> words, final WordListUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        batchPushWords(words, 0, responseHandler);
    }

    /**
     * Recursively works through the list of words, processing individual batches and waiting for
     * the server's reply to move onto the next batch.
     *
     * @param allWords        The full list of words to split into batches
     * @param index           The starting index to batch from
     * @param responseHandler Actions to perform upon completion.
     */
    private void batchPushWords(final List<VocObject> allWords, final int index, final WordListUpdateListener responseHandler)
    {
        // BASE CASE:  If we've batched all list entries, return success
        if (index >= allWords.size())
        {
            responseHandler.onSuccess(allWords);
            return;
        }

        // Get the sublist to batch
        int limit = Math.min(allWords.size(), index + BATCH_SIZE);
        final List<VocObject> words = allWords.subList(index, limit);

        StringEntity body;
        try
        {
            // Convert the sublist into a JSON object
            JSONObject jObj = JSONHandler.getWordInfoJSONArray(words);

            // Convert the JSON object into an HTTP body
            body = buildEntity(jObj);
        }
        // Catches JSONExceptions (if error occurs while parsing JSON) or
        // UnsupportedEncodingExceptions if the JSON could not be formatted into a StringEntity
        catch (Exception e)
        {
            responseHandler.onLocalFailure(e);
            return;
        }

        // Build response object for the query
        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    Map<String, VocObject> labelMap = new HashMap<>();
                    for (VocObject word : words)
                        labelMap.put(word.getLabel(), word);

                    // Get the results from the JSON object
                    List<JSONObject> results = JSONHandler.extractResults(jObj);

                    // Iterate through all results, parse them, and use the values to update words
                    for (int i = 0; i < results.size(); i++)
                    {
                        String label = JSONHandler.parseWordLabel(results.get(i));
                        if (labelMap.containsKey(label))
                            JSONHandler.parseWordInto(results.get(i), labelMap.get(label));
                        else
                            Log.e(LOG_TAG, String.format("Word update skipped (label = %s).", label));

                    }

                    // Move onto the next batch
                    batchPushWords(allWords, index + BATCH_SIZE, responseHandler);
                }
                // Catches JSONExceptions (if error occurred while parsing JSON) and
                // IllegalArgumentExceptions (if for some reason labels don't match when
                // calling JSONHandler.parseWordInto())
                catch (Exception e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.put(c, URL_DATA_DICTIONARY, body, APPLICATION_JSON, loggingWrapper(handler));
    }


    /**
     * Pushes all given sessions and interactions to the remote database.
     *
     * @param user                  User to associate the sessions with
     * @param sessionInteractionMap The sessions and their respective interactions to push.
     * @param responseHandler       Actions to perform upon completion.
     */
    public void pushSessions(UserObject user,
                             Map<SessionObject, List<InterxObject>> sessionInteractionMap,
                             final GenericUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        if (user == null)
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable get info for null user."));
            return;
        }

        Header sessionToken = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());
        Header objectId = new BasicHeader(HEADER_OBJECT_ID, user.getObjectId());
        Header[] headers = new Header[]{sessionToken, objectId};

        List<SessionObject> sessionList = new ArrayList<>(sessionInteractionMap.keySet());

        batchPushSessions(sessionInteractionMap, sessionList, 0, headers, responseHandler);
    }

    /**
     * Recursively works through the list of sessions and interactions, processing individual
     * sessions and waiting for the server's reply to move onto the next session.
     *
     * @param sessionInteractionMap The map from sessions to interactions
     * @param sessionList           The list of sessions to work through
     * @param index                 The index of the next session to submit
     * @param headers               The headers to include in the request
     * @param responseHandler       Actions to perform upon completion.
     */
    private void batchPushSessions(final Map<SessionObject, List<InterxObject>> sessionInteractionMap,
                                   final List<SessionObject> sessionList, final int index,
                                   final Header[] headers, final GenericUpdateListener responseHandler)
    {
        // BASE CASE:  If we've successfully iterated through all sessions, report success
        if (index >= sessionList.size())
        {
            responseHandler.onSuccess();
            return;
        }

        SessionObject currSession = sessionList.get(index);

        // Convert the current session and interaction list into a JSON object
        StringEntity body;
        try
        {
            // Convert the session and interactions into a JSON object
            JSONObject jObj = JSONHandler.getSessionJSON(currSession, sessionInteractionMap.get(currSession));

            // Convert the JSON object into an HTTP body
            body = buildEntity(jObj);
        }
        // Catches JSONExceptions (if error occurs while parsing JSON) or
        // UnsupportedEncodingExceptions if the JSON could not be formatted into a StringEntity
        catch (Exception e)
        {
            responseHandler.onLocalFailure(e);
            return;
        }


        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                // Do nothing with the JSON
                batchPushSessions(sessionInteractionMap, sessionList, index + 1,
                                  headers, responseHandler);
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.post(c, URL_USER_SESSION, headers, body, APPLICATION_JSON, loggingWrapper(handler));
    }



    public void pushUserInfo(UserObject user, final GenericUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        if (user == null)
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable get info for null user."));
            return;
        }

        Header sessionToken = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());
        Header objectId = new BasicHeader(HEADER_OBJECT_ID, user.getObjectId());
        Header[] headers = new Header[]{sessionToken, objectId};

        // Convert the current session and interaction list into a JSON object
        StringEntity body;
        try
        {
            // Convert the session and interactions into a JSON object
            JSONObject jObj = JSONHandler.getUserInfoJSON();

            // Convert the JSON object into an HTTP body
            body = buildEntity(jObj);
        }
        // Catches JSONExceptions (if error occurs while parsing JSON) or
        // UnsupportedEncodingExceptions if the JSON could not be formatted into a StringEntity
        catch (Exception e)
        {
            responseHandler.onLocalFailure(e);
            return;
        }

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                // Do nothing with JSON
                responseHandler.onSuccess();
            }
        }, responseHandler);



        // Perform the HTTP operation
        client.put(c, URL_USER_INFO, headers, body, APPLICATION_JSON, loggingWrapper(handler));
    }

    public void pushUserWordInfo(UserObject user, Collection<VocObject> words,
                                 GenericUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        if (user == null)
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable get info for null user."));
            return;
        }

        Header sessionToken = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());
        Header objectId = new BasicHeader(HEADER_OBJECT_ID, user.getObjectId());
        Header[] headers = new Header[]{sessionToken, objectId};

        batchPushUserWordInfo(new ArrayList<VocObject>(words), 0, headers, responseHandler);
    }

    private void batchPushUserWordInfo(final List<VocObject> allWords, final int index,
                                       final Header[] headers,
                                       final GenericUpdateListener responseHandler)
    {
        // BASE CASE:  If we've batched all list entries, return success
        if (index >= allWords.size())
        {
            responseHandler.onSuccess();
            return;
        }

        // Get the sublist to batch
        int limit = Math.min(allWords.size(), index + BATCH_SIZE);
        final List<VocObject> words = allWords.subList(index, limit);

        StringEntity body;
        try
        {
            // Convert the sublist into a JSON object
            JSONObject jObj = JSONHandler.getWordInfoJSONArray(words);

            // Convert the JSON object into an HTTP body
            body = buildEntity(jObj);
        }
        // Catches JSONExceptions (if error occurs while parsing JSON) or
        // UnsupportedEncodingExceptions if the JSON could not be formatted into a StringEntity
        catch (Exception e)
        {
            responseHandler.onLocalFailure(e);
            return;
        }

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                // Do nothing with JSON
                batchPushUserWordInfo(allWords, index + BATCH_SIZE, headers, responseHandler);
            }
        }, responseHandler);



        // Perform the HTTP operation
        client.put(c, URL_USER_WORD_INFO, headers, body, APPLICATION_JSON, loggingWrapper(handler));
    }

    /**
     * Attempts to log-in the given user.
     * <p>
     * If the given user is null, a new user is generated and registered.
     * </p>
     * <p>
     * Otherwise, the user's session token is checked for validity.  If it is
     * valid, no change occurs.  If it is not valid, then a new session token is obtained.
     * </p>
     * <p>
     * Results are reported through the given listener.  If successful, the logged in user
     * is passed to {@link UserUpdateListener#onSuccess(UserObject)}.
     * </p>
     *
     * @param user            The user to log in.
     * @param responseHandler Actions to perform upon completion.
     */
    public void logInUser(final UserObject user, final UserUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        // If no user is given, register a new user instead.
        if (user == null)
        {
            registerUser(responseHandler);
            return;
        }

        // Otherwise, attempt to validate the user's session token.


        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                // Attempt to extract an object ID from the response, which will only appear if
                // the session token was successfully validated.  In such a case, return success.
                // If not, then attempt to get a new session token.
                try
                {
                    String objectId = jObj.getString(JSONHandler.FIELD_OBJECTID);
                    user.setObjectId(objectId); // Probably unnecessary

                    responseHandler.onSuccess(user);

                }
                // If no object ID field was successfully parsed, then must have gotten an error
                // response, meaning we the session token was invalid.  Acquire new token.
                catch (JSONException e)
                {
                    getNewSessionToken(user, responseHandler);
                }
            }
        }, responseHandler);


        Header tokenHeader = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());

        // Perform the HTTP operation
        client.get(c, URL_VALIDATION, new Header[]{tokenHeader}, null, loggingWrapper(handler));
    }

    /**
     * Attempts to register a new user.
     * <p>
     * <p>
     * Results are reported through the given listener.  If successful, the logged in user
     * is passed to {@link UserUpdateListener#onSuccess(UserObject)}.
     * </p>
     *
     * @param responseHandler Actions to perform upon completion.
     */
    private void registerUser(final UserUpdateListener responseHandler)
    {
        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    responseHandler.onSuccess(JSONHandler.parseUserOrThrow(jObj));
                } catch (JSONException e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.post(c, URL_LOGIN, null, loggingWrapper(handler));
    }

    /**
     * Attempts to get a new session token for a new user.
     * <p>
     * <p>
     * Results are reported through the given listener.  If successful, the logged in user
     * is passed to {@link UserUpdateListener#onSuccess(UserObject)}.
     * </p>
     *
     * @param user            The user to get a new session token for.
     * @param responseHandler Actions to perform upon completion.
     */
    private void getNewSessionToken(final UserObject user, final UserUpdateListener responseHandler)
    {
        // Add the user's anonymous ID as a parameter for logging in
        RequestParams params = new RequestParams();
        params.add(PARAM_ANON_ID, user.getAnonId());

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    responseHandler.onSuccess(JSONHandler.parseUserOrThrow(jObj));
                } catch (JSONException e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.get(c, URL_LOGIN, params, loggingWrapper(handler));
    }


    /**
     * Retrieves all parameter (constant) values that have been updated since the given timestamp.
     * The values returned by the server are immediately applied to {@link com.example.kathrin1.vokabeltrainer_newlayout.learnmodel.ModelMath}
     *
     * @param lastUpdate      Timestamp to filter results by.  Only parameters that have been updated
     *                        AFTER this timestamp will be updated.  May be left null.
     * @param responseHandler Actions to be performed upon completion.
     */
    public void updateConstants(Date lastUpdate, final GenericUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        RequestParams params = new RequestParams();
        if (lastUpdate != null)
            params.put(PARAM_LAST_UPDATE, DBHandler.ISO_DATE.format(lastUpdate));

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    JSONHandler.parseConstantsAndUpdate(jObj);
                    responseHandler.onSuccess();
                } catch (JSONException e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Perform the HTTP operation
        client.get(c, URL_CONSTANTS, params, loggingWrapper(handler));
    }

    /**
     * Retrieves all user info from the server, and updates local values accordingly.
     *
     * @param user            The user to receive updates for
     * @param responseHandler Actions to perform upon completion
     */
    public void updateUserInfo(UserObject user, final GenericUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }

        if (user == null)
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable get info for null user."));
            return;
        }

        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    ModelMath.BETA_S = (float) jObj.getDouble(JSONHandler.FIELD_USER_BETA_S);
                    responseHandler.onSuccess();
                } catch (JSONException e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Create the necessary headers
        Header sessionToken = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());
        Header objectId = new BasicHeader(HEADER_OBJECT_ID, user.getObjectId());


        // Perform the HTTP operation
        client.get(c, URL_USER_INFO, new Header[]{sessionToken, objectId}, null,
                   loggingWrapper(handler));
    }

    /**
     * Retrieves all user word info from the server, and updates local values accordingly.
     *
     * @param lastUpdate      Timestamp to filter results by.  Only parameters that have been updated
     *                        AFTER this timestamp will be updated.  May be left null.
     * @param user            The user to receive updates for
     * @param responseHandler Actions to perform upon completion.  A list of all updated words
     *                        is passed to {@link WordListUpdateListener#onSuccess(List)}.
     */
    public void updateUserWordInfo(Date lastUpdate, UserObject user,
                                   final WordListUpdateListener responseHandler)
    {
        if (!isNetworkReady())
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));
            return;
        }
        if (user == null)
        {
            responseHandler.onLocalFailure(new IllegalStateException("Unable get info for null user."));
            return;
        }


        RequestParams params = new RequestParams();
        if (lastUpdate != null)
            params.put(PARAM_LAST_UPDATE, DBHandler.ISO_DATE.format(lastUpdate));


        AsyncHttpResponseHandler handler = basicHandler(new JSONReceivedListener()
        {
            @Override
            public void onJSONReceived(JSONObject jObj)
            {
                try
                {
                    List<VocObject> wordList = new ArrayList<>();

                    // Get the results from the JSON object
                    List<JSONObject> results = JSONHandler.extractResults(jObj);

                    // Iterate through all results, parse them, and use the values to update words
                    for (int i = 0; i < results.size(); i++)
                    {
                        wordList.add(JSONHandler.parseWord(results.get(i), dm));
                    }

                    responseHandler.onSuccess(wordList);

                } catch (JSONException e)
                {
                    responseHandler.onLocalFailure(e);
                }
            }
        }, responseHandler);


        // Create the necessary headers
        Header sessionToken = new BasicHeader(HEADER_SESSION_TOKEN, user.getSessionToken());
        Header objectId = new BasicHeader(HEADER_OBJECT_ID, user.getObjectId());


        // Perform the HTTP operation
        client.get(c, URL_USER_WORD_INFO, new Header[]{sessionToken, objectId}, params,
                   loggingWrapper(handler));
    }


    /**
     * Determines whether network communications are currently possible or not.
     *
     * @return Returns true if the network is ready for communication, false otherwise.
     */
    public boolean isNetworkReady()
    {
        // TODO:  Implement
        return true;
    }

    /**
     * Constructs a string entity out of the given JSON object to attach to an HTTP request
     *
     * @param jObj The JSON object to built the string entity out of
     * @return The constructed string entity
     */
    private static StringEntity buildEntity(JSONObject jObj) throws UnsupportedEncodingException
    {
        StringEntity entity = new StringEntity(jObj.toString());

        entity.setContentType(APPLICATION_JSON);

        return entity;
    }

    /**
     * Wraps an existing response handler with another response handler that reports activity
     * to the log.
     *
     * @param toWrap The response handler to wrap.
     * @return The wrapped response handler
     */
    private AsyncHttpResponseHandler loggingWrapper(final AsyncHttpResponseHandler toWrap)
    {
        return new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                String body = responseBody == null ? "." : ": " + new String(responseBody);
                Log.d(LOG_TAG, "Communication success" + body);
                toWrap.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                String body = responseBody == null ? "." : ": " + new String(responseBody);
                Log.e(LOG_TAG, "Communication error" + body, error);
                toWrap.onFailure(statusCode, headers, responseBody, error);
            }
        };
    }

    /**
     * Provides a basic listener to attach to all HTTP requests which handles all potential errors
     * in communication and parsing of the JSON response.
     *
     * @param successListener Listener invoked if JSON body is successfully received and parsed.
     * @param failureListener Listener invoked if an error occurred while attempting to
     *                        communicate with the server and parse the response.
     * @return The newly constructed basic AsyncHttpResponseHandler.
     */
    private AsyncHttpResponseHandler basicHandler(final JSONReceivedListener successListener,
                                                  final NetworkFailureListener failureListener)
    {
        return new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                // If there was no response body, simply report success with an empty JSON object
                if (responseBody == null)
                {
                    successListener.onJSONReceived(new JSONObject());
                    return;
                }

                // Attempt to parse the successful response into a JSON object
                JSONObject jObj;
                try
                {
                    jObj = JSONHandler.parseJSON(responseBody);

                    // If the JSON was successfully parsed, report success
                    successListener.onJSONReceived(jObj);

                } catch (JSONException e)
                {
                    failureListener.onLocalFailure(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if (responseBody != null)
                    failureListener.onRemoteFailure(NetworkError.build(statusCode, responseBody));
                else
                    failureListener.onLocalFailure(error);
            }
        };
    }

}
