package com.example.kathrin1.vokabeltrainer_newlayout.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.R;
import com.opencsv.CSVReader;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Manages creating and updating all tables in the database.  Also contains static strings for
 * referencing all columns in the tables.
 */
public class DBHandler extends SQLiteAssetHelper
{
    // All strings that would be used as keys are established here as constants.
    // This is more robust against coding typos, and allows for easier renaming, if necessary.
    // Also, keeps information about all columns in one place, for reference.


    /**
     * WORDS TABLE
     * ======================================================================
     */
    public static final String WORD_TABLENAME = "vocabulary";
    public static final String WORD_ID = "_id";
    public static final String WORD_WORD = "Vokabel";
    public static final String WORD_TRANSLATION = "Uebersetzung";
    public static final String WORD_VOCLEMMA = "VocLemma";
    public static final String WORD_STATUS = "Status";
    public static final String WORD_BOOK = "Band";
    public static final String WORD_CHAPTER = "Fundstelle";
    public static final String WORD_POS = "Wortart";
    public static final String WORD_EXAMPLE = "Beispielsatz";
    public static final String WORD_NOTE = "Hinweis";
    public static final String WORD_TAGGED = "Tagged";
    public static final String WORD_LEMMA = "Lemma";
    public static final String WORD_SENTID = "SentId";
    public static final String WORD_GDEX = "GDEX";
    public static final String WORD_LEARNER = "Learner";
    public static final String WORD_LEVEL = "Tested";
    public static final String WORD_LABEL = "label"; // Unique identifier for synchronization
    public static final String WORD_PARSEID = "ParseId"; // ID of word in Parse database
    public static final String WORD_USERINFO_PARSEID = "userInfoParseId"; // ID of user+word info in Parse database
    public static final String WORD_BETA_si = "beta_si"; // Item+user difficulty
    public static final String WORD_BETA_i = "beta_i"; // Item difficulty
    public static final String WORD_ALPHA = "alpha_i"; // Activation intercept for word
    public static final String WORD_ALPHA_d = "alpha_d"; // Default activation of word (DEPRECATED)
    public static final String WORD_SIGMA = "sigma_i"; // Frequency modifier for word
    public static final String WORD_ACTIVATION = "activation"; // Activation of the word
    public static final String[] WORD_COLUMNS = {WORD_ID, WORD_WORD, WORD_TRANSLATION,
                                                 WORD_VOCLEMMA, WORD_STATUS, WORD_BOOK,
                                                 WORD_CHAPTER, WORD_POS, WORD_SENTID,
                                                 WORD_GDEX, WORD_LEARNER,
                                                 WORD_PARSEID, WORD_BETA_si, WORD_BETA_i,
                                                 WORD_ALPHA, WORD_SIGMA, WORD_LABEL,
                                                 WORD_EXAMPLE, WORD_NOTE, WORD_TAGGED,
                                                 WORD_LEVEL, WORD_LEMMA, WORD_ACTIVATION,
                                                 WORD_USERINFO_PARSEID};

    public static final String INDEX_WORD_LABEL = "index_word_label";
    public static final String INDEX_WORD_PARSEID = "index_word_parseid";

    // NO LONGER USED
    final private static String CREATE_WORD_TABLE =

            "CREATE TABLE " + WORD_TABLENAME + " (" +
            WORD_ID + " INTEGER NOT NULL, " +
            WORD_WORD + " TEXT, " +
            WORD_TRANSLATION + " TEXT, " +
            WORD_VOCLEMMA + " TEXT, " +
            WORD_STATUS + " TEXT, " +
            WORD_BOOK + " TEXT, " +
            WORD_CHAPTER + " TEXT, " +
            WORD_POS + " TEXT, " +
            WORD_EXAMPLE + " TEXT, " +
            WORD_NOTE + " TEXT, " +
            WORD_TAGGED + " TEXT, " +
            WORD_LEMMA + " TEXT, " +
            WORD_SENTID + " TEXT, " +
            WORD_GDEX + " TEXT, " +
            WORD_LEARNER + " TEXT, " +
            WORD_LABEL + " TEXT, " +
            WORD_LEVEL + " INTEGER DEFAULT 0, " +
            WORD_PARSEID + " TEXT DEFAULT '', " +
            WORD_USERINFO_PARSEID + " TEXT DEFAULT '', " +
            WORD_BETA_si + " DECIMAL DEFAULT 0.0, " +
            WORD_BETA_i + " DECIMAL DEFAULT 0.0, " +
            WORD_ALPHA + " DECIMAL DEFAULT 0.0, " +
            WORD_SIGMA + " DECIMAL DEFAULT 0.0, " +
            WORD_ACTIVATION + " DECIMAL, " + // If not filled, treated as negative infinity
            "PRIMARY KEY (" + WORD_ID + ") )";


    /**
     * SENTENCES TABLE
     * ======================================================================
     */
    public static final String SENT_TABLENAME = "sentences";
    public static final String SENT_TABLENAME_OLD = "sentences_old";
    public static final String SENT_ID = "_id";
    public static final String SENT_BOOK = "Book";
    public static final String SENT_PAGE = "Page";
    public static final String SENT_CHAPTER = "Chapter";
    public static final String SENT_SENTENCE = "Sentence";
    public static final String SENT_TAGGED = "Tagged";
    public static final String SENT_MAPPED = "Mapped";
    public static final String SENT_LEMMA = "Lemma";
    public static final String[] SENT_COLUMNS = {SENT_ID, SENT_BOOK, SENT_CHAPTER, SENT_PAGE,
                                                 SENT_SENTENCE, SENT_TAGGED, SENT_MAPPED, SENT_LEMMA};

    // NO LONGER USED
    final private static String CREATE_SENT_TABLE =

            "CREATE TABLE " + SENT_TABLENAME + " (" +
            SENT_ID + " INTEGER NOT NULL, " +
            SENT_BOOK + " TEXT, " +
            SENT_CHAPTER + " TEXT, " +
            SENT_PAGE + " INTEGER, " +
            SENT_SENTENCE + " TEXT, " +
            SENT_TAGGED + " TEXT, " +
            SENT_MAPPED + " TEXT, " +
            SENT_LEMMA + " TEXT, " +
            "PRIMARY KEY (" + SENT_ID + ") )";


    /**
     * STUDY SESSIONS TABLE
     * ======================================================================
     */
    public static final String SESSION_TABLENAME = "study_sessions";
    public static final String SESSION_ID = "_id";
    public static final String SESSION_START = "start"; // Starting time of the study session
    public static final String SESSION_END = "end"; // Ending time of the study session
    public static final String SESSION_PARSEID = "parseId"; // ID of the session in Parse database
    public static final String[] SESSION_COLUMNS = {SESSION_ID, SESSION_START, SESSION_END,
                                                    SESSION_PARSEID};


    // NO LONGER USED
    private static final String CREATE_SESSION_TABLE =

            "CREATE TABLE " + SESSION_TABLENAME + " (" +
            SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SESSION_START + " TIMESTAMP, " +
            SESSION_END + " TIMESTAMP, " +
            SESSION_PARSEID + " TEXT)";


    /**
     * INTERACTIONS TABLE
     * ======================================================================
     */
    public static final String INTERX_TABLENAME = "interactions";
    public static final String INTERX_ID = "_id";
    public static final String INTERX_WORD = "ortho"; // Orthography of the interaction's word
    public static final String INTERX_WORD_DBID = "db_id"; // ID of word in local database
    public static final String INTERX_WORD_PARSEID = "parse_id"; // ID of word in Parse database
    public static final String INTERX_LATENCY = "latency"; // Reaction time of the interaction
    public static final String INTERX_TIME = "timestamp"; // Timestamp of the interaction
    public static final String INTERX_RESULT = "result"; // Result of the interaction
    public static final String INTERX_CHARCOUNT = "charCount"; // Number of characters in context
    public static final String INTERX_EXERCISE_TYPE = "exerciseType"; // Type of exercise
    public static final String INTERX_SESSION = "session"; // Session of this interaction
    public static final String INTERX_PREALPHA = "preAlpha"; // Alpha before interaction
    public static final String INTERX_POSTALPHA = "postAlpha"; // Alpha after interaction
    public static final String INTERX_PREACTIVATION = "preActivation"; // Activation before interaction
    public static final String[] INTERX_COLUMNS = {INTERX_ID, INTERX_WORD, INTERX_WORD_DBID,
                                                   INTERX_WORD_PARSEID, INTERX_LATENCY,
                                                   INTERX_TIME, INTERX_RESULT, INTERX_CHARCOUNT,
                                                   INTERX_EXERCISE_TYPE, INTERX_SESSION,
                                                   INTERX_PREALPHA, INTERX_POSTALPHA,
                                                   INTERX_PREACTIVATION};

    // NO LONGER USED
    private static final String CREATE_INTERX_TABLE =

            "CREATE TABLE " + INTERX_TABLENAME + " (" +
            INTERX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            INTERX_WORD + " TEXT, " +
            INTERX_WORD_DBID + " INTEGER, " +
            INTERX_WORD_PARSEID + " TEXT, " +
            INTERX_LATENCY + " INTEGER, " +
            INTERX_TIME + " TIMESTAMP, " +
            INTERX_CHARCOUNT + " INTEGER, " +
            INTERX_EXERCISE_TYPE + " TEXT, " +
            INTERX_RESULT + " TEXT, " +
            INTERX_SESSION + " INTEGER, " +
            INTERX_PREALPHA + " DECIMAL, " +
            INTERX_POSTALPHA + " DECIMAL, " +
            INTERX_PREACTIVATION + " DECIMAL, " +
            "FOREIGN KEY (" + INTERX_SESSION + ") " +
            "REFERENCES " + SESSION_TABLENAME + "(" + SESSION_ID + ") )";

    //TODO
    /**
     * MORPHO TABLE
     * ======================================================================
     */
    public static final String MORPH_TABLENAME = "xtagmorph";
    public static final String MORPH_ID = "_id";
    public static final String MORPH_WORD = "Word";
    public static final String MORPH_LEMMA = "Lemma";
    public static final String MORPH_READING1 = "Reading1";
    public static final String MORPH_READING2 = "Reading2";
    public static final String MORPH_READING3 = "Reading3";
    public static final String MORPH_READING4 = "Reading4";
    public static final String MORPH_READING5 = "Reading5";
    public static final String MORPH_READING6 = "Reading6";
    public static final String MORPH_READING7 = "Reading7";
    public static final String MORPH_READING8 = "Reading8";
    public static final String[] MORPH_COLUMNS = {MORPH_ID, MORPH_WORD, MORPH_LEMMA, MORPH_READING1,
            MORPH_READING2, MORPH_READING3, MORPH_READING4, MORPH_READING5, MORPH_READING6,
            MORPH_READING7, MORPH_READING8};

    // NO LONGER USED
    final private static String CREATE_MORPH_TABLE =

            "CREATE TABLE " + MORPH_TABLENAME + " (" +
                    MORPH_ID + " INTEGER NOT NULL, " +
                    MORPH_WORD + " TEXT, " +
                    MORPH_LEMMA + " TEXT, " +
                    MORPH_READING1 + " TEXT, " +
                    MORPH_READING2 + " TEXT, " +
                    MORPH_READING3 + " TEXT, " +
                    MORPH_READING4 + " TEXT, " +
                    MORPH_READING5 + " TEXT, " +
                    MORPH_READING6 + " TEXT, " +
                    MORPH_READING7 + " TEXT, " +
                    MORPH_READING8 + " TEXT, " +
                    "PRIMARY KEY (" + MORPH_ID + ") )";


    // CSV file paths
    private static final String WORD_CSV = "databases/vocabulary.csv";
    private static final String SENT_CSV = "databases/sentences.csv";
    private static final String MORPH_CSV = "databases/xtagmorph.csv";

    // Date formats.  The timezones of these formats are set to UTC inside the constructor
    public static final SimpleDateFormat ISO_DATE =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.GERMANY);
    public static final SimpleDateFormat SQL_DATE =
            new SimpleDateFormat("'TIMESTAMP'''yyyy-MM-dd HH:mm:ss''", Locale.GERMANY);


    private static final String LOG_TAG = "[DBHandler]";


    // Name of the database in storage
    private static final String NAME = "vokabel.db";


    // INCREMENT THIS VALUE TO FORCE UPDATE
    // ======================================
    private static final int VERSION = 17;
    // ======================================

    private static final int FORCED_UPGRADE_VERSION = 17;


    private final Context c;


    /**
     * Constructor.
     */
    public DBHandler(Context context)
    {
        super(context, NAME, null, VERSION);
        this.c = context;
        setForcedUpgrade(FORCED_UPGRADE_VERSION);

        ISO_DATE.setTimeZone(TimeZone.getTimeZone("UTC"));
        SQL_DATE.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        /*
        // VERSION 2 UPGRADE
        // ==================
        if (oldVersion < 2 && newVersion >= 2)
        {
            db.execSQL(String.format("alter table %s add %s numeric",
                                     WORD_TABLENAME, WORD_ACTIVATION));

            ContentValues vals = new ContentValues();
            vals.put(WORD_ALPHA, 0f);
            db.update(WORD_TABLENAME, vals, null, null);

            db.execSQL(String.format("alter table %s add %s integer",
                                     INTERX_TABLENAME, INTERX_CHARCOUNT));
            db.execSQL(String.format("alter table %s add %s text",
                                     INTERX_TABLENAME, INTERX_EXERCISE_TYPE));

        }


        // VERSION 3 UPGRADE
        // ==================
        if (oldVersion < 3 && newVersion >= 3)
        {
            db.execSQL(String.format("alter table %s add %s text",
                                     WORD_TABLENAME, WORD_LABEL));

            db.execSQL(String.format("update %s set %s = %s",
                                     WORD_TABLENAME, WORD_LABEL, WORD_ID));

            db.execSQL(String.format("alter table %s add %s text",
                                     WORD_TABLENAME, WORD_USERINFO_PARSEID));

            db.execSQL(String.format("update %s set %s = %s",
                                     WORD_TABLENAME, WORD_USERINFO_PARSEID, "''"));

            Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
        }

        // VERSION 4 UPGRADE
        // ==================
        if (oldVersion < 4 && newVersion >= 4)
        {
            db.execSQL(String.format("create unique index %s on %s (%s)",
                                     INDEX_WORD_LABEL, WORD_TABLENAME, WORD_LABEL));

            Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
        }

        // VERSION 5 UPGRADE
        // ==================
        if (oldVersion < 5 && newVersion >= 5)
        {

        }

        // VERSION 6 UPGRADE
        // ==================
        if (oldVersion < 6 && newVersion >= 6)
        {
            db.execSQL(String.format("create unique index %s on %s (%s)",
                                     INDEX_WORD_PARSEID, WORD_TABLENAME, WORD_PARSEID));

            Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
        }

        // VERSION 7 UPGRADE
        // ==================
        if (oldVersion < 7 && newVersion >= 7)
        {
            db.execSQL(String.format("alter table %s add %s decimal",
                                     INTERX_TABLENAME, INTERX_PREALPHA));
            db.execSQL(String.format("update %s set %s = 0.0",
                                     INTERX_TABLENAME, INTERX_PREALPHA));
            db.execSQL(String.format("alter table %s add %s decimal",
                                     INTERX_TABLENAME, INTERX_POSTALPHA));
            db.execSQL(String.format("update %s set %s = 0.0",
                                     INTERX_TABLENAME, INTERX_POSTALPHA));
            db.execSQL(String.format("alter table %s add %s decimal",
                                     INTERX_TABLENAME, INTERX_PREACTIVATION));
            db.execSQL(String.format("update %s set %s = 0.0",
                                     INTERX_TABLENAME, INTERX_PREACTIVATION));

            Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
        }

        */

        // VERSION 16 UPGRADE
        // ==================
        /*if (oldVersion < 16 && newVersion >= 16)
        {
            db.execSQL(CREATE_MORPH_TABLE);
            Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
        }*/

        // This denotes which versions should not include CSV updates.  This could be simplified,
        // but I think it's clearer when each ignored version is specifically listed
        if (newVersion != 8
                && newVersion != 9
                && newVersion != 10
                && newVersion != 11
                && newVersion != 12
                && newVersion != 13
                && newVersion != 14
                && newVersion != 15
                && newVersion != 16
                && newVersion != 17)
        {
            // Whenever the version number of the database increases, synchronize with the CSV files
            syncWithCSV(db);
        }

        Log.d(LOG_TAG, String.format("Updated database to version [%d].", newVersion));
    }


    /**
     * Copies values from CSV files in the assets folder into the local database.  Used when
     * upgrading the database.  Keeps a progress dialog open while working.
     *
     * @param db The database to write the CSV values into.
     */
    public void syncWithCSV(final SQLiteDatabase db)
    {
        // Evidently ProgressDialog is pseudo-deprecated and discouraged, but I didn't
        // feel like figuring out a different solution

        Log.d(LOG_TAG, "Loading CSV files asynchronously...");

        // Opens an indeterminate progress dialog
        final ProgressDialog dialog =
                ProgressDialog.show(c,
                                    c.getString(R.string.DBHandler_ProgressDialog_Title),
                                    c.getString(R.string.DBHandler_ProgressDialog_Message),
                                    true);

        // Creates a task to perform in the background on a non-UI thread and executes it
        (new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                readCSVIntoTable(db, WORD_TABLENAME, WORD_CSV);
                readCSVIntoTable(db, SENT_TABLENAME, SENT_CSV);
                //TODO
                readCSVIntoTable(db, MORPH_TABLENAME, MORPH_CSV);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                Log.d(LOG_TAG, "Finished loading CSV files.");

                // Dismisses the dialog once finished
                dialog.dismiss();
            }
        }).execute();
    }


    /**
     * Reads the CSV file at the given asset path, inserting the values into the given table of
     * the given database.  CSV file should be preceded by two formatting rows:  The first row
     * should contain the headers for each column, and the second row should contain the data
     * type of the column (int, string, float, time, id).
     * <p>
     * Columns marked as containing 'time' should contain strings in the following format:
     * yyyy-MM-dd'T'HH:mm:ssZ
     * eg. 2017-03-11'T'12:00:00Z
     * <p>
     * Columns marked as containing 'id' are used as reference.  ID values that already exist in
     * the database are updated with the values given in that row, otherwise a new entry is
     * created.  The csv file must have exactly one column marked as ID, and is assumed to contain
     * integer values.
     *
     * @param db        The database to insert values into
     * @param tableName The table to insert values into
     * @param csvPath   The path to the CSV file to read
     */
    private void readCSVIntoTable(SQLiteDatabase db, String tableName, String csvPath)
    {
        AssetManager am = c.getAssets();

        // Initialize word values from CSV file
        CSVReader reader;
        try
        {
            InputStream csvStream = am.open(csvPath);
            reader = new CSVReader(new InputStreamReader(csvStream));

            String[] columns = reader.readNext(); // First row contains column names
            String[] dataTypes = reader.readNext(); // Second row contains data types of columns

            String idColumn = null;

            // Convert data type strings to lowercase, for consistency
            // Also, identify the id column
            for (int i = 0; i < dataTypes.length; i++)
            {
                dataTypes[i] = dataTypes[i].trim().toLowerCase();
                if (dataTypes[i].equals("id"))
                {
                    if (idColumn == null)
                        idColumn = columns[i];
                    else
                        throw new DatabaseHandlerException("CSV file should have only one 'id' column");
                }
            }

            // If no column with the 'id' type is found, throw error
            if (idColumn == null)
                throw new DatabaseHandlerException("CSV file is missing column with 'id' type.");


            if (columns.length != dataTypes.length)
                throw new DatabaseHandlerException("Data type row length does not equal column " +
                                                   "header row length.");


            String[] line;
            // Read through all remaining lines in the CSV file
            while ((line = reader.readNext()) != null)
            {
                if (line.length != columns.length)
                    continue;

                ContentValues vals = new ContentValues();

                int itemId = -1;

                // Iterate through all columns
                for (int i = 0; i < line.length; i++)
                {
                    // Add the column item to the value collection after converting from string,
                    // if necessary
                    switch (dataTypes[i].toLowerCase())
                    {
                        case "int":
                            vals.put(columns[i], Integer.parseInt(line[i]));
                            break;
                        case "float":
                            vals.put(columns[i], Float.parseFloat(line[i]));
                            break;
                        case "time":
                            vals.put(columns[i], SQL_DATE.format(ISO_DATE.parse(line[i])));
                            break;
                        case "id":
                            itemId = Integer.parseInt(line[i]);
                            vals.put(idColumn, itemId);
                            break;
                        case "string":
                            vals.put(columns[i], line[i]);
                            break;
                        default:
                            throw new DatabaseHandlerException(String.format("Invalid data type in column %d: '%s'",
                                                                             i, dataTypes[i]));
                    }
                }

                // Attempt to insert
                long id = db.insertWithOnConflict(tableName, null, vals, SQLiteDatabase.CONFLICT_IGNORE);
                // If there was an ID conflict (ID was already in the table), update existing row
                if (id < 0)
                    db.update(tableName, vals, idColumn + " = " + itemId, null);

                // Could use SQLiteDatabase.CONFLICT_REPLACE here instead of CONFLICT_IGNORE, but
                // this would force values in columns not mentioned by the csv file to null.  Since
                // the columns relating to model values aren't going to be in the csv file, this
                // would wipe existing user data inadvertently.

            }

            reader.close();

        } catch (IOException e)
        {
            Log.e("[DBHandler]", "Error reading CSV file.", e);
        } catch (ParseException e)
        {
            Log.e("[DBHandler]", "Error parsing date/time string.", e);
        }
    }


    /**
     * An exception that indicates there was an error with handling the database.
     */
    public static class DatabaseHandlerException extends SQLiteException
    {

        public DatabaseHandlerException()
        {
            super();
        }

        public DatabaseHandlerException(String error)
        {
            super(error);
        }
    }
}
