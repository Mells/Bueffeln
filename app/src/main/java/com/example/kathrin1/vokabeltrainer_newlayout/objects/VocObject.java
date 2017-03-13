package com.example.kathrin1.vokabeltrainer_newlayout.objects;

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
    private int tested; // TODO: Remove this eventually

    // For use with the learning model
    private String parseId; // ID of the word in remote Parse database
    private float beta_si; // Item+user difficulty
    private float beta_i; // Item difficulty
    private Float alpha; // Activation level.. uses wrapper Float to allow for null
    private float alpha_d; // Default activation
    private float sigma; // Frequency modifier



    public VocObject(int id, String voc, String lemma, String translation, String status, String book, String chapter, String pos,
                     String sentences, int tested) {
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
        tested = cursor.getInt(cursor.getColumnIndexOrThrow(DBHandler.WORD_LEVEL)); // TODO: remove
        parseId = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PARSEID));
        beta_si = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_BETA_si));
        beta_i = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_BETA_i));
        alpha_d = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_ALPHA_d));
        sigma = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_SIGMA));
        alpha = !cursor.isNull(cursor.getColumnIndexOrThrow(DBHandler.WORD_ALPHA))
                ? cursor.getFloat(cursor.getColumnIndexOrThrow(DBHandler.WORD_ALPHA))
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

    public String getParseId()
    {
        return parseId;
    }

    public float getBeta_si()
    {
        return beta_si;
    }

    public float getBeta_i()
    {
        return beta_i;
    }

    public Float getAlpha()
    {
        return alpha;
    }

    public float getAlpha_d()
    {
        return alpha_d;
    }

    public float getSigma()
    {
        return sigma;
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
}
