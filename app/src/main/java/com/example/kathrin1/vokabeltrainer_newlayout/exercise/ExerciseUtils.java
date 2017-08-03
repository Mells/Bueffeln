package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static String deleteWordFromSentence(SentObject sentence, VocObject word)
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
    public static String replaceWordInSentence(SentObject sentence, VocObject word,
                                               String replacementString)
    {
        Map<String, List<String>> smap = DBUtils.stringOfJSONTagsToMap(sentence.getMapped());

        List<String> lemmaVocList = DBUtils.splitJSONListString(word.getLemma());
        String sent = sentence.getSentence();

        // TODO:  Probably move this bit somewhere more sensible
        // Cleans up spaces before punctuation
        sent = sent.replaceAll("\\s([.,!?])", "$1");
        Log.d("ExerciseUtils: Smap", String.valueOf(smap));
        Log.d("ExerciseUtils: lemvocli", String.valueOf(lemmaVocList));
        for (String lemma : lemmaVocList)
        {
            if (smap.containsKey(lemma))
            {
                List<String> bla = smap.get(lemma);
                Log.d("ExerciseUtils: Lemma", String.valueOf(bla));
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
    public static Answer isAnswerCorrect(SentObject sentence, VocObject word, String input, boolean german)
    {
        String wordString = german ? word.getTranslation()
                                   : word.getVoc();


        input = input.toLowerCase().trim();

        Map<String, List<String>> smap = DBUtils.stringOfJSONTagsToMap(sentence.getMapped());

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
     *
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
     * Determines the current example sentence in accord of the preferences of the user
     *
     * @param c Context within which to perform the operation
     * @param dbManager The database manager
     * @param voc The vocable for which the sentence is queried
     * @return The sentence
     */
    public static SentObject getTranslationPreferenceSentence(Context c, DatabaseManager dbManager, VocObject voc){
        //Get first preference sentence
        String pref_first_sentence = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("word_first", "");
        SentObject sentence = null;
        switch(pref_first_sentence) {
            case "":
                sentence = dbManager.getExampleOldSentence(voc);
                break;
            case "book":
                sentence = dbManager.getExampleOldSentence(voc);
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
                    sentence = dbManager.getExampleOldSentence(voc);
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
                    sentence = dbManager.getExampleOldSentence(voc);
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
     * Determines the current example sentence in accord of the preferences of the user
     *
     * @param c Context within which to perform the operation
     * @param dbManager The database manager
     * @param voc The vocable for which the sentence is queried
     * @param part String which denotes which number of sentence is needed
     * @return The sentence
     */
    public static SentObject getKontextPreferenceSentence(Context c, DatabaseManager dbManager,
                                                          VocObject voc, String part,
                                                          String sentence1, String sentence2){

        String firstPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("kontext_"+part+"_first", "");
        SentObject sentence = null;
        switch(firstPrefForSentence) {
            case "":
                Log.d("ExersiseUtils", "No first preference for sentence saved");
                sentence = dbManager.getExampleOldSentence(voc);
                if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                    List<String> bookList = DBUtils.splitListString(voc.getOldSentences());
                    for (int i = 0; i <= bookList.size()-1; i++) {
                        sentence = dbManager.getSentence(bookList.get(i));
                        if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                break;
            case "book":
                Log.d("ExersiseUtils", "1st Taking a Book sentence");
                sentence = dbManager.getExampleOldSentence(voc);
                if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                    List<String> bookList = DBUtils.splitListString(voc.getOldSentences());
                    for (int i = 0; i <= bookList.size()-1; i++) {
                        sentence = dbManager.getSentence(bookList.get(i));
                        if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                break;
            case "learner":
                Log.d("ExersiseUtils", "1st Taking a Learner sentence");
                sentence = dbManager.getExampleLearnerSentence(voc);
                if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                    List<String> learnerList = DBUtils.splitListString(voc.getLearnerSentences());
                    for (int i = 0; i <= learnerList.size()-1; i++) {
                        sentence = dbManager.getSentence(learnerList.get(i));
                        if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                break;
            case "gdex":
                Log.d("ExersiseUtils", "1st Taking a Gdex sentence");
                sentence = dbManager.getExampleGDEXSentence(voc);
                if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                    List<String> gdexList = DBUtils.splitListString(voc.getGDEXSentences());
                    for (int i = 0; i <= gdexList.size()-1; i++) {
                        sentence = dbManager.getSentence(gdexList.get(i));
                        if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                break;
        }
        // get second preference sentence
        if (sentence.getSentence().equals("") || sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
            String secondPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("kontext_"+part+"_second", "");
            switch(secondPrefForSentence) {
                case "":
                    Log.d("ExersiseUtils", "No second preference for sentence saved");
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> learnerList = DBUtils.splitListString(voc.getLearnerSentences());
                        for (int i = 0; i <= learnerList.size()-1; i++) {
                            sentence = dbManager.getSentence(learnerList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                case "book":
                    Log.d("ExersiseUtils", "2nd Taking a Book sentence");
                    sentence = dbManager.getExampleOldSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> bookList = DBUtils.splitListString(voc.getOldSentences());
                        for (int i = 0; i <= bookList.size()-1; i++) {
                            sentence = dbManager.getSentence(bookList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                case "learner":
                    Log.d("ExersiseUtils", "2nd Taking a Learner sentence");
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> learnerList = DBUtils.splitListString(voc.getLearnerSentences());
                        for (int i = 0; i <= learnerList.size()-1; i++) {
                            sentence = dbManager.getSentence(learnerList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                case "gdex":
                    Log.d("ExersiseUtils", "2nd Taking a GDEX sentence");
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> gdexList = DBUtils.splitListString(voc.getGDEXSentences());
                        for (int i = 0; i <= gdexList.size()-1; i++) {
                            sentence = dbManager.getSentence(gdexList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
            }
        }
        // get third preference sentence
        if (sentence.getSentence().equals("") || sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
            String thirdPrefForSentence = PreferenceManager.getDefaultSharedPreferences(c)
                    .getString("kontext_"+part+"_third", "");
            switch(thirdPrefForSentence) {
                case "":
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    break;
                case "book":
                    sentence = dbManager.getExampleOldSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> bookList = DBUtils.splitListString(voc.getOldSentences());
                        for (int i = 0; i <= bookList.size()-1; i++) {
                            sentence = dbManager.getSentence(bookList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                case "learner":
                    sentence = dbManager.getExampleLearnerSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> learnerList = DBUtils.splitListString(voc.getLearnerSentences());
                        for (int i = 0; i <= learnerList.size()-1; i++) {
                            sentence = dbManager.getSentence(learnerList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
                case "gdex":
                    sentence = dbManager.getExampleGDEXSentence(voc);
                    if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)){
                        List<String> gdexList = DBUtils.splitListString(voc.getGDEXSentences());
                        for (int i = 0; i <= gdexList.size()-1; i++) {
                            sentence = dbManager.getSentence(gdexList.get(i));
                            if (sentence.getSentence().equals(sentence1) || sentence.getSentence().equals(sentence2)) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    break;
            }
        }
        return sentence;
    }
}
