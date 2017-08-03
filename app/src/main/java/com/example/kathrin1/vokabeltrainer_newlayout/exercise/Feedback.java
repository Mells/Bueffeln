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

public class Feedback {

    private String learner_vocable;
    private String learner_pos;
    private String learner_lemma;


    // BOOK Tags: a, av, d, i, irr, k, p, ph, pr, s, v
    // xtag: A, Adv, Comp, Conj, Det, G, I, N, NVC, Part, Punct, Pron, Prep, PropN, V, VVC


    private Map<String, List> postags = new HashMap<>();

    private String app_vocable;
    private String app_pos;
    private String app_lemma;
    private String app_soundex;

    private Boolean isEnglishWord;

    private DatabaseManager dbManager;

    private String reading1, reading2, reading3, reading4, reading5, reading6, reading7, reading8;
    private String[] allReadings;

    public Feedback(String vocableByLearner, String voc_vocable, String pos, String lemma, Boolean isEnglishWord, Context c){
        this.learner_vocable = vocableByLearner;
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
        //look up tag and lemma of word

        if (isEnglishWord) {
            // test purpose
            // Vokabel; pro_voc;    tag_proc_voc;       soundex;    lemma;  tag_lemma_voc;  simple_tagged_lemma_vocable;
            // can;     ['can'];    [('can', 'MD')];    ['C50'];    ['can'];[('can', 'MD')];[('can', 'MD')]
            app_vocable = "can";
            app_pos = "MD";
            app_lemma = "can";
            app_soundex = "C50";

            learner_vocable = "";

            // 1 Word
            if (learner_vocable.split("\\s").length == 1) {
                MorphObject morph = dbManager.getMorphInformation(learner_vocable);

                // no word could be retrieved
                if (morph.isEmpty()) {

                    String learner_soundex = generateSoundex(learner_vocable);
                    int levenshtein_soundex = levenshteinDistance(learner_soundex, app_soundex);
                    int levenshtein_spelling = levenshteinDistance(learner_vocable, app_vocable);

                    // is the soundx the same?
                    if (levenshtein_soundex == 0) {
                        return "Du hast kein bekanntes Wort eingegeben, " +
                                "aber die Lösung klingt gleich wie deine Eingabe";
                    }

                    // smaller value is closer to the solution
                    if (levenshtein_soundex == levenshtein_spelling) {
                        return "Du hast kein bekanntes Wort eingegeben. " +
                                "Damit können wir leider nicht arbeiten.";
                    } else if (levenshtein_soundex < levenshtein_spelling) {
                        // todo threshold
                        return "Du hast kein bekanntes Wort eingegeben, aber die Lösung klingt " +
                                "ähnlich. Bitte überprüfe deine Rechtschreibung";
                    } else {
                        // todo threshold
                        return "Du hast kein bekanntes Wort eingegeben, " +
                                "bitte überprüfe deine Rechtschreibung.";
                    }
                }
                // word could be retrieved
                else {
                    //todo there could be more than 1 meaning
                    String[] allReadings = morph.getAllReadings();
                    // (well;well)   N 3sg#well , Adv#well , V INF;
                    // (welled;well)    V PAST WK#well , V PPART WK


                    MorphInfoObject mio = new MorphInfoObject(morph.getReading1());
                    learner_pos = mio.getPOS();
                    // does it have the same lemma?
                    String learner_lemma = morph.getLemma();
                    if (learner_lemma.equals(app_lemma)) {   // the lemma is the same
                        // todo not finished
                        return comparePOSTags(mio, app_pos);
                    }
                    else {
                        String category_pos_learner ="";
                        String category_pos_app ="";
                        for (Map.Entry<String, List> entry : postags.entrySet()) {
                            if (entry.getValue().equals(learner_pos)) {
                                category_pos_learner = entry.getKey();
                            }
                        }
                        for (Map.Entry<String, List> entry : postags.entrySet()) {
                            if (entry.getValue().equals(app_pos)) {
                                category_pos_app = entry.getKey();
                            }
                        }
                        if (category_pos_app.equals(category_pos_learner)) {
                            return comparePOSTags(mio, app_pos);
                        }
                        else {
                            return "Du hast ein " + category_pos_learner + " eingegeben, du brauchst aber " +
                                    "ein " + category_pos_app + ".";
                        }
                    }
                }
            }
        }
        return null;
    }

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
                // ;sings;sing;N 3pl#sing;V 3sg PRES;
                if (!mio.getLemma().equals("be") || !mio.getLemma().equals("have")){
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
                    return "Du hast eine Form von 'have' oder 'be' benutzt hier hier brauchst du " +
                            "aber eine anderes Verb";
                }

            case "VVZ":     // verb, 3rd person sing. present
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
