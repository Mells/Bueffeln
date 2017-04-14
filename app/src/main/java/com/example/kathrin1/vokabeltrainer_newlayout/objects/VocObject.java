package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

public class VocObject {

    private int id;
    private String voc;
    private String translation;
    private String status;
    private String book;
    private String chapter;
    private String pos;
    private String lemma;
    private String sentences;
    private int tested;
    private String label; // Unique label for this word, for synchronizing between local and remote databases

    // For use with the learning model
    private String parseId; // ID of the word in remote Parse database
    private String userInfoParseId;
    private float beta_si; // Item+user difficulty
    private float beta_i; // Item difficulty
    private float alpha; // Activation intercept
    private float sigma; // Frequency modifier
    private Float activation; // Activation level.. uses wrapper Float to allow for null


    public VocObject(int id, String voc, String translation, String status, String book,
                     String chapter, String pos, String sentences, int tested, String lemma,
                     String label) {
        this.id = id;
        this.voc = voc;
        this.lemma = lemma;
        this.translation = translation;
        this.status = status;
        this.book = book;
        this.chapter = chapter;
        this.pos = pos;
        this.sentences = sentences;
        this.tested = tested;
        this.label = label;
    }

    /**
     * Reads in the values at the current position in the given cursor to generate a new
     * VocObject.
     *
     * @param cursor Database cursor at the position of the word to instantiate.
     * @throws IllegalArgumentException Throws an exception if there was an error reading the cursor.
     */
    public VocObject(Cursor cursor) throws IllegalArgumentException
    {
        if (cursor == null)
            throw new IllegalArgumentException("Cursor is null while creating VocObject.");
        if (cursor.isAfterLast() || cursor.isBeforeFirst())
            throw new IllegalArgumentException("Cursor is not pointing at data while creating VocObject.");

        id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.WORD_ID));
        voc = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_WORD));
        translation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TRANSLATION));
        status = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_STATUS));
        book = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_BOOK));
        chapter = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_CHAPTER));
        pos = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_POS));
        lemma = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_VOCLEMMA));
        sentences = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_SENTID));
        tested = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.WORD_LEVEL));
        parseId = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PARSEID));
        beta_si = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_BETA_si));
        beta_i = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_BETA_i));
        sigma = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_SIGMA));
        alpha = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_ALPHA));
        label = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_LABEL));
        userInfoParseId = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_USERINFO_PARSEID));
        activation = !cursor.isNull(cursor.getColumnIndexOrThrow(DBHandler.WORD_ACTIVATION))
                ? cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_ACTIVATION))
                : null;
    }

    public int getId() {
        return id;
    }

    public String getVoc() {
        return voc;
    }

    public String getLemma() { return lemma; }

    public String getTranslation() { return translation; }

    public String getStatus() { return status; }

    public String getBook() {
        return book;
    }

    public String getChapter() {
        return chapter;
    }

    public String getPOS() {
        return pos;
    }

    public String getSentences() {
        return sentences;
    }

    public int getTested() { return tested; }

    public String getLabel()
    {
        return label;
    }

    public String getParseId()
    {
        return parseId;
    }

    public boolean hasParseId() { return parseId != null && !parseId.equals(""); }

    public String getUserInfoParseId()
    {
        return userInfoParseId;
    }

    public void setUserInfoParseId(String userInfoParseId)
    {
        this.userInfoParseId = userInfoParseId;
    }

    public void setParseId(String parseId)
    {
        this.parseId = parseId;
    }

    public float getBeta_si()
    {
        return beta_si;
    }

    public float getBeta_i()
    {
        return beta_i;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public float getSigma()
    {
        return sigma;
    }

    public Float getActivation() { return activation; }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    public boolean isNew() {
        return activation == null || Float.isInfinite(activation);
    }

    /**
     * Sets all model parameters for this word.  Returns this object in order to facilitate chaining.
     *
     * @param beta_si
     * @param beta_i
     * @param alpha
     * @param sigma
     * @return This VocObject.
     */
    public VocObject setParameters(float beta_si, float beta_i, float alpha, float sigma)
    {
        this.beta_i = beta_i;
        this.beta_si = beta_si;
        this.alpha = alpha;
        this.sigma = sigma;
        return this;
    }

    public void setActivation(Float activation)
    {
        this.activation = activation;
    }

    public ContentValues getContentValues()
    {

        ContentValues vals = new ContentValues();

        if (id > 0)
            vals.put(DBHandler.WORD_ID, id);
        vals.put(DBHandler.WORD_WORD, voc);
        vals.put(DBHandler.WORD_TRANSLATION, translation);
        vals.put(DBHandler.WORD_VOCLEMMA, lemma);
        vals.put(DBHandler.WORD_STATUS, status);
        vals.put(DBHandler.WORD_BOOK, book);
        vals.put(DBHandler.WORD_CHAPTER, chapter);
        vals.put(DBHandler.WORD_POS, pos);
        vals.put(DBHandler.WORD_SENTID, sentences);
        vals.put(DBHandler.WORD_LEVEL, tested);
        vals.put(DBHandler.WORD_LABEL, label);
        vals.put(DBHandler.WORD_PARSEID, parseId);
        vals.put(DBHandler.WORD_BETA_si, beta_si);
        vals.put(DBHandler.WORD_BETA_i, beta_i);
        vals.put(DBHandler.WORD_ALPHA, alpha);
        vals.put(DBHandler.WORD_SIGMA, sigma);
        vals.put(DBHandler.WORD_ACTIVATION, activation);
        vals.put(DBHandler.WORD_USERINFO_PARSEID, userInfoParseId);

        return vals;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VocObject vocObject = (VocObject) o;

        return id == vocObject.id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "VocObject{" +
               "id=" + id +
               ", voc='" + voc + '\'' +
               ", lemma='" + lemma + '\'' +
               ", label='" + label + '\'' +
               ", parseId='" + parseId + '\'' +
               ", alpha=" + alpha +
               ", activation=" + activation +
               '}';
    }
}
