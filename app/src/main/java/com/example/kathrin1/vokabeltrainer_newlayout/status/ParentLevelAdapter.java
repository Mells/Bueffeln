package com.example.kathrin1.vokabeltrainer_newlayout.status;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.settings.SettingKontexttest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParentLevelAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<String> mListDataHeader;
    private final Map<String, List<String>> mListData_SecondLevel_Map;
    private final Map<String, List<String>> mListData_ThirdLevel_Map;

    public ParentLevelAdapter(Context mContext, List<String> mListDataHeader) {
        this.mContext = mContext;
        this.mListDataHeader = new ArrayList<>();
        this.mListDataHeader.addAll(mListDataHeader);

        // SECOND LEVEL
        String[] mItemHeaders;
        mListData_SecondLevel_Map = new HashMap<>();
        int parentCount = mListDataHeader.size();
        for (int i = 0; i < parentCount; i++) {
            String content = mListDataHeader.get(i);
            switch (content) {
                case "Camden Town I":
                    mItemHeaders = new String[]{"Welcome: Welcome to Camden Town!", "Chapter 1: At school", "Chapter 2: At home", 
                            "Chapter 3: Birthdays", "Chapter 4: Free time", "Chapter 5: Pets", "Chapter 6: Holidays"};
                    break;
                case "Camden Town II":
                    mItemHeaders = new String[]{"Chapter 1: After the holidays", "Chapter 2: Let’s get the party started", 
                            "Chapter 3: London", "Chapter 4: School life", "Chapter 5: Going green", "Chapter 6: Fun and games"};
                    break;
                case "Camden Town III":
                    mItemHeaders = new String[]{"Chapter 1: On the move", "Chapter 2: Welcome to Wales!", "Chapter 3: Famous Brits", 
                            "Chapter 4: Keep me posted", "Chapter 5: Diverse Britain", "Chapter 6: The Great Outdoors", };
                    break;
                default:
                    mItemHeaders = new String[]{"Welcome: Welcome to Camden Town!", "Chapter 1: At school", "Chapter 2: At home", 
                            "Chapter 3: Birthdays", "Chapter 4: Free time", "Chapter 5: Pets", "Chapter 6: Holidays"};
                    break;

            }
            mListData_SecondLevel_Map.put(mListDataHeader.get(i), Arrays.asList(mItemHeaders));
        }

        // THIRD LEVEL
        DatabaseManager dbManager = DatabaseManager.build(mContext);
        mListData_ThirdLevel_Map = new HashMap<>();
        String[] book = {"I", "II", "III"};
        String[] chapter1 = {"Welcome", "1", "2", "3", "4", "5", "6"};
        String[] chapter23 = {"1", "2", "3", "4", "5", "6"};
        int bookNumber;

        for (Object o : mListData_SecondLevel_Map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object object = entry.getValue();
            Log.d("secondLVl", object.toString());
            if(object.toString().startsWith("[Welcome: Welcome")){
                bookNumber = 0;
            }
            else if (object.toString().startsWith("[Chapter 1: On the move")){
                bookNumber = 1;
            }
            else {
                bookNumber = 2;
            }
            if (object instanceof List) {
                List<String> stringList = new ArrayList<>();
                Collections.addAll(stringList, (String[]) ((List) object).toArray());
                for (int c = 0; c < stringList.size(); c++) {
                    List<String> levelList = new ArrayList<>();
                    int numberOfWordsInLevel;
                    int numberOfWordsInChapter = 0;
                    int learnedWordsInNumber = 0;
                    float percentage = 0;
                    if (bookNumber == 0){
                        for(int l = 0; l <= 7; l++) {
                            numberOfWordsInLevel = (dbManager.getWordsByBookChapterLevel(book[bookNumber], chapter1[c], l)).size();
                            levelList.add("Level "+l+" Vokabeln: "+numberOfWordsInLevel);
                            learnedWordsInNumber = learnedWordsInNumber + (numberOfWordsInLevel * l);
                            numberOfWordsInChapter = numberOfWordsInLevel + numberOfWordsInChapter;
                        }
                    }
                    else {
                        for(int l = 0; l <= 7; l++) {
                            numberOfWordsInLevel = (dbManager.getWordsByBookChapterLevel(book[bookNumber], chapter23[c], l)).size();
                            levelList.add("Level "+l+" Vokabeln: "+numberOfWordsInLevel);
                            learnedWordsInNumber = learnedWordsInNumber + (numberOfWordsInLevel * l);
                            numberOfWordsInChapter = numberOfWordsInLevel + numberOfWordsInChapter;
                        }
                    }
                    percentage = learnedWordsInNumber / (numberOfWordsInChapter+7) * 100;
                    String color;
                    // no correct word
                    if (percentage == 0){
                        color = "#E33C17"; // red
                    }
                    else if (percentage <= 25.0)
                    {
                        color = "#E38017"; // orange
                    }
                    else if (percentage <= 50.0)
                    {
                        color = "#E3C417"; // yellow-ish
                    }
                    else if (percentage <= 75.0)
                    {
                        color = "#BEE317"; // light green
                    }
                    else if (percentage < 100)
                    {
                        color = "#65CF03"; // green
                    }
                    else {
                        color = "#1F8900";
                    }

                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                            .putString(stringList.get(c), color).commit();
                    mListData_ThirdLevel_Map.put(stringList.get(c), levelList);
                }
            }
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final CustomExpListView secondLevelExpListView = new CustomExpListView(this.mContext);
        String parentNode = (String) getGroup(groupPosition);
        secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.mContext, mListData_SecondLevel_Map.get(parentNode), mListData_ThirdLevel_Map));
        secondLevelExpListView.setGroupIndicator(null);
//        secondLevelExpListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            int previousGroup = -1;
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                if (groupPosition != previousGroup)
//                    secondLevelExpListView.collapseGroup(previousGroup);
//                previousGroup = groupPosition;
//            }
//        });
        return secondLevelExpListView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.status_drawer_list_group, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setTextColor(Color.BLACK);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
