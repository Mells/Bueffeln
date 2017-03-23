package com.example.kathrin1.vokabeltrainer_newlayout.learnmodel;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

import java.text.ParseException;
import java.util.Date;

/**
 * For retrieving and storing values stored locally.
 */

public class StoredValueManager
{
    public static final String PREFS_FILE = "ModelPrefs";
    public static final String LAST_UPDATE = "lastUpdate";


    private final Context c;
    private final SharedPreferences prefs;



    /**
     * Private constructor, use static constructor instead.
     */
    private StoredValueManager(Context c)
    {
        this.c = c;
        prefs = c.getSharedPreferences(PREFS_FILE, 0);
    }

    /**
     * Static constructor, creates a new stored value manager object and returns it.
     *
     * @return  The newly constructed StoredValueManager object.
     */
    public static StoredValueManager build(Context c)
    {
        return new StoredValueManager(c);
    }


    public Date getLastUpdate()
    {
        String update = prefs.getString(LAST_UPDATE, null);

        if (update == null)
            return null;

        try {
            return DBHandler.ISO_DATE.parse(update);
        } catch (ParseException e) {
            return null;
        }
    }

    public void storeLastUpdate(Date update)
    {
        prefs.edit()
             .putString(LAST_UPDATE, DBHandler.ISO_DATE.format(update))
             .apply();
    }
}
