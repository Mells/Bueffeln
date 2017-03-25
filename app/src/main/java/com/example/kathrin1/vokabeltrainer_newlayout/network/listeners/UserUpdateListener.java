package com.example.kathrin1.vokabeltrainer_newlayout.network.listeners;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.UserObject;

/**
 * Listener for reporting results when attempting update a user
 */
public interface UserUpdateListener extends NetworkFailureListener
{
    void onSuccess(UserObject user);
}
