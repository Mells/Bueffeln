package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.content.Context;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.network.listeners.WordListUpdateListener;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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

    public static final String URL_DATA_DICTIONARY = URL + "/data/dictionary";
    public static final String LOG_TAG = "[RequestManager]";

    public static final int BATCH_SIZE = 40;


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
     *                        since this date will be retrieved.
     * @param responseHandler Listener for possible response outcomes.
     */
    public void getDictionaryItems(Date lastUpdate, final WordListUpdateListener responseHandler)
    {
        if (!isNetworkReady())
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));

        RequestParams params = new RequestParams();
        if (lastUpdate != null)
            params.put(PARAM_LAST_UPDATE, DBHandler.ISO_DATE.format(lastUpdate));

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                JSONObject jObj;
                try
                {
                    jObj = JSONHandler.parseJSONOrThrow(responseBody);
                } catch (Exception e)
                {
                    responseHandler.onLocalFailure(e);
                    return;
                }

                List<VocObject> words = new ArrayList<>();
                for (JSONObject wordObj : JSONHandler.extractResults(jObj))
                {
                    VocObject word = JSONHandler.parseWord(wordObj, dm);
                    if (word != null)
                        words.add(word);
                }

                responseHandler.onSuccess(words);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if (responseBody != null)
                    responseHandler.onRemoteFailure(NetworkError.build(statusCode, responseBody));
                else
                    responseHandler.onLocalFailure(error);
            }
        };

        client.get(URL_DATA_DICTIONARY, params, loggingWrapper(handler));
    }

    public void pushDictionaryItems(List<VocObject> words, final WordListUpdateListener responseHandler)
    {
        if (!isNetworkReady())
            responseHandler.onLocalFailure(new IllegalStateException("Unable to communicate on network."));

        batchPushWords(words, 0, responseHandler);
    }

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

        // Convert the sublist into a JSON object
        JSONObject jObj = JSONHandler.getWordInfoJSONArray(words);
        if (jObj == null)
            responseHandler.onLocalFailure(new IllegalArgumentException("Unable to build JSON object."));

        // Convert the JSON object into an HTTP body
        StringEntity body = buildEntity(jObj);

        if (body == null)
            responseHandler.onLocalFailure(new IllegalArgumentException("Unable to build string entity."));

        // Builder response object for the query
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                // Attempt to parse the successful response into a JSON object
                JSONObject jObj;
                try
                {
                    jObj = JSONHandler.parseJSONOrThrow(responseBody);
                } catch (Exception e)
                {
                    responseHandler.onLocalFailure(e);
                    return;
                }

                Map<String, VocObject> labelMap = new HashMap<>();
                for (VocObject word : words)
                    labelMap.put(word.getLabel(), word);


                // Get the results from the JSON object
                List<JSONObject> results = JSONHandler.extractResults(jObj);

                // Iterate through all results, parse them, and use the values to update words
                for (int i = 0; i < results.size(); i++)
                {
                    try
                    {
                        String label = JSONHandler.parseWordLabel(results.get(i));
                        if (labelMap.containsKey(label))
                            JSONHandler.parseWordInto(results.get(i), labelMap.get(label));
                        else
                            Log.e(LOG_TAG, "Word update skipped (label = " + label + ").");
                    } catch (IllegalArgumentException e)
                    {
                        responseHandler.onLocalFailure(e);
                    }
                }

                // Move onto the next batch
                batchPushWords(allWords, index + BATCH_SIZE, responseHandler);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if (responseBody != null)
                    responseHandler.onRemoteFailure(NetworkError.build(statusCode, responseBody));
                else
                    responseHandler.onLocalFailure(error);
            }
        };

        // Perform the HTTP operation
        client.put(c, URL_DATA_DICTIONARY, body, APPLICATION_JSON, loggingWrapper(handler));
    }


    private boolean isNetworkReady()
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
    private static StringEntity buildEntity(JSONObject jObj)
    {
        try
        {
            StringEntity entity = new StringEntity(jObj.toString());

            entity.setContentType(APPLICATION_JSON);

            return entity;
        } catch (UnsupportedEncodingException e)
        {
            // TODO:  Do something?
            return null;
        }
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

}
