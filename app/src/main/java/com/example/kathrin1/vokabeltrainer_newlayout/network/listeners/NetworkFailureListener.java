package com.example.kathrin1.vokabeltrainer_newlayout.network.listeners;

import com.example.kathrin1.vokabeltrainer_newlayout.network.NetworkError;

/**
 * Template for other listeners that are checking for network failures
 */
public interface NetworkFailureListener
{
    void onRemoteFailure(NetworkError error);
    void onLocalFailure(Throwable error);
}
