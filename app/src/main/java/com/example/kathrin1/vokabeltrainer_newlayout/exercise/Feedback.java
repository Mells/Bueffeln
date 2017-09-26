package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;
import android.util.Log;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.MorphInfoObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.MorphObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.VocObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Created by kathrin1 on 03.05.17.
 */

public class Feedback {

    private String learnerAnswer;

    private ArrayList appProcessedAnswer;
    private ArrayList appProcessedAnswerTag;
    private ArrayList appLemma;
    private ArrayList appSoundex;

    private String bestAppReading;

    private Boolean isEnglishWord;

    private DatabaseManager dbManager;

    private String singleTarget;

    public Feedback(String learnerAnswer, VocObject vocable, Boolean isEnglishWord, Context c){

        this.learnerAnswer = learnerAnswer;
        this.isEnglishWord = isEnglishWord;

        if(this.isEnglishWord) {
            this.appProcessedAnswer = vocable.getProcessedVocable();
            this.appProcessedAnswerTag = vocable.getTaggedProcessedVocable();
            this.appSoundex = vocable.getProcessedVocableSoundex();
            this.appLemma = vocable.getLemmaVocable();
        }
        else {
            this.appProcessedAnswer = vocable.getProcessedTranslation();
            this.appProcessedAnswerTag = vocable.getTaggedProcessedTranslation();
            this.appSoundex = vocable.getProcessedTranslationSoundex();
            this.appLemma = vocable.getLemmaTranslation();
        }

        bestAppReading = "";

        dbManager = DatabaseManager.build(c);


    }

    public String generateFeedback(){

        if (isEnglishWord) {
            return generateEnglishFeedback();
        }
        else {

        }
        return null;
    }

    private String generateEnglishFeedback() {

        Log.d("Feedback: appA", appProcessedAnswer.toString());

        singleTarget = getSingleTarget();

        // <SingleTarget> is a single word:
        if (singleTarget.split("\\s").length < 2) {

            // <LearnerString> is a single word:
            if (learnerAnswer.split("\\s").length < 2) {


                Log.d("Feedback", "aWord:" + singleTarget + " - " + singleTarget.getBytes());
                Log.d("Feedback", "lWord:" + learnerAnswer + " - " + learnerAnswer.getBytes());

                return learnerAnswer + ": " + singleWordComparison(singleTarget, learnerAnswer, null);
            }
            else { // <LearnerString> is more than a single word:

                return learnerAnswer + ": Deine Antwort beinhaltet " + Integer.toString(learnerAnswer.split("\\s").length) +
                        " Wörter, es wird aber nur eins gesucht";
            }
        } else { // <SingleTarget> is more than one word
            // [['to expect a baby'], ['to have a baby']]
            //
            // [[('to', ['V TO']), ('expect', ['V INF']), ('a', ['Det']), ('baby', ['N 3sg'])],
            // [('to', ['V TO']), ('have', ['V INF']), ('a', ['Det']), ('baby', ['N 3sg'])]]
            if (singleTarget.split("\\s").length == learnerAnswer.split("\\s").length) {
                // have the same number of words

                ArrayList<Pair> newAppAnswerTag = getCorrectTaggedByIndex(singleTarget, appProcessedAnswer, appProcessedAnswerTag);
                Log.d("Feedback: appPAnswer: ", appProcessedAnswer.toString());
                Log.d("Feedback: Pair: ", newAppAnswerTag.toString());

                String[] learnerWords = learnerAnswer.split("\\s");
                StringBuilder theFeedback = new StringBuilder();
                MorphObject learnerWordReading;

                for (int i = 0; i < newAppAnswerTag.size(); i++){
                    //for (Pair word : newAppAnswerTag){
                    Pair aWord = newAppAnswerTag.get(i);
                    String lWord = learnerWords[i];

                    theFeedback.append("<b>" + lWord + "</b>" + ": ");
//
                    if (lWord.equals(aWord.getKey())){
                        theFeedback.append("Dieses Wort ist korrekt.");
                        if (i < newAppAnswerTag.size()){
                            theFeedback.append("<br/>");
                        }
                        continue;
                    }

                    ArrayList<Pair> currentPair = new ArrayList<>();
                    currentPair.add(Pair.of((String)aWord.getKey(), aWord.getValue()));
                    theFeedback.append(singleWordComparison((String)aWord.getKey(), lWord, currentPair));

                    if (i < newAppAnswerTag.size()){
                        theFeedback.append("<br/>");
                    }
                }
                return theFeedback.toString();
            }
            else { // have not the same number of words
                return learnerAnswer + ": Die Antwort hat " + singleTarget.split("\\s").length + " Wörter. " +
                        "Deine Antwort hat " + learnerAnswer.split("\\s").length + " Wörter.";
            }
        }
    }

    private String getSingleTarget(){
        String singleTarget = "";
        // if it has more than one correct answer
        if (appProcessedAnswer.size() > 1) {
            // [[xxx], [yyy]]
            int topDistance = 1000;
            String topAnswer = "";
            // select element from list that has shortest Levensthein distance with LearnerString
            for (int i = 0; i < appProcessedAnswer.size(); i++){
                //Log.d("Feedback: appA0", appProcessedAnswer.get(i).toString());
                ArrayList<String> temp = (ArrayList<String>) appProcessedAnswer.get(i);
                String answer = temp.get(0);
                int distance = levenshteinDistance(answer, learnerAnswer);
                if (distance < topDistance){
                    topDistance = distance;
                    topAnswer = answer;
                }
            }
            // call it <SingleTarget>
            singleTarget = topAnswer;
        }
        // there is a single answer
        else {
            // [[we]]
            // <SingleTarget> = <TargetAnswer>
            ArrayList<String> temp = (ArrayList<String>) appProcessedAnswer.get(0);
            singleTarget = temp.get(0);
        }
        Log.d("Feedback: singTar", singleTarget);
        return singleTarget;
    }

    private String singleWordComparison(String singleTarget, String learnerAnswer, ArrayList<Pair> newAppAnswerTag){

        ArrayList<Pair> mNewAppAnswerTag;
        //is the problem just a upper-/lower case problem
        if (singleTarget.toLowerCase().equals(learnerAnswer.toLowerCase())){
            return "Es scheint als ob du einen Fehler in der " +
                    "Groß- und Kleinschreibung hast.";
        }

        //	<xtag> the morphological database
        MorphObject learnerWordReading = dbManager.getMorphInformation(learnerAnswer.replace("'", "''"));

        // <SingleTarget> is not known in <xtag>
        if (learnerWordReading.isEmpty()) {

            return feedbackForWordNotFound(singleTarget, learnerAnswer);

        } else {
            // <SingleTarget> is known in <xtag>
            Log.d("Feedback: reading", this.appProcessedAnswerTag.toString());

            //[[(can,V PRES, V INF)]] more than one reading

            if (newAppAnswerTag == null){
                mNewAppAnswerTag = getCorrectTaggedByIndex(singleTarget, appProcessedAnswer, appProcessedAnswerTag);
            }
            else{
                mNewAppAnswerTag = newAppAnswerTag;
            }
            // only 1 entry long as we compare just one word

            Pair pair = (Pair) mNewAppAnswerTag.get(0);
            String appReading = (String) pair.getValue();
            String[] allAppReadings = appReading.split(", ");

            MorphInfoObject morphologicalReading = getBestLearnerReading(appReading, allAppReadings, learnerWordReading);

            return compareReadings(learnerWordReading, morphologicalReading, bestAppReading);
        }
    }

    private String feedbackForWordNotFound(String singleTarget, String mLearnerAnswer) {

        // calculate  <soundex> of learner word
        String learner_soundex = generateSoundex(mLearnerAnswer);

        // calculate <sound_levenshtein> between soundex
        String singleTargetSoundex = getCorrectSoundexByIndex(singleTarget, appProcessedAnswer, appSoundex);
        int levenshtein_soundex = levenshteinDistance(learner_soundex, singleTargetSoundex);

        // calculate <word_levenshtein> between words
        int levenshtein_spelling = levenshteinDistance(mLearnerAnswer, singleTarget);

        Log.d("Feedback: learnerSound", learner_soundex);
        Log.d("Feedback: levenSound", String.valueOf(levenshtein_soundex));
        Log.d("Feedback: levenSpell", String.valueOf(levenshtein_spelling));

        // if <sound_levenshtein> distance = 0
        if (levenshtein_spelling == 1){
            if (mLearnerAnswer.toLowerCase().equals(singleTarget.toLowerCase())){
                return "Es scheint als ob du einen Fehler in der " +
                        "Groß- und Kleinschreibung hast";
            }
            else {
                return "Du hast kein bekanntes Wort eingegeben, " +
                        "aber die Lösung ist fast gleich zu deiner Eingabe.";
            }
        }
        if (levenshtein_soundex == 0) {
            // return “sounds the same”
            return "Du hast kein bekanntes Wort eingegeben, " +
                    "aber die Lösung klingt gleich wie deine Eingabe";
        }
        else if (levenshtein_soundex == levenshtein_spelling) { // if <sound_levenshtein> = <word_levenshtein>
            Log.d("Feedback in", "same");
            // todo threshold
            return "Muss noch konstruiert werden";
        } else if (levenshtein_soundex < levenshtein_spelling) { // else if <sound_levenshtein> smaller than <word_levenshtein>
            // return "Das Wort klingt gleich"
            // todo threshold
            Log.d("Feedback in", "soundex");
            return "Du hast kein bekanntes Wort eingegeben, aber die Lösung klingt " +
                    "ähnlich. Bitte überprüfe deine Rechtschreibung";
        } else {
            // else (if) <sound_levenshtein> larger than <word_levenshtein>
            // return "schau nach einem Rechtschreibfehler"
            // todo threshold
            Log.d("Feedback in", "spelling");
            return "Du hast kein bekanntes Wort eingegeben, " +
                    "bitte überprüfe deine Rechtschreibung.";
        }
    }

    private MorphInfoObject getBestLearnerReading(String appReading, String[] allAppReadings, MorphObject learnerWordReading) {

        MorphInfoObject morphologicalReading = null;

        if (appReading.split(", ").length > 1){
            if (learnerWordReading.getNumberOfReadings() > 1) {
                // <WordReading> has more than one <MorphologicalReading>
                // get the best <morphologicalReading> through levenshtein

                String bestStringResult = "";
                int bestIntResult = 100;
                for (String read : allAppReadings){
                    for (int i = 1; i <= learnerWordReading.getNumberOfReadings(); i++){
                        String lReading = learnerWordReading.getReadingByNumber(i);
                        lReading = lReading.split("#")[0];
                        int leven = levenshteinDistance(read, lReading);
                        if (leven < bestIntResult){
                            bestIntResult = leven;
                            bestStringResult = lReading;
                            bestAppReading = read;
                        }
                    }
                }

                morphologicalReading = new MorphInfoObject(bestStringResult);
            }
            else {
                // get <morphologicalReading> from <wordReading>
                String bestStringResult = "";
                int bestIntResult = 100;
                for (String read : allAppReadings){
                    int leven = levenshteinDistance(read, learnerWordReading.getReading1().split("#")[0]);
                    if (leven < bestIntResult){
                        bestIntResult = leven;
                        bestStringResult = learnerWordReading.getReading1();
                        bestAppReading = read;
                    }
                }
                morphologicalReading = new MorphInfoObject(bestStringResult);
            }
        }
        else {
            if (learnerWordReading.getNumberOfReadings() > 1) { // <WordReading> has more than one <MorphologicalReading>

                // get the best <morphologicalReading> through levenshtein
                String bestStringResult = "";
                int bestIntResult = 100;
                for (int i = 1; i <= learnerWordReading.getNumberOfReadings(); i++){
                    String lReading = learnerWordReading.getReadingByNumber(i);
                    lReading = lReading.split("#")[0];
                    int leven = levenshteinDistance(appReading, lReading);
                    if (leven < bestIntResult){
                        bestIntResult = leven;
                        bestStringResult = lReading;
                        bestAppReading = appReading;
                    }
                }

                morphologicalReading = new MorphInfoObject(bestStringResult);

            }
            else {

                // get <morphologicalReading> from <wordReading>
                morphologicalReading = new MorphInfoObject(learnerWordReading.getReading1());
                bestAppReading = appReading;
            }
        }
        return morphologicalReading;
    }

    private ArrayList<Pair> getCorrectTaggedByIndex(String voc, ArrayList vocableProcessed, ArrayList vocableProcessedTagged){

        int tmpX = 0;
        int tmpY = 0;
        //String max = "to have a baby";
        // there are some changes here. in addition to the caching
        for (int i = 0; i < vocableProcessed.size(); i++) {
            ArrayList<String> inner = (ArrayList<String>) vocableProcessed.get(i);
            // caches inner variable so that it does not have to be looked up
            // as often, and it also tests based on the inner loop's length in
            // case the inner loop has a different length from the outer loop.
            for (int y = 0; y < inner.size(); y++) {
                System.out.println(inner.get(y) + " - " + voc);
                if (inner.get(y).equals(voc)) {
                    voc = inner.get(y);
                    // store the coordinates of max
                    tmpX = i; tmpY = y;
                }
            }
        }
        System.out.println(voc);
        // convert to string before outputting:
        System.out.println("The (x,y) is: ("+tmpX+","+tmpY+")");
        System.out.println(vocableProcessedTagged.get(tmpX));
        return (ArrayList<Pair>) vocableProcessedTagged.get(tmpX);
    }

    private String getCorrectSoundexByIndex(String voc, ArrayList vocableProcessed, ArrayList vocableSoundex){

        int tmpX = 0;
        int tmpY = 0;
        //String max = "to have a baby";
        // there are some changes here. in addition to the caching
        for (int i = 0; i < vocableProcessed.size(); i++) {
            ArrayList<String> inner = (ArrayList<String>) vocableProcessed.get(i);
            // caches inner variable so that it does not have to be looked up
            // as often, and it also tests based on the inner loop's length in
            // case the inner loop has a different length from the outer loop.
            for (int y = 0; y < inner.size(); y++) {
                System.out.println(inner.get(y) + " - " + voc);
                if (inner.get(y).equals(voc)) {
                    voc = inner.get(y);
                    // store the coordinates of max
                    tmpX = i; tmpY = y;
                }
            }
        }
        System.out.println(voc);
        // convert to string before outputting:
        System.out.println("The (x,y) is: ("+tmpX+","+tmpY+")");
        System.out.println(vocableSoundex.get(tmpX));
        return ((ArrayList<String>) vocableSoundex.get(tmpX)).get(0);
    }


    /*
    COMPARE THE SAME PART OF SPEECH TAGS
     */
    private String compareReadings(MorphObject learnerWordReading, MorphInfoObject morphologicalLearnerReading, String bestAppReading) {
        MorphInfoObject morphologicalAppReading = new MorphInfoObject(bestAppReading);
        String learnerPOS = morphologicalLearnerReading.getPOS();
        String appPos = morphologicalAppReading.getPOS();
        if (learnerWordReading.getLemma().equals(this.appLemma.get(0))){
            if (morphologicalAppReading.getPOS().equals(morphologicalLearnerReading.getPOS())){
                return compareMorphologicalInformation(morphologicalAppReading, morphologicalLearnerReading);
            }
            else {
                return "Fehler: (1) Lemma ist das Gleiche, aber nicht der POS Tag.";
            }
        }
        else if (morphologicalLearnerReading.getLemma().equals(this.appLemma.get(0))){
            if (morphologicalAppReading.getPOS().equals(morphologicalLearnerReading.getPOS())){
                return compareMorphologicalInformation(morphologicalAppReading, morphologicalLearnerReading);
            }
            else {
                return "Fehler: (2) Lemma ist das Gleiche, aber nicht der POS Tag.";
            }
        }
        else if (appPos.equals(learnerPOS)){
            //return "Die Wortart: " + posToString(appPos) + " ist richtig. Aber das ist nicht das " +
            //        "gesuchte Wort. Probiere ein anderes "  + posToString(appPos) + ".";

            return "Die Wortart: " + posToString(appPos) + " ist richtig. Aber das ist nicht das " +
                    "gesuchte Wort. " + compareMorphologicalInformation(morphologicalAppReading, morphologicalLearnerReading);

            //return compareMorphologicalInformation(morphologicalAppReading, morphologicalLearnerReading);
        }
        else {
            return "Es wird ein " + posToString(appPos) + " gesucht, du hast ein "
                    + posToString(learnerPOS) + " eingegeben.";
        }

    }

    private String compareMorphologicalInformation(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        Log.d("Feedback: CMI:", morphologicalAppReading.toString() + " - " + morphologicalLearnerReading.toString());
        switch (morphologicalAppReading.getPOS()) {
            case "Comp":
                return compareComparatives();
            case "Conj":
                return compareConjuctions();
            case "G":
                return compareGenitiveNoun();
            case "I":
                return compareInterjections();
            case "Part":
                return compareParticles();
            case "Prep":
                return comparePrepositions();
            case "Punct":
                return "Punctuation"; // TODO?
            case "A":
                return compareAdjectives(morphologicalAppReading, morphologicalLearnerReading);
            case "Adv":
                return compareAdverbs(morphologicalAppReading, morphologicalLearnerReading);
            case "Det":
                return compareDeterminers(morphologicalAppReading, morphologicalLearnerReading);
            case "N":
                return compareNouns(morphologicalAppReading, morphologicalLearnerReading);
            case "NVC":
                return compareNounVerbCombinations(morphologicalAppReading, morphologicalLearnerReading);
            case "Pron":
                return comparePronouns(morphologicalAppReading, morphologicalLearnerReading);
            case "PropN":
                return compareProperNouns(morphologicalAppReading, morphologicalLearnerReading);
            case "V":
                return compareVerbs(morphologicalAppReading, morphologicalLearnerReading);
            case "VVC":
                return compareVerbVerbCombinations(morphologicalAppReading, morphologicalLearnerReading);
            default:
                return "Fehler: compareMorphologicalInformation";
        }
    }

    private String compareComparatives() {
        return "Probiere ein anderes Komparativ.";
    }

    private String compareConjuctions() {
        return "Probiere eine andere Konjuktion.";
    }

    private String compareGenitiveNoun() {
        return "Probiere ein anderes Genetiv Nomen.";
    }

    private String compareInterjections() {
        return "Probiere eine andere Interjektion.";
    }

    private String compareParticles() {
        return "Probiere ein anderen Partikel.";
    }

    private String comparePrepositions() {
        return "Probiere ein andere Preposition.";
    }

    private String compareAdjectives(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {

        String result = compareComparativeSuperlative(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }
        return "Probiere ein anderes Adjektiv.";
    }

    private String compareAdverbs(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // setWhTag(mReading)

        String result = compareWh(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }
        return "Probiere ein anderes Adverb.";
    }

    private String compareDeterminers(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // setNumberTag(mReading);
        // setCaseTag(mReading);
        // setGenderTag(mReading);
        // setWhTag(mReading);

        String result = compareWh(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareGenders(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareCases(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere einen anderen Artikel.";
    }

    private String compareNouns(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // setNumberTag(mReading);
        // setCaseTag(mReading);
        //setGenderTag(mReading);

        String result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareGenders(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareCases(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere ein anderes Nomen.";
    }

    private String compareNounVerbCombinations(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // setNumberTag(mReading);
        // setGenderTag(mReading);
        // setWhTag(mReading);
        // setTimeTag(mReading);
        // setStrongVerbTag(mReading);

        String result = compareWh(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareTime(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareGenders(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareStrong(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere eine andere Nomen-Verb-Kombination.";

    }

    private String comparePronouns(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // Pron {'ref2sg', '3sg', 'masc', 'ref3pl', '2pl', '3rd', '1sg', 'fem', 'reffem', 'refmasc', 'neut', 'wh', '3pl', 'nomacc', 'ref1sg', '1pl', 'ref3sg', 'GEN', 'ref1pl', 'ref2nd', 'NEG', 'nom', 'acc', 'refl', '2nd', '2sg'}
        // setNumberTag(mReading);
        // setGenderTag(mReading);
        // setWhTag(mReading);
        // setCaseTag(mReading);
        // setNegationTag(mReading);
        // setReflexiveTag(mReading);

        String result = compareWh(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareGenders(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareCases(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")) {
            return result;
        }

        return "Probiere ein anderes Pronomen.";


    }

    private String compareProperNouns(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // PropN {'3sg', '3pl', 'GEN'}
        // setNumberTag(mReading);
        // setCaseTag(mReading);

        String result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareCases(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere ein anderen Eigenname.";
    }

    private String compareVerbs(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        // V {'1sg', 'INDAUX', '3sg', 'NEG', 'INF', 'PROG', 'TO', 'STR', 'pl', '3pl', 'PAST', 'PRES', 'WK', 'PPART', 'PASSIVE', 'CONTR', '2sg'}
        // setNumberTag(mReading);
        // setINDAUXTag(mReading);
        // setNegationTag(mReading);
        // setTimeTag(mReading);
        // setTOTag(mReading);
        // setStrongVerbTag(mReading);
        // setWeakVerbTag(mReading);
        // setPassiveTag(mReading);
        // setCONTRTag(mReading);


        String result = compareNegation(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareNumbers(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareTime(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareWeak(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareStrong(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = comparePassive(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        result = compareTO(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere ein anderes Verb.";
    }

    private String compareVerbVerbCombinations(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading){
        // VVC {'PRES', 'INF'}
        // setTimeTag(mReading);

        String result = compareTime(morphologicalAppReading, morphologicalLearnerReading);
        if (!result.equals("")){
            return result;
        }

        return "Probiere eine andere Verb Verb Kombination.";
    }



    /*
     HELPER METHODS: LINGUISTIC CASES
     */
    private String compareNumbers(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading){
        if (!morphologicalAppReading.getNumber().equals("")){
            // solution has a number tag
            if (!morphologicalLearnerReading.getNumber().equals("")){
                // learner also has an number tag
                if (morphologicalAppReading.getNumber().equals(morphologicalLearnerReading.getNumber())) {
                    return "";
                }
                else {
                    return "Das Wort wird in " + numberToString(morphologicalAppReading.getNumber())
                            + " gesucht, nicht in " + numberToString(morphologicalLearnerReading.getNumber())
                            + ".";
                }
            }
            else {
                return "Das Wort wird in " + numberToString(morphologicalAppReading.getNumber())
                        + " gesucht.";
            }
        }
        else {
            // solution has no number marker
            if (!morphologicalLearnerReading.getNumber().equals("")){
                // learner has number marker
                return "Deine Eingabe ist in " + numberToString(morphologicalLearnerReading.getNumber())
                        + ". Bist du sicher ob das richtig ist?";
            }
        }

        return "";
    }

    private String compareCases(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (!morphologicalAppReading.getLingCase().equals("")){
            // solution has a case marker
            if (!morphologicalLearnerReading.getLingCase().equals("")){
                // learner also has an case marker
                if (morphologicalAppReading.getLingCase().equals(morphologicalLearnerReading.getLingCase())) {
                    return "";
                }
                else {
                    return "Das Wort wird in " + caseToString(morphologicalAppReading.getLingCase())
                            + "gesucht, nicht in " + caseToString(morphologicalLearnerReading.getLingCase())
                            + ".";
                }
            }
            else {
                return "Das Wort wird in " + caseToString(morphologicalAppReading.getLingCase())
                        + "gesucht.";
            }
        }
        else {
            // solution has no case marker
            if (!morphologicalLearnerReading.getLingCase().equals("")){
                // learner has case marker
                return "Deine Eingabe ist in " + caseToString(morphologicalLearnerReading.getLingCase())
                        + ". Bist du sicher ob das richtig ist?";
            }
        }
        return "";
    }

    private String compareTime(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (!morphologicalAppReading.getLingTime().equals("")){
            // solution has a case marker
            if (!morphologicalLearnerReading.getLingTime().equals("")){
                // learner also has an case marker
                if (morphologicalAppReading.getLingTime().equals(morphologicalLearnerReading.getLingTime())) {
                    return "";
                }
                else {
                    return "Das Wort wird in " + timeToString(morphologicalAppReading.getLingTime())
                            + "gesucht, nicht in " + timeToString(morphologicalLearnerReading.getLingTime())
                            + ".";
                }
            }
            else {
                return "Das Wort wird in " + timeToString(morphologicalAppReading.getLingTime())
                        + "gesucht.";
            }
        }
        else {
            // solution has no case marker
            if (!morphologicalLearnerReading.getLingTime().equals("")){
                // learner has case marker
                return "Deine Eingabe ist in " + timeToString(morphologicalLearnerReading.getLingTime())
                        + ". Bist du sicher ob das richtig ist?";
            }
        }
        return "";
    }

    private String compareNegation(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsNegation()){
            // solution has a neg marker
            if (morphologicalLearnerReading.getIsNegation()){
                // learner also has an neg marker
                return "";
            }
            else {
                // learner has no infinitive but a neg marker
                return "Das Wort wird in Verneinung gesucht.";
            }
        }
        else { // solution has no neg marker
            // learner has neg
            if (morphologicalLearnerReading.getIsNegation()){
                return "Das Wort wird nicht in Verneinung gesucht.";
            }
            else {
                // no neg marker in solution and learner.
                return "";

            }
        }
    }

    private String compareWeak(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsWeakVerb()){
            // solution has a infinitive marker
            if (morphologicalLearnerReading.getIsWeakVerb()){
                // learner also has an infinitive marker
                return "";
            }
            else {
                // learner has no infinitive but a time marker
                return "Das Wort das gesucht wird ist ein schwaches Verb.";
            }
        }
        else { // solution has no infinitive marker
            // learner has infinitive
            if (morphologicalLearnerReading.getIsWeakVerb()){
                return "Das Wort das gesucht wird ist kein schwaches Verb.";
            }
            else {
                // no infinitive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareStrong(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsStrongVerb()){
            // solution has a infinitive marker
            if (morphologicalLearnerReading.getIsStrongVerb()){
                // learner also has an infinitive marker
                return "";
            }
            else {
                // learner has no infinitive but a time marker
                return "Das Wort das gesucht wird ist ein starkes Verb.";
            }
        }
        else { // solution has no infinitive marker
            // learner has infinitive
            if (morphologicalLearnerReading.getIsStrongVerb()){
                return "Das Wort das gesucht wird ist kein starkes Verb.";
            }
            else {
                // no infinitive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareNegative(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsNegation()){
            // solution has a infinitive marker
            if (morphologicalLearnerReading.getIsNegation()){
                // learner also has an infinitive marker
                return "";
            }
            else {
                // learner has no infinitive but a time marker
                return "Das Wort das gesucht wird ist ein starkes Verb.";
            }
        }
        else { // solution has no infinitive marker
            // learner has infinitive
            if (morphologicalLearnerReading.getIsNegation()){
                return "Das Wort das gesucht wird ist kein starkes Verb.";
            }
            else {
                // no infinitive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareReflexive(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsReflexive()){
            // solution has a reflexive marker
            if (morphologicalLearnerReading.getIsReflexive()){
                // learner also has an reflexive marker
                return "";
            }
            else {
                // learner has no reflexive marker
                return "Das Wort das gesucht wird ist ein reflexives Verb.";
            }
        }
        else { // solution has no reflexive marker
            // learner has reflexive marker
            if (morphologicalLearnerReading.getIsReflexive()){
                return "Das Wort das gesucht wird ist kein reflexives Verb.";
            }
            else {
                // no reflexive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareTO(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsTO()){
            // solution has a TO marker
            if (morphologicalLearnerReading.getIsTO()){
                // learner also has an TO marker
                return "";
            }
            else {
                // learner has no TO marker
                return "Das gesuchte Verb wird für den Infinitiv benötigt.";
            }
        }
        else { // solution has no TO marker
            // learner has TO marker
            if (morphologicalLearnerReading.getIsTO()){
                return "Das gesuchte Verb wird nicht für den Infinitiv benötigt.";
            }
            else {
                // no TO marker in solution and learner.
                return "";

            }
        }
    }

    private String comparePassive(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsPassive()){
            // solution has a infinitive marker
            if (morphologicalLearnerReading.getIsPassive()){
                // learner also has an infinitive marker
                return "";
            }
            else {
                // learner has no infinitive but a time marker
                return "Das Verb wird im Passiv gesucht.";
            }
        }
        else { // solution has no infinitive marker
            // learner has infinitive
            if (morphologicalLearnerReading.getIsPassive()){
                return "Das Verb wird nicht im Passiv gesucht.";
            }
            else {
                // no infinitive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareGenders(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (!morphologicalAppReading.getGender().equals("")){
            // solution has a case marker
            if (!morphologicalLearnerReading.getGender().equals("")){
                // learner also has an case marker
                if (morphologicalAppReading.getGender().equals(morphologicalLearnerReading.getGender())) {
                    return "";
                }
                else {
                    return "Das Wort wird in " + genderToString(morphologicalAppReading.getGender())
                            + "gesucht, nicht in " + genderToString(morphologicalLearnerReading.getGender())
                            + ".";
                }
            }
            else {
                return "Das Wort wird in " + genderToString(morphologicalAppReading.getGender())
                        + "gesucht.";
            }
        }
        else {
            // solution has no case marker
            if (!morphologicalLearnerReading.getGender().equals("")){
                // learner has case marker
                return "Deine Eingabe ist in " + genderToString(morphologicalLearnerReading.getGender())
                        + ". Bist du sicher ob das richtig ist?";
            }
        }
        return "";
    }

    private String compareWh(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsWh()){
            // solution has a infinitive marker
            if (morphologicalLearnerReading.getIsWh()){
                // learner also has an infinitive marker
                return "";
            }
            else {
                // learner has no infinitive but a time marker
                return "Es wird ein W-Wort gesucht.";
            }
        }
        else { // solution has no infinitive marker
            // learner has infinitive
            if (morphologicalLearnerReading.getIsWh()){
                return "Es wird kein W-Wort gesucht.";
            }
            else {
                // no infinitive marker in solution and learner.
                return "";

            }
        }
    }

    private String compareComparativeSuperlative(MorphInfoObject morphologicalAppReading, MorphInfoObject morphologicalLearnerReading) {
        if (morphologicalAppReading.getIsComparative()){
            // is compartive
            if (morphologicalLearnerReading.getIsComparative()){
                // is comparative
                return "";
            }
            else if (morphologicalLearnerReading.getIsSuperlative()){
                return "Das Adjektiv ist im Comparativ, du hast eins im Superlativ.";
            }
            else {
                return "Das Adjektiv ist im Comparativ, du hast eins in einfacher Form.";
            }
        }
        else if (morphologicalAppReading.getIsSuperlative()){
            // is superlativ
            if (morphologicalLearnerReading.getIsSuperlative()){
                // is superlativ
                return "";
            }
            else if (morphologicalLearnerReading.getIsComparative()){
                return "Das Adjektiv ist im Superlativ, du hast eins im Comparativ.";
            }
            else {
                return "Das Adjektiv ist im Superlativ, du hast eins in einfacher Form.";
            }
        }
        else {
            if (morphologicalLearnerReading.getIsSuperlative()){
                // is comparative
                return "Das Adjektiv ist in normaler Form, du hast eins im Superlativ.";
            }
            else if (morphologicalLearnerReading.getIsComparative()){
                return "Das Adjektiv ist in normaler Form, du hast eins im Comparativ.";
            }
            else {
                return "";
            }
        }
    }


    /*
    HELPER METHODS: TO STRING
    */
    private String timeToString(String lTime) {
        switch (lTime){
            case "PAST": return "Vergangenheit";
            case "PRES": return "Gegenwart";
            case "PPART": return "Partizip II";
            case "PROG": return "Verlaufsform der Gegenwart";
            default: return "Fehler";
        }
    }

    private String posToString(String pos){
        switch (pos) {
            case "A":  return "Adjektiv";
            case "Adv":  return "Adverb" ;
            case "Comp": return "Komparativ";
            case "Conj": return "Konjuktion";
            case "Det": return "Artikel";
            case "G": return "Genitive Nomen";
            case "I": return "Interjektion";
            case "N": return "Nomen";
            case "NVC": return "Nomen Verb Kombination";
            case "Part": return "Partikel";
            case "Punct": return "Satzzeichen";
            case "Pron": return "Pronomen";
            case "Prep": return "Preposition";
            case "PropN": return "Eigenname";
            case "V": return "Verb";
            case "VVC": return "Verb Verb Kombination";
            default: return "Fehler";
        }

    }

    private String numberToString(String number){
        switch (number){
            case "pl": return "Plural";
            case "SG": return "Singular";
            case "1sg": return "erste Person Singular";
            case "1pl": return "erste Person Plural";
            case "2sg": return "zweite Person Singular";
            case "2pl": return "zweite Person Plural";
            case "3sg": return "dritte Person Singular";
            case "3pl": return "dritte Person Plural";
            case "2nd": return "zweite Person";
            case "3rd": return "dritte Person";
            case "ref1sg": return "erste Person Singular reflexive";
            case "ref2sg": return "zweite Person Singular reflexive";
            case "ref3sg": return "dritte Person Singular reflexive";
            case "ref1pl": return "erste Person Plural reflexive";
            case "ref2pl": return "zweite Person Plural reflexive";
            case "ref3pl": return "dritte Person Plural reflexive";
            default: return "Fehler: nicht vorhandene Nummer";
        }
    }

    private String caseToString(String lCase){
        // "acc", "nom", "GEN", "nomacc"
        switch (lCase){
            case "acc": return "Akkusativ";
            case "nom": return "Nominative";
            case "GEN": return "Genetiv";
            case "nomacc": return "Nominative oder Akkusative";
            default: return "Fehler: nicht vorhandener Case";
        }
    }

    private String genderToString(String lTime) {
        switch (lTime){
            case "masc": return "der männlichen Form";
            case "fem": return "der weiblichen Form";
            case "neut": return "der sächlichen Form";
            case "reffem": return "reflexiven weiblichen Form";
            case "refmasc": return "reflexiv männlichen Form";
            default: return "Fehler";
        }
    }

    /*
     HELPER METHODS: SOUNDEX, LEVENSHTEIN
     */

    private String generateSoundex(String learner_word) {
        // digits holds the soundex values for the alphabet
        char[] digits = "01230120022455012623010202".toCharArray();
        String sndx = "";
        Character fc = null;

        //soundex = []
        ArrayList<String> soundex = new ArrayList<>();
        int l = 0;

        // translate alpha chars in name to soundex digits
        String[] slit_by_space = learner_word.split(" ");
        String concat_string = "";
        for (String word : slit_by_space){
            sndx = "";
            l = word.length();
            for (char c : word.toUpperCase().toCharArray()){
                if (Character.isLetter(c)){
                    if (fc == null){
                        fc = c; // remember first letter
                    }
                    char d = digits[(int) c - (int) 'A'];
                    // duplicate consecutive soundex digits are skipped
                    if (sndx.isEmpty() || (d != sndx.substring(sndx.length() - 1).charAt(0))){
                        sndx += d;
                    }
                }
            }
            // replace first digit with first alpha character
            sndx = sndx.replaceFirst(sndx.substring(0,1), fc.toString());
            // remove all 0s from the soundex code
            sndx = sndx.replaceAll("0", "");
            // soundex code padded to len characters

            String sound = StringUtils.rightPad(sndx, l, "0");
            concat_string += sound + " ";
        }

        return concat_string.trim();
    }

    //todo generate german soundex

    private int levenshteinDistance (CharSequence lhs, CharSequence rhs) {
        // todo https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

}
