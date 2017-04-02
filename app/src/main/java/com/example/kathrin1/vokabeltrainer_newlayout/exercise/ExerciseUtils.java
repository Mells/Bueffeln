package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.text.Html;
import android.text.Spanned;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.apache.commons.text.similarity.LevenshteinDistance;

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
        Map<String, List<String>> smap = DBUtils.stringOfJSONTagsToMap(sentence.getTagged());

        List<String> lemmaVocList = DBUtils.splitJSONListString(word.getLemma());
        String sent = sentence.getSentence();

        for (String lemma : lemmaVocList)
        {
            if (smap.containsKey(lemma))
            {
                List<String> bla = smap.get(lemma);
                for (String s : bla)
                {
                    sent = sent.replaceAll("\\b" + s + "\\b", replacementString.replace("%s", s));
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
     * @param german   True if the user is looking for the German translation, false otherwise.
     * @return Returns true if the answer is deemed correct, false otherwise.
     */
    public static Answer isAnswerCorrect(SentObject sentence, VocObject word, String input, boolean german)
    {
        String wordString = german ? word.getTranslation()
                                   : word.getVoc();


        input = input.toLowerCase();

        Map<String, List<String>> smap = DBUtils.stringOfJSONTagsToMap(sentence.getTagged());

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
}
