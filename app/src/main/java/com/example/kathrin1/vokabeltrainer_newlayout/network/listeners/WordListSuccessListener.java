package com.example.kathrin1.vokabeltrainer_newlayout.network.listeners;

import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.Collection;

/**
 * Simple listener for reporting success with a word list
 */
public interface WordListSuccessListener
{
    void onSuccess(Collection<VocObject> words);
}
