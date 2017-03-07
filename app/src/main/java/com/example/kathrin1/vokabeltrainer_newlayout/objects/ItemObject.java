package com.example.kathrin1.vokabeltrainer_newlayout.objects;

public class ItemObject {

    private int mId;
    private boolean mIsEnglish;
    private String mItemName;

    public ItemObject(int id, boolean isEnglish, String name) {
        mId = id;
        mIsEnglish = isEnglish;
        mItemName = name;
    }

    @Override
    public String toString() {
        return mItemName;
    }

    public int oldPos() {
        return mId;
    }

    public boolean language() {
        return mIsEnglish;
    }
}