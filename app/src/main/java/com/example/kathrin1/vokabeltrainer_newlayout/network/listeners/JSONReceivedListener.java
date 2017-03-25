package com.example.kathrin1.vokabeltrainer_newlayout.network.listeners;

import org.json.JSONObject;

/**
 * Listener for reporting successful retrieval of a JSON response from the remote server
 */
public interface JSONReceivedListener
{
    void onJSONReceived(JSONObject jObj);
}
