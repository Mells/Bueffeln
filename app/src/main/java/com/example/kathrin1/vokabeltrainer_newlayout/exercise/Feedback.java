package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kathrin1 on 03.05.17.
 */

/* Provide intelligent, linguistic, scaffolding feedback
    - as visual feedback to support any kind of conceptual feedback:
    show difference to target (i.e. bold what is different)

        feedback on underlying issues:
        - part of speech ("I  met my wife during I was in prison" instead of "while")


        - wrong word ("went" instead of "walked")


        - morphology ("You used the right word, but think about the form:"):
            - tempus ("walked" instead of "walks")
            - agreement ("He walk" instead of "He walks")
            - determiner ("an book_book" instead of "a book_book")
            - wrong pronoun ("Mary likes himself" instead of "Mary likes herself", or
            - "I like himself" instead of "I like myself")
            - spelling  ("hte book_book" instead of "the book_book")

        - word order ("He put on it" instead of "He put it on")

    - there is a further possibility for scaffolding in multi-gap activities
    by providing more context examples
*/

public class Feedback {

    private String learner_vocable;
    private String learner_pos;
    private String learner_lemma;

    private String voc_vocable;
    private String voc_pos;
    private String voc_lemma;

    private String file = "assets/databases/xtag-morph-1.5a.txt";

    public Feedback(String vocableByLearner, String voc_vocable, String pos, String lemma){
        this.learner_vocable = vocableByLearner;
        this.voc_vocable = voc_vocable;
        this.voc_pos = pos;
        this.voc_lemma = lemma;
    }

    public String generateFeedback(){
        //look up tag and lemma of word
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));

            // do reading, usually loop until end of file reading
            String line;
            String[] lineArray;
            while ((line = reader.readLine()) != null) {
                //process line
                if (line.startsWith(";;;")){
                    continue;
                }

                lineArray = line.split("\\t");
                if (lineArray[0].equals(learner_vocable)){
                    learner_lemma = lineArray[1];
                    learner_pos = lineArray[2];
                    Log.d("Feedback", learner_lemma + " " + learner_pos);
                }
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        // put word into a gaped sentence for tagging?

        //are there the same amount of words

        //if it is not the same tag call partOfSpeech

        //if it is the same tag but not lemma call wrongWord

        //if it is the same tag and lemma call morphology


        return null;
    }

    private String sameAmountOfWords(){

        return "";
    }

    private String partOfSpeech(){

        return "";
    }

    private String wrongWord(){

        return null;
    }

    private String morphology(){

//      tempus ("walked" instead of "walks")
//      agreement ("He walk" instead of "He walks")
//      determiner ("an book_book" instead of "a book_book")
//      wrong pronoun ("Mary likes himself" instead of "Mary likes herself", or
//      "I like himself" instead of "I like myself")
//      spelling  ("hte book_book" instead of "the book_book")
        return null;
    }

    private String checkTempus(){
        // compare tags of the word

        return null;
    }

    private String checkAgreement(){

        return null;
    }

    private String checkDeterminer(){

        return null;
    }

    private String checkPronoun(){

        return null;
    }

    private String checkSpelling(){
        // + groß-/kleinschreibung

        return null;
    }

    private String checkWordOrder(){

        return null;
    }
}
