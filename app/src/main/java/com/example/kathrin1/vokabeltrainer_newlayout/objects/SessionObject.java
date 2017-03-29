package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

import java.text.ParseException;
import java.util.Date;

/**
 * For holding all information relating to a single study session
 */
public class SessionObject
{
    private long id;
    private Date start, end;
    private String parseId;

    /**
     * Private constructor, use static constructor instead.
     */
    private SessionObject(long id, Date start, Date end)
    {
        if (start != null && end != null && !start.before(end))
            throw new IllegalArgumentException("Start time must be before ending time when" +
                                               "creating new SessionObject.");

        this.id = id;
        this.start = start;
        this.end = end;
    }

    /**
     * Static constructor, creates a new session object and returns it.
     *
     * @param id    ID of the session object
     * @param start Starting timestamp of the session
     * @param end   Ending timestamp of the session
     * @return The newly constructed SessionObject.
     */
    public static SessionObject build(long id, Date start, Date end)
    {
        return new SessionObject(id, start, end);
    }

    /**
     * Static constructor, creates a new session object without setting an ending timestamp.  Used
     * for currently ongoing sessions that have not yet completed.  If an object has been created
     * this way, {@link SessionObject#isFinished()} will return false, until it is finished
     * using {@link SessionObject#finish(Date)}.
     *
     * @param id    ID of the session object
     * @param start Starting timestamp of the session
     * @return The newly constructed SessionObject.
     */
    public static SessionObject buildUnfinished(long id, Date start)
    {
        return new SessionObject(id, start, null);
    }

    /**
     * Creates a blank session with the given ID, used for matching hashes.  Do NOT use for any
     * other purposes, as key fields will not be instantiated and NullPointerExceptions will occur.
     *
     * @param id The id to set for this placeholder
     * @return The newly constructed placeholder SessionObject
     */
    public static SessionObject buildPlaceholder(long id)
    {
        return new SessionObject(id, null, null);
    }

    /**
     * Finishes a currently unfinished session, setting the given ending timestamp.  If a ending
     * timestamp has already been set for this session, an {@link IllegalStateException} is
     * thrown.
     *
     * @param end The ending timestamp to set
     */
    public void finish(Date end)
    {
        if (isFinished())
            throw new IllegalStateException("SessionObject is already finished, cannot be finished again.");

        this.end = end;
    }

    /**
     * Returns whether this session has been finished or not (whether it has an ending timestamp
     * or not).
     *
     * @return Returns true if this session has been finished (has an ending timestamp), false
     * otherwise.
     */
    public boolean isFinished()
    {
        return end != null;
    }

    /**
     * Reads in the values at the current position in the given cursor to generate a new
     * SessionObject.
     *
     * @param cursor Database cursor at the position of the word to instantiate.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public static SessionObject build(Cursor cursor) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating SessionObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating SessionObject.");

        try
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.SESSION_ID));
            Date start = DBHandler.SQL_DATE.parse(
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SESSION_START)));
            Date end = DBHandler.SQL_DATE.parse(
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SESSION_END)));
            String parseId = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.SESSION_PARSEID));

            return build(id, start, end).setParseId(parseId);

        } catch (ParseException e)
        {
            throw new IllegalArgumentException("Error while parsing timestamp.");
        }

    }

    /**
     * Setter for the Parse ID of this study session.  Returns this object in order
     * to facilitate chaining.
     *
     * @param parseId The Parse ID of this session to set.
     * @return This SessionObject.
     */
    public SessionObject setParseId(String parseId)
    {
        this.parseId = parseId;
        return this;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Date getStart()
    {
        return start;
    }

    public Date getEnd()
    {
        return end;
    }

    public void setStart(Date start)
    {
        this.start = start;
    }

    public String getParseId()
    {
        return parseId;
    }

    public ContentValues getContentVals()
    {
        if (!isFinished())
            throw new IllegalStateException("Session should be finished before saving into " +
                                            "database.");

        ContentValues vals = new ContentValues();

        if (id > 0)
            vals.put(DBHandler.SESSION_ID, id);
        vals.put(DBHandler.SESSION_START, DBHandler.SQL_DATE.format(start));
        vals.put(DBHandler.SESSION_END, DBHandler.SQL_DATE.format(end));
        if (parseId != null)
            vals.put(DBHandler.SESSION_PARSEID, parseId);

        return vals;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionObject that = (SessionObject) o;

        return id == that.id;

    }

    @Override
    public int hashCode()
    {
        return (int) (id ^ (id >>> 32));
    }
}
