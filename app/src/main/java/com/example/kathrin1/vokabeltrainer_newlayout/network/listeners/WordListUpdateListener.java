package com.example.kathrin1.vokabeltrainer_newlayout.network.listeners;

import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.List;

/**
 * Listener for reporting results when attempting retrieve dictionary items from remote database
 */

public interface WordListUpdateListener
{
    void onSuccess(List<VocObject> words);
    void onRemoteFailure(NetworkError error);
    void onLocalFailure(Throwable error);
}
