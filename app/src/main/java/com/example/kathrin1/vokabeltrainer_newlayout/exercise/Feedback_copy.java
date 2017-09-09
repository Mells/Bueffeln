package com.example.kathrin1.vokabeltrainer_newlayout.exercise;

import android.content.Context;

import com.example.kathrin1.vokabeltrainer_newlayout.database.DatabaseManager;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.MorphInfoObject;
import com.example.kathrin1.vokabeltrainer_newlayout.objects.MorphObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kathrin1 on 03.05.17.
 */

public class Feedback_copy {

    private String learnerAnswer;
    private String learner_pos;
    private String learner_lemma;


    // BOOK Tags: a, av, d, i, irr, k, p, ph, pr, s, v
    // xtag: A, Adv, Comp, Conj, Det, G, I, N, NVC, Part, Punct, Pron, Prep, PropN, V, VVC


    private Map<String, List> postags = new HashMap<>();

    private List app_vocable;
    private String app_pos;
    private String app_lemma;
    private String app_soundex;

    private Boolean isEnglishWord;

    private DatabaseManager dbManager;

    private String reading1, reading2, reading3, reading4, reading5, reading6, reading7, reading8;
    private String[] allReadings;

    public Feedback_copy(String vocableByLearner, List voc_vocable, String pos, String lemma, Boolean isEnglishWord, Context c){
        this.learnerAnswer = vocableByLearner;
        if (voc_vocable.get(0) instanceof List) {
            this.app_vocable = (ArrayList<ArrayList<String>>) voc_vocable;
        }
        else {
            this.app_vocable = (ArrayList<String>) voc_vocable;
        }
        this.app_vocable = voc_vocable;
        this.app_pos = pos;
        this.app_lemma = lemma;
        // todo add soundex
        this.app_soundex = "";
        this.isEnglishWord = isEnglishWord;
        dbManager = DatabaseManager.build(c);

        // BOOK Tags: i, irr, ph
        // xtag: G, I, NVC, Part(Partical?)
        // Penn: EX (existential there), TO	(infinitive ‘to’), UH (interjection), RP (particle)
        //CARDINAL_NUMBER = Arrays.asList(new String[]{"CD"}); - only penn
        //FOREIGN_WORD = Arrays.asList(new String[]{"FW"}); - only penn
        postags.put("NOUN", Arrays.asList(new String[]{"N", "PropN", "NN", "NNS", "NNSZ", "NNZ", "NP", "NPS",
                "NPSZ", "NPZ"}));
        postags.put("DETERMINER", Arrays.asList(new String[]{"Det", "DT", "WDT"}));
        postags.put("ADJECTIVE", Arrays.asList(new String[]{"A", "JJ", "JJR", "JJS"}));
        postags.put("VERB", Arrays.asList(new String[]{"V", "VVC", "MD", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ",
                "VH", "VHD", "VHG", "VHN", "VHP", "VHZ", "VV", "VVD", "VVG", "VVN", "VVP", "VVZ"}));
        postags.put("PRONOUN", Arrays.asList(new String[]{"Pron", "PP", "PP$", "WP", "WPZ"}));
        postags.put("ADVERB", Arrays.asList(new String[]{"Adv", "RB", "RBR", "RBS"}));
        postags.put("PREPOSITION", Arrays.asList(new String[]{"Prep", "IN"}));
        postags.put("CONJUNCTION", Arrays.asList(new String[]{"Conj", "CC"}));
        postags.put("SENTENCE_MARKER", Arrays.asList(new String[]{"Punct", "SENT"}));
    }

    public String generateFeedback(){
        // ## single word single answer ##
        // Vokabel | pro_voc | tag_proc_voc    | soundex | lemma   | tag_lemma_voc   | simple_tagged_lemma_vocable;
        // can     | ['can'] | [('can', 'MD')] | ['C50'] | ['can'] | [('can', 'MD')] | [('can', 'MD')]

        // single word multiple answer
        // Vokabel | pro_voc         | tag_proc_voc                    | soundex
        // a/an    | [['a'], ['an']] | [[('a', 'DT')], [('an', 'DT')]] | [['A'], ['A5']]
        // --------------------------------------------------------------------------------------
        // | lemma           | tag_lemma_voc                   | simple_tagged_lemma_vocable;
        // | [['a'], ['an']] | [[('a', 'DT')], [('an', 'DT')]] | [[('a', 'DT')], [('an', 'DT')]]

        // ## phrase one answer ##
        // Vokabel                 | pro_voc                    | tag_proc_voc
        // Welcome to Camden Town! | ['Welcome to Camden Town'] | [('Welcome', 'JJ'), ('to', 'TO'), ('Camden', 'NP'), ('Town', 'NP')]
        // --------------------------------------------------------------------------------------------------------------------------
        // | soundex                    | lemma                               | tag_lemma_voc
        // | ['W425000 W0 W53500 W500'] | ['welcome', 'to', 'Camden', 'Town'] | [('welcome', 'JJ'), ('to', 'TO'), ('Camden', 'NP'), ('Town', 'NP')]
        // --------------------------------------------------------------------------------------------------------------------------
        // | simple_tagged_lemma_vocable
        // | [('welcome', 'JJ'), ('to', 'TO'), ('Camden', 'NP'), ('Town', 'NP')]

        // phrase multiple answer
        // Vokabel                  | pro_voc                                    |
        // to expect/to have a baby | [['to expect a baby'], ['to have a baby']] |
        // ----------------------------------------------------------------------------------
        // tag_proc_voc    | soundex | lemma   | tag_lemma_voc   | simple_tagged_lemma_vocable;
        // [[('to', 'TO'), ('expect', 'VV'), ('a', 'DT'), ('baby', 'NN')], [('to', 'TO'), ('have', 'VH'), ('a', 'DT'), ('baby', 'NN')]];[['T0 T21230 T T100'], ['T0 T100 T T100']];[['expect', 'a', 'baby'], ['have', 'a', 'baby']];[[('expect', 'VV'), ('a', 'DT'), ('baby', 'NN')], [('have', 'VH'), ('a', 'DT'), ('baby', 'NN')]];[[('expect', 'VV'), ('a', 'DT'), ('baby', 'NN')], [('have', 'VH'), ('a', 'DT'), ('baby', 'NN')]]



        // if <TargetAnswer> is a list longer than one:	# there are alternative answers
        // ['can'] - [['a'], ['an']] - ['Welcome to Camden Town'] - [['to expect a baby'], ['to have a baby']]
        String singleTarget;
        if (app_vocable.get(0) instanceof List) {
            int topDistance = 1000;
            String topAnswer = "";
            // select element from list that has shortest Levensthein distance with LearnerString
            for (int i = 0; i <= app_vocable.size(); i++){
                String answer = (String) app_vocable.get(i);
                int distance = levenshteinDistance(answer, learnerAnswer);
                if (distance < topDistance){
                    topDistance = distance;
                    topAnswer = answer;
                }
            }
            // call it <SingleTarget>
            singleTarget = topAnswer;
        } else { // there is a single answer
            // <SingleTarget> = <TargetAnswer>
            singleTarget = (String) app_vocable.get(0);
        }

        // if <SingleTarget> is a single word:
        if (singleTarget.split("\\s").length < 2) {
            //if <LearnerString> is a single word:
            if (learnerAnswer.split("\\s").length < 2) {
                //	<xtag> the morphological database
                MorphObject wordReading = dbManager.getMorphInformation(learnerAnswer);
                // if: <SingleTarget> is not known in <xtag>
                if (wordReading.isEmpty()) {
                    // calculate  <soundex> of learner word
                    String learner_soundex = generateSoundex(learnerAnswer);

                    // calculate <sound_levenshtein> between soundex
                    int levenshtein_soundex = levenshteinDistance(learner_soundex, singleTarget);

                    // calculate <word_levenshtein> between words
                    int levenshtein_spelling = levenshteinDistance(learnerAnswer, singleTarget);

                    // if <sound_levenshtein> distance = 0
                    if (levenshtein_soundex == 0) {
                        // return “sounds the same”
                        return "Du hast kein bekanntes Wort eingegeben, " +
                                "aber die Lösung klingt gleich wie deine Eingabe";
                    }
                    else if (levenshtein_soundex == levenshtein_spelling) { // if <sound_levenshtein> = <word_levenshtein>
                        return "";
                    } else if (levenshtein_soundex < levenshtein_spelling) { // else if <sound_levenshtein> smaller than <word_levenshtein>
                        // return "Das Wort klingt gleich"
                        // todo threshold
                        return "Du hast kein bekanntes Wort eingegeben, aber die Lösung klingt " +
                            "ähnlich. Bitte überprüfe deine Rechtschreibung";
                    } else if (levenshtein_soundex > levenshtein_spelling){ // else if <sound_levenshtein> larger than <word_levenshtein>
                        // return "schau nach einem Rechtschreibfehler"
                        // todo threshold
                        return "Du hast kein bekanntes Wort eingegeben, " +
                            "bitte überprüfe deine Rechtschreibung.";
                    }
                } else { //	else # <SingleTarget> is known in <xtag>
                    // <xtag> has <WordReading>
                    // <wordReading> has <morphologicalReading>

                    MorphInfoObject morphologicalReading;
                    if (wordReading.getNumberOfReadings() > 1) { // <WordReading> has more than one <MorphologicalReading>
                        // get the best <morphologicalReading> through levenshtein
                        morphologicalReading = getBestReading(app_pos, wordReading.getAllReadings());
                    }
                    else {
                        // get <morphologicalReading> from <wordReading>
                        morphologicalReading = new MorphInfoObject(wordReading.getReading1());
                    }

                    comparePOSTags(morphologicalReading, app_pos);

                }
            }
            else { // <LearnerString> is more than a single word:
                return "Deine Antwort beinhaltet " + Integer.toString(learnerAnswer.split("\\s").length) +
                        " Wörter, es wird aber nur eins gesucht";
            }
        } else { // <SingleTarget> is more than one word
            // ['to expect a baby']
            if (singleTarget.split("\\s").length == learnerAnswer.split("\\s").length) { // have the same number of words

            }
            else { // have not the same number of words

            }

        }

        return null;
    }

    //
    // GET THE BEST MORPHOLOGICAL READING
    //
    private MorphInfoObject getBestReading(String app_pos, String[] allReadings) {
        String pos_app = "";
        for (Map.Entry<String, List> entry : postags.entrySet()) {
            if (entry.getValue().equals(app_pos)) {
                pos_app = entry.getKey();
            }
            switch (pos_app) {

                case "NOUN": return getBestNounReading(app_pos, allReadings);
                case "DETERMINER": return getBestDeterminerReading(app_pos, allReadings);
                case "ADJECTIVE": return getBestAdjectiveReading(app_pos, allReadings);
                case "VERB": return getBestVerbReading(app_pos, allReadings);
                case "PRONOUN": return getBestPronounReading(app_pos, allReadings);
                case "ADVERB": return getBestAdverbReading(allReadings);
                case "PREPOSITION": return getBestPrepositionReading(allReadings);
                case "CONJUNCTION": return getBestConjunctionReading(allReadings);
                case "SENTENCE_MARKER": return getBestSentenceMarkerReading(allReadings);
                default: new MorphInfoObject("");

            }
        }
        return null;
    }

    private MorphInfoObject getBestNounReading(String app_pos, String[] allReadings) {
        // NN, NNZ      = N (sg, 2nd, 3rd)
        // NNS, NNSZ    = N (pl)
        // NP, NPZ      = PropN (sg, 2nd, 3rd)
        // NPS, NPSZ    = PropN (pl)
        int bestLevenshteinDistance = 100;
        String bestReading = "";
        for (String reading : allReadings){
            if (reading.substring(0,1).equals("N")){
                if (reading.contains("sg") || reading.contains("2nd") || reading.contains("3rd")){
                    int nn = levenshteinDistance(app_pos, "NN");
                    if (nn < bestLevenshteinDistance){
                        bestLevenshteinDistance = nn;
                        bestReading = reading;
                    }
                    int nnz = levenshteinDistance(app_pos, "NNZ");
                    if (nnz < bestLevenshteinDistance){
                        bestLevenshteinDistance = nnz;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("pl")){
                    int nns = levenshteinDistance(app_pos, "NNS");
                    if (nns < bestLevenshteinDistance){
                        bestLevenshteinDistance = nns;
                        bestReading = reading;
                    }
                    int nnsz = levenshteinDistance(app_pos, "NNSZ");
                    if (nnsz < bestLevenshteinDistance){
                        bestLevenshteinDistance = nnsz;
                        bestReading = reading;
                    }
                }
            }
            else if (reading.substring(0,5).equals("PropN")){
                if (reading.contains("sg") || reading.contains("2nd") || reading.contains("3rd")){
                    int np = levenshteinDistance(app_pos, "NP");
                    if (np < bestLevenshteinDistance){
                        bestLevenshteinDistance = np;
                        bestReading = reading;
                    }
                    int npz = levenshteinDistance(app_pos, "NPZ");
                    if (npz < bestLevenshteinDistance){
                        bestLevenshteinDistance = npz;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("pl")){
                    int nps = levenshteinDistance(app_pos, "NPS");
                    if (nps < bestLevenshteinDistance){
                        bestLevenshteinDistance = nps;
                        bestReading = reading;
                    }
                    int npsz = levenshteinDistance(app_pos, "NPSZ");
                    if (npsz < bestLevenshteinDistance){
                        bestLevenshteinDistance = npsz;
                        bestReading = reading;
                    }
                }
            }
        }
        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestDeterminerReading(String app_pos, String[] allReadings) {
        // DT   = Det
        // WDT  = Det wh
        int bestLevenshteinDistance = 100;
        String bestReading = "";
        for (String reading : allReadings){
            if (reading.substring(0,3).equals("Det")){
                if (reading.contains(" wh")){
                    int wdt = levenshteinDistance(app_pos, "WDT");
                    if (wdt < bestLevenshteinDistance){
                        bestLevenshteinDistance = wdt;
                        bestReading = reading;
                    }
                }
                else {
                    int dt = levenshteinDistance(app_pos, "DT");
                    if (dt < bestLevenshteinDistance){
                        bestLevenshteinDistance = dt;
                        bestReading = reading;
                    }
                }
            }
        }
        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestAdjectiveReading(String app_pos, String[] allReadings) {
        // JJ   = A -comp -super
        // JJR  = A +comp
        // JJS  = A +super

        int bestLevenshteinDistance = 100;
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 1).equals("A")) {
                if (reading.contains("COMP")){
                    int jjr = levenshteinDistance(app_pos, "JJR");
                    if (jjr < bestLevenshteinDistance){
                        bestLevenshteinDistance = jjr;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("SUPER")) {
                    int jjs = levenshteinDistance(app_pos, "JJS");
                    if (jjs < bestLevenshteinDistance){
                        bestLevenshteinDistance = jjs;
                        bestReading = reading;
                    }
                }
                else {
                    int jj = levenshteinDistance(app_pos, "JJ");
                    if (jj < bestLevenshteinDistance){
                        bestLevenshteinDistance = jj;
                        bestReading = reading;
                    }
                }
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestVerbReading(String app_pos, String[] allReadings) {
        // MD                   = V PRES
        // VBP                  = V PRES [1|2]sg
        // VBZ, VHZ, VVZ        = V PRES 3sg
        // VB, VH, VHP, VV, VVP = V INF
        // VBD, VHD, VVD        = V PAST
        // VBG, VHG, VVG        = V PROG
        // VBN, VHN, VVN        = V PPART

        int bestLevenshteinDistance = 100;
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 1).equals("V")) {
                if (reading.contains("PRES")) {
                    if (reading.matches("[1|2]sg")) {
                        int vbp = levenshteinDistance(app_pos, "VBP");
                        if (vbp < bestLevenshteinDistance){
                            bestLevenshteinDistance = vbp;
                            bestReading = reading;
                        }
                    }
                    else if (reading.contains("3sg")) {
                        int vbz = levenshteinDistance(app_pos, "VBZ");
                        if (vbz < bestLevenshteinDistance){
                            bestLevenshteinDistance = vbz;
                            bestReading = reading;
                        }
                        int vhz = levenshteinDistance(app_pos, "VHZ");
                        if (vhz < bestLevenshteinDistance){
                            bestLevenshteinDistance = vhz;
                            bestReading = reading;
                        }
                        int vvz = levenshteinDistance(app_pos, "VVZ");
                        if (vvz < bestLevenshteinDistance){
                            bestLevenshteinDistance = vvz;
                            bestReading = reading;
                        }
                    }
                    else {
                        int md = levenshteinDistance(app_pos, "MD");
                        if (md < bestLevenshteinDistance){
                            bestLevenshteinDistance = md;
                            bestReading = reading;
                        }
                    }
                }
                else if (reading.contains("INF")) {
                    int vb = levenshteinDistance(app_pos, "VB");
                    if (vb < bestLevenshteinDistance){
                        bestLevenshteinDistance = vb;
                        bestReading = reading;
                    }
                    int vh = levenshteinDistance(app_pos, "VH");
                    if (vh < bestLevenshteinDistance){
                        bestLevenshteinDistance = vh;
                        bestReading = reading;
                    }
                    int vhp = levenshteinDistance(app_pos, "VHP");
                    if (vhp < bestLevenshteinDistance){
                        bestLevenshteinDistance = vhp;
                        bestReading = reading;
                    }
                    int vv = levenshteinDistance(app_pos, "VV");
                    if (vv < bestLevenshteinDistance){
                        bestLevenshteinDistance = vv;
                        bestReading = reading;
                    }
                    int vvp = levenshteinDistance(app_pos, "VVP");
                    if (vvp < bestLevenshteinDistance){
                        bestLevenshteinDistance = vvp;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("PAST")) {
                    int vbd = levenshteinDistance(app_pos, "VBD");
                    if (vbd < bestLevenshteinDistance){
                        bestLevenshteinDistance = vbd;
                        bestReading = reading;
                    }
                    int vhd = levenshteinDistance(app_pos, "VHD");
                    if (vhd < bestLevenshteinDistance){
                        bestLevenshteinDistance = vhd;
                        bestReading = reading;
                    }
                    int vvd = levenshteinDistance(app_pos, "VVD");
                    if (vvd < bestLevenshteinDistance){
                        bestLevenshteinDistance = vvd;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("PROG")) {
                    int vbg = levenshteinDistance(app_pos, "VBG");
                    if (vbg < bestLevenshteinDistance){
                        bestLevenshteinDistance = vbg;
                        bestReading = reading;
                    }
                    int vhg = levenshteinDistance(app_pos, "VHG");
                    if (vhg < bestLevenshteinDistance){
                        bestLevenshteinDistance = vhg;
                        bestReading = reading;
                    }
                    int vvg = levenshteinDistance(app_pos, "VVG");
                    if (vvg < bestLevenshteinDistance){
                        bestLevenshteinDistance = vvg;
                        bestReading = reading;
                    }
                }
                else if (reading.contains("PPART")) {
                    int vbn = levenshteinDistance(app_pos, "VBN");
                    if (vbn < bestLevenshteinDistance){
                        bestLevenshteinDistance = vbn;
                        bestReading = reading;
                    }
                    int vhn = levenshteinDistance(app_pos, "VHN");
                    if (vhn < bestLevenshteinDistance){
                        bestLevenshteinDistance = vhn;
                        bestReading = reading;
                    }
                    int vvn = levenshteinDistance(app_pos, "VVN");
                    if (vvn < bestLevenshteinDistance){
                        bestLevenshteinDistance = vvn;
                        bestReading = reading;
                    }
                }
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestPronounReading(String app_pos, String[] allReadings) {
        // PP, PP$  = Pron
        // WP, WPZ  = Pron wh
        int bestLevenshteinDistance = 100;
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 4).equals("Pron")) {
                if (reading.contains(" wh")){
                    int pp = levenshteinDistance(app_pos, "PP");
                    if (pp < bestLevenshteinDistance){
                        bestLevenshteinDistance = pp;
                        bestReading = reading;
                    }
                    int ppz = levenshteinDistance(app_pos, "PP$");
                    if (ppz < bestLevenshteinDistance){
                        bestLevenshteinDistance = ppz;
                        bestReading = reading;
                    }
                }
                else {
                    int wp = levenshteinDistance(app_pos, "WP");
                    if (wp < bestLevenshteinDistance){
                        bestLevenshteinDistance = wp;
                        bestReading = reading;
                    }
                    int wpz = levenshteinDistance(app_pos, "WPZ");
                    if (wpz < bestLevenshteinDistance){
                        bestLevenshteinDistance = wpz;
                        bestReading = reading;
                    }
                }
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestAdverbReading(String[] allReadings) {
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 3).equals("Adv")) {
                bestReading = reading;
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestPrepositionReading(String[] allReadings) {
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 4).equals("Prep")) {
                bestReading = reading;
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestConjunctionReading(String[] allReadings) {
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 4).equals("Conj")) {
                bestReading = reading;
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }

    private MorphInfoObject getBestSentenceMarkerReading(String[] allReadings) {
        String bestReading = "";

        for (String reading : allReadings) {
            if (reading.substring(0, 5).equals("Punct")) {
                bestReading = reading;
            }
        }

        if (bestReading.isEmpty()){
            return new MorphInfoObject(allReadings[0]);
        }
        else {
            return new MorphInfoObject(bestReading);
        }
    }


    //
    // HELPER METHODS: SOUNDEX & LEVENSHTEIN
    //
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

    public int levenshteinDistance (CharSequence lhs, CharSequence rhs) {
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


    //
    // COMPARE POS TAGS
    //
    private String comparePOSTags(MorphInfoObject mio, String app_pos) {
        String pos_app = "";
        for (Map.Entry<String, List> entry : postags.entrySet()) {
            if (entry.getValue().equals(app_pos)) {
                pos_app = entry.getKey();
            }
            switch (pos_app) {

                case "NOUN": return compareNouns(pos_app, mio);
                case "DETERMINER": return compareDeterminers(pos_app, mio);
                case "ADJECTIVE": return compareAdjectives(pos_app, mio);
                case "VERB": return compareVerbs(pos_app, mio);
                case "PRONOUN": return comparePronouns(pos_app, mio);
                case "ADVERB": return compareAdverbs(pos_app, mio);
                case "PREPOSITION": return "Du brauchst eine Präposition.";
                case "CONJUNCTION": return "Du brauchst eine Konjunktion.";
                case "SENTENCE_MARKER": return "Dir fehlt ein Satzzeichen.";
                default: return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";

            }
        }
        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
    }

    private String compareNouns(String pos_app, MorphInfoObject mio) {
        // xtag "N", "PropN"
        //      - setNumberTag(mReading);
        //      - setCaseTag(mReading);
        //      - setGenderTag(mReading); - not poss.
        switch (pos_app){
            case "NN":      // noun, singular or mass
                if (mio.getPOS().equals("N")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("sg") || number.contains("2nd") || number.contains("3rd")) {
                        return "Es tut mir leid ich kann dir leider nicht weiterhelfen.";
                    }
                    else {
                        return "Es wird ein Noun mit der Eigenschaft: Singular gesucht, du hast " +
                                "eins in Plural.";
                    }
                }
                else {
                    return "Es wird ein Noun mit den Eigenschaften: Singular oder Stoffname " +
                            "gesucht, hast du eventuel einen Eigennamen eingegeben?.";
                }
            case "NNS":     // noun plural
                if (mio.getPOS().equals("N")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("pl")) {
                        return "Es tut mir leid ich kann dir leider nicht weiterhelfen.";
                    }
                    else {
                        return "Es wird ein Noun mit der Eigenschaft: Plural gesucht, du hast " +
                                "eins in Singular.";
                    }
                }
                else {
                    return "Es wird ein Noun mit der Eigenschaft: Plural gesucht, hast du eventuel " +
                            "einen Eigennamen eingegeben?.";
                }
            case "NNSZ":    // possesive noun plural
                if (mio.getPOS().equals("N")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("pl")) {
                        return "Es tut mir leid ich kann dir leider nicht weiterhelfen.";
                    }
                    else {
                        return "Es wird ein Noun mit den Eigenschaften: Possesive und Plural " +
                                "gesucht, du hast eins in Singular.";
                    }
                }
                else {
                    return "Es wird ein Noun mit den Eigenschaften: Possesive und Plural gesucht, " +
                            "hast du eventuel einen Eigennamen eingegeben?.";
                }
            case "NNZ":     // possesive noun, singular or mass
                if (mio.getPOS().equals("N")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("sg") || number.contains("2nd") || number.contains("3rd")) {
                        return "Es wird ein Noun mit den Eigenschaften: Possesive und " +
                                "(Singular oder Stoffname) gesucht. Ist dein Nomen eventuell " +
                                "nicht Possesive?";
                    }
                    else {
                        return "Es wird ein Noun mit den Eigenschaften: Possesive und " +
                                "(Singular oder Stoffname) gesucht, du hast eins in Plural.";
                    }
                }
                else {
                    return "Es wird ein Noun mit den Eigenschaften: Possesive und " +
                            "(Singular oder Stoffname) gesucht, hast du eventuel einen " +
                            "Eigennamen eingegeben?.";
                }
            case "NP":      // proper noun, singular
                if (mio.getPOS().equals("PropN")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("sg") || number.contains("2nd") || number.contains("3rd")) {
                        return "Es tut mir leid ich kann dir leider nicht weiterhelfen.";
                    }
                    else {
                        return "Es wird ein Eigenname mit der Eigenschaft: Singular gesucht, du " +
                                "hast eins in Plural.";
                    }
                }
                else {
                    return "Es wird ein Eigenname mit der Eigenschaft: Singular gesucht, " +
                            "hast du eventuel eine Nomen eingegeben?.";
                }
            case "NPS":     // proper noun, plural
                if (mio.getPOS().equals("PropN")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("pl")) {
                        return "Es tut mir leid ich kann dir leider nicht weiterhelfen.";
                    }
                    else {
                        return "Es wird ein Eigenname mit der Eigenschaft: Plural gesucht, du " +
                                "hast eins in Singular.";
                    }
                }
                else {
                    return "Es wird ein Eigenname mit der Eigenschaft: Plural gesucht, " +
                            "hast du eventuel eine Nomen eingegeben?.";
                }
            case "NPSZ":    // possesive proper noun, plural
                if (mio.getPOS().equals("PropN")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("pl")) {
                        return "Es wird ein Eigenname mit den Eigenschaften: Possesive und " +
                                "Plural gesucht. Ist dein Eigenname eventuell " +
                                "nicht Possesive?";
                    }
                    else {
                        return "Es wird ein Eigenname mit den Eigenschaften: Possesive und Plural " +
                                "gesucht, du hast eins in Singular.";
                    }
                }
                else {
                    return "Es wird ein Eigenname mit den Eigenschaft: Possesive und Plural gesucht, " +
                            "hast du eventuel eine Nomen eingegeben?.";
                }
            case "NPZ":     // possesive proper noun, singular
                if (mio.getPOS().equals("PropN")){
                    // check number, poss. {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
                    // "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};
                    String number = mio.getNumber().toLowerCase();
                    if (number.contains("sg") || number.contains("2nd") || number.contains("3rd")) {
                        return "Es wird ein Eigenname mit den Eigenschaften: Possesive und " +
                                "Singular gesucht. Ist dein Eigenname eventuell " +
                                "nicht Possesive?";
                    }
                    else {
                        return "Es wird ein Eigenname mit den Eigenschaften: Possesive und Singular " +
                                "gesucht, du hast eins in Plural.";
                    }
                }
                else {
                    return "Es wird ein Eigenname mit den Eigenschaft: Possesive und Singular gesucht, " +
                            "hast du eventuel eine Nomen eingegeben?.";
                }
            default:
                return "Es tut mir leid irgendetwas ist schief gelaufen.";
        }
    }

    private String compareDeterminers(String pos_app, MorphInfoObject mio) {
        // xtag "Det":
        //      - setNumberTag(mReading);
        //      - setCaseTag(mReading);
        //      - setGenderTag(mReading);
        //      - setWhTag(mReading);
        switch (pos_app){
            case "DT":
                if (mio.getIsWh()) {
                    return "Ein Artikel ist schon richtig, wir suchen aber keinen wh-Artikel.";
                }
                else {
                    return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                }
            case "WDT":
                if (!mio.getIsWh()) {
                    return "Ein Artikel ist schon richtig, wir suchen aber einen wh-Artikel.";
                }
                else {
                    return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                }
        }
        return pos_app;
    }

    private String compareAdjectives(String pos_app, MorphInfoObject mio) {
        // xtag "A"
        //      - isComparative
        //      - isSuperlative
        switch (pos_app) {
            case "JJ":  // adjective
                if (mio.getIsComparative()){
                    return "Du hast ein Adjective im Komparativ du brauchst aber eins im Posetiv.";
                }
                else if (mio.getIsSuperlative()) {
                    return "Du hast ein Adjective im Superlativ du brauchst aber eins im Posetiv";
                }
                else {
                    return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                }
            case "JJR": // adjective, comparative
                if (mio.getIsComparative()){
                    return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                }
                else if (mio.getIsSuperlative()) {
                    return "Du hast ein Adjective im Superlativ du brauchst aber eins im Komparativ";
                }
                else {
                    return "Du hast ein Adjective im Posetiv du brauchst aber eins im Komparativ";
                }
            case "JJS": // adjective, superlative
                if (mio.getIsComparative()){
                    return "Du hast ein Adjective im Komparativ du brauchst aber eins im Superlativ";
                }
                else if (mio.getIsSuperlative()) {
                    return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                }
                else {
                    return "Du hast ein Adjective im Posetiv du brauchst aber eins im Superlativ";
                }
                default: return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";

        }
    }

    private String compareVerbs(String pos_app, MorphInfoObject mio) {
        // xtag "V", "VVC"
        //      - setNumberTag(mReading);
        //      - setINDAUXTag(mReading);
        //      - setNegationTag(mReading);
        //      - setInfinitiveTag(mReading);
        //      - setTimeTag(mReading);
        //      - setTOTag(mReading);
        //      - setStrongVerbTag(mReading);
        //      - setWeakVerbTag(mReading);
        //      - setPassiveTag(mReading);
        //      - setCONTRTag(mReading);

        switch (pos_app){
            case "MD":      // modal
                // ;can;can;V INF#can;V PRES;
                // ;could;could;V PRES;
                return "Es wird ein Modalverb gesucht.";

            case "VB":      // verb be, base form
                // ;be;be;V INF;
                if (mio.getLemma().equals("be")){
                    if (mio.getIsInfinitive()){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()){
                        return "Du hast das richtige Wort, wir suchen es aber im Infinitiv. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Infinitiv.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VBD":     // verb be, past tense
                // ;was;be;V 1sg PAST STR#be;V 3sg PAST STR;
                if (mio.getLemma().equals("be")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Vergangenheitsform.";
                    }
                    else if (mio.getLingTime().equals("PAST")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Vergangenheitsform. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Vergangenheitsform.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VBG":     // verb be, gerund/present participle
                // ;being;being;N 3sg#be;V PROG;
                if (mio.getLemma().equals("be")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Verlaufsform der Gegenwart.";
                    }
                    else if (mio.getLingTime().equals("PROG")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Verlaufsform der Gegenwart. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Verlaufsform der Gegenwart.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VBN":     // verb be, past participle
                // ;been;be;V PPART STR;
                if (mio.getLemma().equals("be")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern im Partizip II.";
                    }
                    else if (mio.getLingTime().equals("PPART")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber im Partizip II. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Partizip II.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VBP":     // verb be, sing. present, non-3d
                // ;am;be;V 1sg PRES;
                if (mio.getLemma().equals("be")){
                    String number = mio.getNumber();
                    // "1sg", "2sg"
                    if (number.matches("[1|2]sg")) {
                        if (mio.getIsInfinitive()) {
                            return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                    "sondern in der Gegenwart";
                        } else if (mio.getLingTime().equals("PRES")) {
                            return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                        } else if (!mio.getLingTime().isEmpty()) {
                            return "Du hast das richtige Wort, wir suchen es aber in der Gegenwart. " +
                                    "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                        } else {
                            return "Du brauchst das Wort in der Gegenwart, Singular und es darf " +
                                    "nicht in 3ter Form sein.";
                        }
                    }
                    else {
                        return "Das Wort muss im Singular und nicht in 3ter Form sein.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VBZ":     // verb be, 3rd person sing. present
                // ;is;be;V 3sg PRES;
                if (mio.getLemma().equals("be")){
                    String number = mio.getNumber();
                    // "3sg"
                    if (number.equals("3sg")) {
                        if (mio.getIsInfinitive()) {
                            return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                    "sondern in der Gegenwart";
                        } else if (mio.getLingTime().equals("PRES")) {
                            return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                        } else if (!mio.getLingTime().isEmpty()) {
                            return "Du hast das richtige Wort, wir suchen es aber in der Gegenwart. " +
                                    "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                        } else {
                            return "Du brauchst das Wort in der Gegenwart und muss in der 3ter " +
                                    "Form Singular sein.";
                        }
                    }
                    else {
                        return "Das Wort muss in der 3ter Form Singular sein.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VH":      // verb have, base form
                // ;have;have;V INF;
                if (mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()){
                        return "Du hast das richtige Wort, wir suchen es aber im Infinitiv. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Infinitiv.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VHD":     // verb have, past tense
                // ;had;have;V PAST STR#have;V PPART STR;
                if (mio.getLemma().equals("be")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Vergangenheitsform.";
                    }
                    else if (mio.getLingTime().equals("PAST")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Vergangenheitsform. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Vergangenheitsform.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VHG":     // verb have, gerund/present participle
                // ;having;have;V PROG;
                if (mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Verlaufsform der Gegenwart.";
                    }
                    else if (mio.getLingTime().equals("PROG")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Verlaufsform der Gegenwart. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Verlaufsform der Gegenwart.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VHN":     // verb have, past participle
                // ;had;have;V PAST STR#have;V PPART STR;
                if (mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern im Partizip II.";
                    }
                    else if (mio.getLingTime().equals("PPART")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber im Partizip II. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Partizip II.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VHP":     // verb have, sing. present, non-3d
                // ;have;have;V INF;
                if (mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()) {
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    } else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber im Infinitiv. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    } else {
                        return "Du brauchst das Wort im Infinitiv.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VHZ":     // verb have, 3rd person sing. present
                // ;has;have;V 3sg PRES;
                if (mio.getLemma().equals("have")){
                    String number = mio.getNumber();
                    // "3sg""
                    if (number.equals("3sg")) {
                        if (mio.getIsInfinitive()) {
                            return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                    "sondern in der Gegenwart";
                        } else if (mio.getLingTime().equals("PRES")) {
                            return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                        } else if (!mio.getLingTime().isEmpty()) {
                            return "Du hast das richtige Wort, wir suchen es aber in der Gegenwart. " +
                                    "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                        } else {
                            return "Du brauchst das Wort in der Gegenwart und muss in der 3ter " +
                                    "Form Singular sein.";
                        }
                    }
                    else {
                        return "Das Wort muss in der 3ter Form Singular sein.";
                    }
                }
                else {
                    return "Ein Verb ist richtig, du brauch allerdings ein anderes.";
                }

            case "VV":      // verb, base form
                //
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()){
                        return "Du hast das richtige Wort, wir suchen es aber im Infinitiv. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Infinitiv.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            case "VVD":     // verb, past tense
                //
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Vergangenheitsform.";
                    }
                    else if (mio.getLingTime().equals("PAST")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Vergangenheitsform. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Vergangenheitsform.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }
            case "VVG":     // verb, gerund/present participle
                //
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern in der Verlaufsform der Gegenwart.";
                    }
                    else if (mio.getLingTime().equals("PROG")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Verlaufsform der Gegenwart. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort in der Verlaufsform der Gegenwart.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            case "VVN":     // verb, past participle
                //
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    if (mio.getIsInfinitive()){
                        return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                "sondern im Partizip II.";
                    }
                    else if (mio.getLingTime().equals("PPART")){
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    }
                    else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber im Partizip II. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    }
                    else {
                        return "Du brauchst das Wort im Partizip II.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            case "VVP":     // verb, sing. present, non-3d
                // travel;travel;V INF;
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    String number = mio.getNumber();
                    // "1sg", "2sg"
                    if (mio.getIsInfinitive()) {
                        return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                    } else if (!mio.getLingTime().isEmpty()) {
                        return "Du hast das richtige Wort, wir suchen es aber in der Gegenwart. " +
                                "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                    } else {
                        return "Du brauchst das Wort in der Gegenwart, Singular und es darf " +
                                "nicht in 3ter Form sein.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            case "VVZ":     // verb, 3rd person sing. present
                // ;sings;sing;N 3pl#sing;V 3sg PRES;
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
                    String number = mio.getNumber();
                    // "3sg""
                    if (number.equals("3sg")) {
                        if (mio.getIsInfinitive()) {
                            return "Du hast das richtige Wort wir suchen es aber nicht im Infinitiv " +
                                    "sondern in der Gegenwart";
                        } else if (mio.getLingTime().equals("PRES")) {
                            return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
                        } else if (!mio.getLingTime().isEmpty()) {
                            return "Du hast das richtige Wort, wir suchen es aber in der Gegenwart. " +
                                    "Du hast das Wort in der/im " + timeToString(mio.getLingTime());
                        } else {
                            return "Du brauchst das Wort in der Gegenwart und muss in der 3ter " +
                                    "Form Singular sein.";
                        }
                    }
                    else {
                        return "Das Wort muss in der 3ter Form Singular sein.";
                    }
                }
                else {
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            default: return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
        }
    }

    private String comparePronouns(String pos_app, MorphInfoObject mio) {
        // xtag "Pron"
        //      - setNumberTag(mReading);
        //      - setGenderTag(mReading);
        //      - setWhTag(mReading);
        //      - setCaseTag(mReading);
        //      - setNegationTag(mReading);
        //      - setReflexiveTag(mReading);
        switch (pos_app) {
            // This category includes the personal pronouns proper, without regard for case
            // distictions (I, me, you, he, him, etc.), the reflexive pronouns ending
            // in -self or -selves, and the nominal possessive pronouns mine, yours, his, hers,
            // ours and theirs.
            case "PP":      // personal pronoun
                // ;I;I;N 3sg#I;Pron 1sg nom;
                // ;myself;myself;Pron 1sg refl;
                // ;mine;mine;Pron GEN ref1sg#mine;
                // ;yours;your;Pron GEN ref2nd;         -- nothing they have in common

                return  "Es wird ein (reflexives) Pronomen gesucht";


            // This category includes the adjectival possessive forms my, your, his, her, its,
            // one's, our and their.
            case "PP$":     // possessive pronoun
                // ;your;your;Pron GEN ref2nd;
                // ;her;her;Pron 3sg acc fem#her;
                if (mio.getLingCase().equals("refl")){
                    return "Du hast ein reflexifes Pronomen eingegeben, gesucht wird aber ein " +
                            "possessives (reflexives) Pronomen";
                }
                else {
                    return "Es wird ein possessives (reflexives) Pronomen gesucht";
                }

            // This category includes what, who, and whom
            case "WP":      // wh-pronoun
                // ;what;what;Pron 3sg wh;
                return  "Es wird ein wh-Pronomen gesucht";
            // This category includes the wh-word whose
            case "WPZ":     // possessive wh-pronouns
                return  "Es wird ein possessives wh-Pronomen gesucht";

            default: return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
        }
    }

    private String compareAdverbs(String pos_app, MorphInfoObject mio) {
        // xtag 'Adv'
        //      - setWhTag(mReading);

        switch (pos_app){
            case "RB":      // adverb
                return "Du brauchst ein Adverb in der normalen Form, hast du vielleicht eins im " +
                        "Komparativ oder Superlativ verwendet?";
            case "RBR":     // adverb, comparative
                return "Du brauchst ein Adverb im Komparativ, hast du vielleicht eins im " +
                        "in der normalen Form oder Superlativ verwendet?";
            case "RBS":     // adverb, superlative
                return "Du brauchst ein Adverb im Superlativ, hast du vielleicht eins in der " +
                        "normalen Form oder im  Komparativ oder verwendet?";
            default: return "Es tut mir leid aber ich kann dir keinen weiteren Hinweis geben.";
        }
    }

    private String timeToString(String lTime) {
        switch (lTime){
            case "PAST": return "Vergangenheit";
            case "PRES": return "Gegenwart";
            case "PPART": return "Partizip II";
            case "PROG": return "Verlaufsform der Gegenwart";
            default: return "Fehler";
        }
    }
}
