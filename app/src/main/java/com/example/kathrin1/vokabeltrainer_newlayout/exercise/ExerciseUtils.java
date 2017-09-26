package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.BookObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * For housing some useful static methods used by exercises
 */
public abstract class ExerciseUtils
{
    public enum Answer
    {
        CORRECT, INCORRECT, CLOSE
    }


    /**
     * Generates the sentence to display on a screen using the given sentence, replacing the given
     * word in the sentence with a blank.
     *
     * @param sentence The sentence to replace words in
     * @param word     The word (or words) to replace in the sentence
     * @return The generated string to display
     */
    public static String deleteWordFromSentence(BookObject sentence, VocObject word)
    {
        return replaceWordInSentence(sentence, word, "____");
    }

    /**
     * Generates the sentence to display on a screen using the given sentence, replacing the given
     * word in the sentence with the given string.
     * // TODO:  This more or less gets the job done, but could use improvement.
     * // TODO:  Example:  For the word/phrase 'a week' (in der Woche), all instances of 'a' get erroneously replaced
     *
     * @param sentence          The sentence to replace words in
     * @param word              The word (or words) to replace in the sentence
     * @param replacementString The string to replace the text with.  If the string '%s' is
     *                          included, any such instances are replaced with the word that is
     *                          being replaced, allowing for wrapping the word in HTML tags.
     * @return The generated string to display
     */
    public static String replaceWordInSentence(BookObject sentence, VocObject word,
                                               String replacementString)
    {
        Map<String, List<String>> lemmaToken = DBUtils.stringOfJSONTagsToMap(sentence.getLemmaToken());

        //List<String> lemmaVocList = DBUtils.splitJSONListString(word.getLemmaVocable().toString());
        List<String> lemmaVocList = new ArrayList<>();
        for (Object o : word.getLemmaVocable()){
            if (o instanceof List<?>){
                for (Object i : (List) o){
                    lemmaVocList.add((String) i);
                }
            }
            else {
                lemmaVocList.add((String) o);
            }
        }
        String sent = sentence.getSentence();

        // Cleans up spaces before punctuation
        sent = sent.replaceAll("\\s([.,!?])", "$1");

        Log.d("ExerciseUtils: lemaToke", String.valueOf(lemmaToken));
        Log.d("ExerciseUtils: lemVocLi", String.valueOf(lemmaVocList));

        for (String lemma : lemmaVocList)
        {
            if (lemmaToken.containsKey(lemma))
            {
                List<String> bla = lemmaToken.get(lemma);
                for (String s : bla)
                {
                    sent = sent.replaceAll("\\b" + s + "\\b", replacementString.replace("%s", s));

                    // This strategy of using a Matcher object allows for case-insensitive matching,
                    // while preserving case throughout the string
                    /*Pattern pattern = Pattern.compile("\\b" + s.toLowerCase() + "\\b");
                    Matcher matcher = pattern.matcher(sent.toLowerCase());

                    int prevEnd = 0;
                    StringBuilder sb = new StringBuilder();
                    while (matcher.find())
                    {
                        sb.append(sent, prevEnd, matcher.start());
                        sb.append(replacementString.replace("%s", s));

                        prevEnd = matcher.end();
                    }
                    sb.append(sent, prevEnd, sent.length());

                    sent = sb.toString();*/
                }
            }
        }
        return sent;
    }

    /**
     * Generates displayable spanned text from the given HTML string.
     *
     * @param html The HTML text to convert
     * @return The generated spanned text
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html)
    {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }
        else
        {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * Determines whether the given input string is correct for the given word.
     * //TODO:  This could probably be improved
     *
     * @param sentence The context sentence that the word was presented in
     * @param word     The word that the user is trying to get the correct answer for
     * @param input    The user's input
     * @param german   True if the user is looking for the German exercise_translation, false otherwise.
     * @return Returns true if the answer is deemed correct, false otherwise.
     */
    public static Answer isAnswerCorrect(BookObject sentence, VocObject word, String input, boolean german)
    {
        String wordString = german ? word.getTranslation()
                                   : word.getVoc();


        input = input.toLowerCase().trim();

        Map<String, List<String>> smap = DBUtils.stringOfJSONTagsToMap(sentence.getLemmaToken());

        List<String> wordsToMatch;
        if (!german && smap.containsKey(wordString))
            wordsToMatch = smap.get(wordString);
        else
            wordsToMatch = Collections.singletonList(wordString);

        boolean foundClose = false;
        for (String wordToMatch : wordsToMatch)
        {
            // Remove:
            // 1.) Parentheses and all content inside them
            // 2.) The 'to' in the infinitives of verbs
            // 3.) Leading and trailing whitespace
            // and force into lowercase // TODO:  Maybe want to do some case matching for proper nouns?
            wordToMatch = wordToMatch.replaceAll("\\(.*\\)|^to\\s", "").trim().toLowerCase();


            if (wordToMatch.equals(input))
                return Answer.CORRECT;
            else if (LevenshteinDistance.getDefaultInstance().apply(input, wordToMatch) <= 2)
                foundClose = true;
        }
        if (foundClose)
            return Answer.CLOSE;
        else
            return Answer.INCORRECT;
    }

    /**
     * Determines the current tab of the given tab layout, saving the book_book associated with the tab
     * as the currently selected book_book.
     *
     * @param c Context within which to perform the operation
     * @param tab The tab layout tab to use to determine the selected book_book
     */
    public static void updateBook(Context c, TabLayout.Tab tab) {
        String b = "I";
        switch (tab.getPosition()){
            case 0: b = "I";
                break;
            case 1: b = "II";
                break;
            case 2: b = "III";
                break;
        }
        PreferenceManager.getDefaultSharedPreferences(c).edit()
                         .putString("book_book", b).commit();
    }

    /**
     * Set the current Tab
     * @param c Context within which to perform the operation
     * @return The tab layout tab which is selected
     */
    public static int setCurrentBook(Context c) {
        int tab = 0;
        String pref_book = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("book_book", "I");
        switch (pref_book){
            case "I": tab = 0;
                break;
            case "II": tab = 1;
                break;
            case "III": tab = 2;
                break;
        }
        return tab;
    }

    /**
     *  get the book that was chosen
     * @param c Context within which to perform the operation
     * @return the book
     */
    public static String getPreferenceBook(Context c){
        String pref_book = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("book_book", "0");

        if (!pref_book.equals("0")){
            return pref_book;
        }
        else{
            return "I";
        }
    }

    /**
     *  get the Unit(s) that were chosen
     * @param c Context within which to perform the operation
     * @return The unit
     */
    public static String getPreferenceUnit(Context c) {
        Boolean pref_unit_A = PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("unit_A", true);
        Boolean pref_unit_B = PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("unit_A", false);
        Boolean pref_unit_C = PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("unit_A", false);

        if (pref_unit_A){
            if (pref_unit_B){
                if (pref_unit_C){
                    return  "A B C";
                }
                else{
                    return  "A B";
                }
            }
            else {
                if (pref_unit_C){
                    return "A C";
                }
                else{
                    return  "A";
                }
            }
        }
        else{
            if (pref_unit_B){
                if (pref_unit_C){
                    return  "B C";
                }
                else{
                    return  "B";
                }
            }
            else {
                if (pref_unit_C){
                    return  "C";
                }
                else{
                    return  "A";
                }
            }
        }
    }

    /**
     * Determines the current example sentence in accord of the preferences of the user
     *
     * @param c Context within which to perform the operation
     * @param dbManager The database manager
     * @param voc The vocable for which the sentence is queried
     * @return The sentence
     */
    public static BookObject getTranslationPreferenceSentence(Context c, DatabaseManager dbManager, VocObject voc){
        //Get first preference sentence
        String pref_first_sentence = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("word_first", "");
        BookObject sentence = null;
        switch(pref_first_sentence) {
            case "":
                sentence = dbManager.getExampleBookSentence(voc);
                break;
            case "book":
                sentence = dbManager.getExampleBookSentence(voc);
                break;
            case "learner":
                sentence = dbManager.getExampleLearnerSentence(voc);
                break;
            case "gdex":
                sentence = dbManager.getExampleGDEXSentence(voc);
                break;
        }
        // get second preference sentence
        if (sentence.getSentence().equals("")){
            String pref_second_sentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("word_second", "");
            switch(pref_second_sentence) {
                case "":
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    break;
                case "book":
                    sentence = dbManager.getExampleBookSentence(voc);
                    break;
                case "learner":
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    break;
                case "gdex":
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    break;
            }
        }
        // get third preference sentence
        if (sentence.getSentence().equals("")){
            String pref_second_sentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("word_third", "");
            switch(pref_second_sentence) {
                case "":
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    break;
                case "book":
                    sentence = dbManager.getExampleBookSentence(voc);
                    break;
                case "learner":
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    break;
                case "gdex":
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    break;
            }
        }
        return sentence;
    }

    /**
     * Determines the current example sentence in accord of the preferences of the user for the
     * Kontext exercise
     *
     * @param c Context within which to perform the operation
     * @param dbManager The database manager
     * @param voc The vocable for which the sentence is queried
     * @param part String which denotes which number of sentence is needed
     * @return The sentence
     */
    public static BookObject getKontextSentence(Context c, DatabaseManager dbManager,
                                                VocObject voc, String part,
                                                ArrayList cus) {

        BookObject sentenceObj = null;
        //String sentence = "";

        String firstPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("kontext_"+part+"_first", "");
        Log.d("Pref", firstPrefForSentence);
        sentenceObj = sentence(firstPrefForSentence, "_first", dbManager, cus, voc);


        if (sentenceObj.getSentence().equals("") || cus.contains(sentenceObj.getSentence())) {
            String secondPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("kontext_" + part + "_second", "");
            Log.d("Pref", secondPrefForSentence);
            sentenceObj = sentence(secondPrefForSentence, "_second", dbManager, cus, voc);
        }

        if (sentenceObj.getSentence().equals("") || cus.contains(sentenceObj.getSentence())) {
            String thirdPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("kontext_" + part + "_third", "");
            Log.d("Pref", thirdPrefForSentence);
            sentenceObj = sentence(thirdPrefForSentence, "_third", dbManager, cus, voc);
        }

        if (cus.contains(sentenceObj.getSentence())){
            Log.d("EU", "empty Sentence");
            sentenceObj = sentenceObj.emptySentence();
        }
        return sentenceObj;
    }

    /**
     * Determines the current example sentence in accord of the preferences of the user for the
     * Worttest exercise
     *
     * @param c Context within which to perform the operation
     * @param dbManager The database manager
     * @param voc The vocable for which the sentence is queried
     * @return The sentence
     */
    public static BookObject getWorttestSentence(Context c, DatabaseManager dbManager,
                                                VocObject voc) {

        BookObject sentenceObj = null;

        ArrayList<String> emptyList = new ArrayList<>();
        String firstPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("word_first", "");
        Log.d("Pref", firstPrefForSentence);
        sentenceObj = sentence(firstPrefForSentence, "_first", dbManager, emptyList, voc);


        if (sentenceObj.getSentence().equals("")) {
            String secondPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("word_second", "");
            Log.d("Pref", secondPrefForSentence);
            sentenceObj = sentence(secondPrefForSentence, "_second", dbManager, emptyList, voc);
        }

        if (sentenceObj.getSentence().equals("")) {
            String thirdPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("word_third", "");
            Log.d("Pref", thirdPrefForSentence);
            sentenceObj = sentence(thirdPrefForSentence, "_third", dbManager, emptyList, voc);
        }

        return sentenceObj;
    }

    private static BookObject sentence(String preference, String preferenceEnding, DatabaseManager dbManager, ArrayList cus, VocObject voc) {
        List<String> indexesOfSentences;
        BookObject sentenceObj = null;
        switch(preference) {
            case "":
                Log.d("ExersiseUtils", "No first preference for sentence saved");
                if (preferenceEnding.endsWith("first")){
                    sentenceObj = getPreferenceSentenceForBook(voc, cus, dbManager);
                }
                else if (preferenceEnding.endsWith("second")){
                    sentenceObj = getPreferenceSentenceForLearner(voc, cus, dbManager);
                }
                else if (preferenceEnding.endsWith("third")){
                    sentenceObj = getPreferenceSentenceForGDEX(voc, cus, dbManager);
                }
                else {
                    try {
                        throw new Exception("ExerciseUtils: No known preference.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "book":
                Log.d("ExersiseUtils", "1st Taking a Book sentence");
                sentenceObj = getPreferenceSentenceForBook(voc, cus, dbManager);
                break;
            case "learner":
                Log.d("ExersiseUtils", "1st Taking a Learner sentence");
                sentenceObj = getPreferenceSentenceForLearner(voc, cus, dbManager);
                break;
            case "gdex":
                Log.d("ExersiseUtils", "1st Taking a Gdex sentence");
                sentenceObj = getPreferenceSentenceForGDEX(voc, cus, dbManager);
        }

        if (sentenceObj == null){
            sentenceObj = sentenceObj.emptySentence();
        }

        return sentenceObj;


    }

    private static BookObject getPreferenceSentenceForBook(VocObject voc, ArrayList<String> cus, DatabaseManager dbManager){
        List<String> indexesOfSentences = DBUtils.splitListString(voc.getIndexesBookSentences());
        BookObject sentenceObj = null;
        for (int i = 0; i <= indexesOfSentences.size()-1; i++) {
            sentenceObj = dbManager.getBookSentence(indexesOfSentences.get(i));
            if (cus.contains(sentenceObj.getSentence())) {
                // if is the same sentence as sent as the other sentence continue
                sentenceObj = null;
                continue;
            } else {
                // unique sentence
                break;
            }
        }
        return sentenceObj;
    }

    private static BookObject getPreferenceSentenceForLearner(VocObject voc, ArrayList<String> cus, DatabaseManager dbManager){
        List<String> indexesOfSentences = DBUtils.splitListString(voc.getIndexesLearnerSentences());
        BookObject sentenceObj = null;
        for (int i = 0; i <= indexesOfSentences.size()-1; i++) {
            sentenceObj = dbManager.getCorpusSentence(indexesOfSentences.get(i));
            if (cus.contains(sentenceObj.getSentence())) {
                // if is the same sentence as sent as the other sentence continue
                sentenceObj = null;
                continue;
            } else {
                // unique sentence
                break;
            }
        }
        return sentenceObj;
    }

    private static BookObject getPreferenceSentenceForGDEX(VocObject voc, ArrayList<String> cus, DatabaseManager dbManager){
        List<String> indexesOfSentences = DBUtils.splitListString(voc.getIndexesGDEXSentences());
        BookObject sentenceObj = null;
        for (int i = 0; i <= indexesOfSentences.size()-1; i++) {
            sentenceObj = dbManager.getCorpusSentence(indexesOfSentences.get(i));
            if (cus.contains(sentenceObj.getSentence())) {
                // if is the same sentence as sent as the other sentence continue
                sentenceObj = null;
                continue;
            } else {
                // unique sentence
                break;
            }
        }
        return sentenceObj;
    }
}

