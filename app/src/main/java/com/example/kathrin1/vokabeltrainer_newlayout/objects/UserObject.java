package com.example.kathrin1.vokabeltrainer_newlayout.objects;

/**
 * Used for storing information about an anonymous user for communicating with remote Parse server.
 */
public class UserObject
{
    private String objectId;
    private String anonId;
    private String sessionToken;

    /**
     * Private constructor, use static constructor instead.
     */
    private UserObject(String objectId, String anonId, String sessionToken)
    {
        this.objectId = objectId;
        this.anonId = anonId;
        this.sessionToken = sessionToken;
    }

    /**
     * Static constructor, creates a new user object object and returns it.
     *
     * @return  The newly constructed UserObject object.
     */
    public static UserObject build(String objectId, String anonId, String sessionToken)
    {
        return new UserObject(objectId, anonId, sessionToken);
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    public String getAnonId()
    {
        return anonId;
    }

    public void setAnonId(String anonId)
    {
        this.anonId = anonId;
    }

    public String getSessionToken()
    {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    @Override
    public String toString()
    {
        return "UserObject{" +
               "objectId='" + objectId + '\'' +
               ", anonId='" + anonId + '\'' +
               ", sessionToken='" + sessionToken + '\'' +
               '}';
    }
}
