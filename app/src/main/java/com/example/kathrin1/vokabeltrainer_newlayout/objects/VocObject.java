package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class VocObject {

    private int id;

    private String voc;
    private String processedVocable;
    private String taggedProcessedVocable;
    private String processedVocableSoundex;
    private String lemmaVocable;
    private String taggedLemmaVocable;
    private String taggedLemmaSimpleVocable;

    private String translation;
    private String processedTranslation;
    private String taggedProcessedTranslation;
    private String processedTranslationSoundex;
    private String lemmaTranslation;
    private String taggedLemmaTranslation;

    private String status;
    private String book;
    private String chapter;
    private String pos;
    private String sentences_book;
    private String sentences_gdex;
    private String sentences_learner;
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


    public VocObject(int id, String voc, String processedVocable, String taggedProcessedVocable,
                     String processedVocableSoundex, String lemmaVocable,
                     String taggedLemmaVocable, String taggedLemmaSimpleVocable,
                     String translation, String processedTranslation,
                     String taggedProcessedTranslation, String processedTranslationSoundex,
                     String lemmaTranslation, String taggedLemmaTranslation, String status,
                     String book, String chapter, String pos, String sentences_book,
                     String sentences_gdex, String sentences_learner, int tested, String label) {
        this.id = id;

        this.voc = voc;
        this.processedVocable = processedVocable;
        this.taggedProcessedVocable = taggedProcessedVocable;
        this.processedVocableSoundex = processedVocableSoundex;
        this.lemmaVocable = lemmaVocable;
        this.taggedLemmaVocable = taggedLemmaVocable;
        this.taggedLemmaSimpleVocable = taggedLemmaSimpleVocable;

        this.translation = translation;
        this.processedTranslation = processedTranslation;
        this.taggedProcessedTranslation = taggedProcessedTranslation;
        this.processedTranslationSoundex = processedTranslationSoundex;
        this.lemmaTranslation = lemmaTranslation;
        this.taggedLemmaTranslation = taggedLemmaTranslation;

        this.status = status;
        this.book = book;
        this.chapter = chapter;
        this.pos = pos;
        this.sentences_book = sentences_book;
        this.sentences_gdex = sentences_gdex;
        this.sentences_learner = sentences_learner;
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
        processedVocable = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PROCESSED_VOCABLE));
        taggedProcessedVocable = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TAGGED_PROCESSED_VOCABLE));
        processedVocableSoundex = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PROCESSED_VOCABLE_SOUNDEX));
        lemmaVocable = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_LEMMA_VOCABLE));
        taggedLemmaVocable = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TAGGED_LEMMA_VOCABLE));
        taggedLemmaSimpleVocable = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TAGGED_LEMMA_SIMPLE_VOCABLE));

        translation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TRANSLATION));
        processedTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PROCESSED_TRANSLATION));
        taggedProcessedTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TAGGED_PROCESSED_TRANSLATION));
        processedTranslationSoundex = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_PROCESSED_TRANSLATION_SOUNDEX));
        lemmaTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_LEMMA_TRANSLATION));
        taggedLemmaTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_TAGGED_LEMMA_TRANSLATION));

        status = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_STATUS));
        book = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_BOOK));
        chapter = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_CHAPTER));
        pos = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_POS));

        sentences_book = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_BOOKID));
        sentences_gdex = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_GDEX));
        sentences_learner = cursor.getString(cursor.getColumnIndexOrThrow(DBHandler.WORD_LEARNER));

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

    public ArrayList getProcessedVocable() {return toProcessedArrayList(processedVocable);}

    public ArrayList getTaggedProcessedVocable() {
        return toTaggedProcessedArrayList(taggedProcessedVocable);
    }

    public ArrayList getProcessedVocableSoundex() {
        return toProcessedArrayList(processedVocableSoundex);
    }

    public ArrayList getLemmaVocable() { return toProcessedArrayList(lemmaVocable); }

    public ArrayList getTaggedLemmaVocable() {
        return toTaggedProcessedArrayList(taggedLemmaVocable);
    }

    public ArrayList getTaggedLemmaSimpleVocable() {
        return toTaggedProcessedArrayList(taggedLemmaSimpleVocable);
    }


    public String getTranslation() { return translation; }

    public ArrayList getProcessedTranslation() {
        return toProcessedArrayList(processedTranslation);
    }

    public ArrayList getTaggedProcessedTranslation() {
        return toTaggedProcessedArrayList(taggedProcessedTranslation);
    }

    public ArrayList getProcessedTranslationSoundex() {
        return toProcessedArrayList(processedTranslationSoundex);
    }

    public ArrayList getLemmaTranslation() {
        return toProcessedArrayList(lemmaTranslation);
    }

    public ArrayList getTaggedLemmaTranslation() {
        return toTaggedProcessedArrayList(taggedLemmaTranslation);
    }


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

    public String getOldSentences() { return sentences_book; }

    public String getGDEXSentences() {
        return sentences_gdex;
    }

    public String getLearnerSentences() { return sentences_learner; }

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
        vals.put(DBHandler.WORD_PROCESSED_VOCABLE, processedVocable);
        vals.put(DBHandler.WORD_TAGGED_PROCESSED_VOCABLE, taggedProcessedVocable);
        vals.put(DBHandler.WORD_PROCESSED_VOCABLE_SOUNDEX, processedVocableSoundex);
        vals.put(DBHandler.WORD_LEMMA_VOCABLE, lemmaVocable);
        vals.put(DBHandler.WORD_TAGGED_LEMMA_VOCABLE, taggedLemmaVocable);
        vals.put(DBHandler.WORD_TAGGED_LEMMA_SIMPLE_VOCABLE, taggedLemmaSimpleVocable);

        vals.put(DBHandler.WORD_TRANSLATION, translation);
        vals.put(DBHandler.WORD_PROCESSED_TRANSLATION, processedTranslation);
        vals.put(DBHandler.WORD_TAGGED_PROCESSED_TRANSLATION, taggedProcessedTranslation);
        vals.put(DBHandler.WORD_PROCESSED_TRANSLATION_SOUNDEX, processedTranslationSoundex);
        vals.put(DBHandler.WORD_LEMMA_TRANSLATION, lemmaTranslation);
        vals.put(DBHandler.WORD_TAGGED_LEMMA_TRANSLATION, taggedLemmaTranslation);

        vals.put(DBHandler.WORD_STATUS, status);
        vals.put(DBHandler.WORD_BOOK, book);
        vals.put(DBHandler.WORD_CHAPTER, chapter);
        vals.put(DBHandler.WORD_POS, pos);
        vals.put(DBHandler.WORD_BOOKID, sentences_book);
        vals.put(DBHandler.WORD_GDEX, sentences_gdex);
        vals.put(DBHandler.WORD_LEARNER, sentences_learner);
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
               ", lemma='" + lemmaVocable + '\'' +
               ", label='" + label + '\'' +
               ", parseId='" + parseId + '\'' +
               ", alpha=" + alpha +
               ", activation=" + activation +
               '}';
    }

    private static ArrayList toProcessedArrayList(String theString){
        ArrayList<ArrayList<String>> returnArray = new ArrayList<>();
        if (StringUtils.countMatches(theString, "[") > 1){
            theString = theString.substring(1, theString.length()-1);
            String[] stringArray = theString.split(", ");
            for (String s : stringArray){
                s = s.replace("[", "").replace("]", "");
                String h = s.substring(0,1);
                s = s.replace(h, "");
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(s);
                returnArray.add(arrayList);
            }
        }
        else {
            theString = theString.replace("[", "").replace("]", "")
                    .replace("'", "").replace("'", "");
            ArrayList<String> b = new ArrayList<>();
            b.add(theString);
            returnArray.add(b);
        }
        return returnArray;
    }

    private static ArrayList toTaggedProcessedArrayList(String theString){
        ArrayList<ArrayList<Pair>> returnArray = new ArrayList<>();
        if (theString.substring(0,2).equals("[[")){
            theString = theString.substring(1, theString.length()-1);
            String[] stringArray = theString.split("], \\[");
            for (String s : stringArray){
                s = s.replaceAll("\\[", "").replaceAll("]", "");
                String[] splitByTuple = s.split("\\),");
                ArrayList<Pair> pairs = new ArrayList<>();
                for (String tup : splitByTuple){
                    tup = tup.replaceAll("\\(", "").replaceAll("\\)", "").trim();
                    String[] inTuple = tup.split(", ", 2);
                    ArrayList<String> helperTupleArray = new ArrayList<>();
                    for (String t : inTuple){
                        t = t.replaceAll(t.substring(0,1), "");
                        helperTupleArray.add(t);
                    }
                    pairs.add(Pair.of(helperTupleArray.get(0), helperTupleArray.get(1)));
                }
                returnArray.add(pairs);
            }
        }
        else {
            String[] arrayString = theString.replace("[", "").replace("]", "")
                    .replace("'", "").replace("'", "").split("\\), ");
            ArrayList<Pair> pairs = new ArrayList<>();
            for (String s : arrayString){
                s = s.replaceAll("\\(", "").replaceAll("\\)", "");
                String[] t = s.split(", ", 2);
                pairs.add(Pair.of(t[0], t[1]));
            }
            returnArray.add(pairs);
        }
        return returnArray;
    }
}
