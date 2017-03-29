package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.text.Html;
import android.text.Spanned;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DBUtils;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.SentObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import java.util.List;
import java.util.Map;

/**
 * For housing some useful static methods used by exercises
 */
public abstract class ExerciseUtils
{
    /**
     * Generates the sentence to display on a screen using the given sentence, replacing the given
     * word in the sentence with a blank.
     *
     * @param sentence          The sentence to replace words in
     * @param word              The word (or words) to replace in the sentence
     * @return The generated string to display
     */
    public static String deleteWordFromSentence(SentObject sentence, VocObject word)
    {
        return replaceWordInSentence(sentence, word, "____");
    }

    /**
     * Generates the sentence to display on a screen using the given sentence, replacing the given
     * word in the sentence with the given string.
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
                    sent = sent.replaceAll(s, replacementString.replace("%s", s));
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
     * //TODO:  This could be greatly improved to be more flexible
     *
     * @param word   The word that the user is trying to get the correct answer for
     * @param input  The user's input
     * @param german True if the user is looking for the German translation, false otherwise.
     * @return Returns true if the answer is deemed correct, false otherwise.
     */
    public static boolean isAnswerCorrect(VocObject word, String input, boolean german)
    {
        String wordToMatch = german ? word.getTranslation()
                                    : word.getVoc();

        return wordToMatch.equals(input);
    }
}
