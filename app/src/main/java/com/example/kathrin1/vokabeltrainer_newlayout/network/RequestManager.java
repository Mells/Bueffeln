package com.example.kathrin1.vokabeltrainer_newlayout.network;

import android.content.Context;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Handles all requests to the remote database
 */
public class RequestManager
{
    public static final String APP_ID = "asdf";
    public static final int PORT = 1449;
    public static final String URL = "localhost:" + PORT;

    public static final String APPLICATION_JSON = "application/json";

    public static final String HEADER_APP_ID = "x-parse-application-id";
    public static final String HEADER_OBJECT_ID = "x-parse-object-id";
    public static final String HEADER_SESSION_TOKEN = "x-parse-session-token";

    public static final String PARAM_LAST_UPDATE = "lastUpdate";

    public static final String URL_DATA_DICTIONARY = URL + "/data/dictionary";



    private AsyncHttpClient client = new AsyncHttpClient();
    private Context c;

    /**
     * Private constructor, use static constructor instead.
     */
    private RequestManager(Context c)
    {
        this.c = c;
        client.addHeader(HEADER_APP_ID, APP_ID);
    }

    /**
     * Static constructor, creates a new request manager object and returns it.
     *
     * @param c The context within which to perform the operation
     * @return  The newly constructed RequestManager object.
     */
    public static RequestManager build(Context c)
    {
        return new RequestManager(c);
    }

    private static StringEntity buildEntity(JSONObject jObj)
    {
        try {
            StringEntity entity = new StringEntity(jObj.toString());

            entity.setContentType(APPLICATION_JSON);

            return entity;
        } catch (UnsupportedEncodingException e)
        {
            // TODO:  Do something?
            return null;
        }
    }



    public static void getDictionaryItems(Date lastUpdate, AsyncHttpResponseHandler responseHandler)
    {
        RequestParams params = new RequestParams();
        if (lastUpdate!=null)
            params.put(PARAM_LAST_UPDATE, DBHandler.ISO_DATE.format(lastUpdate));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_DATA_DICTIONARY, params, responseHandler);
    }

}
