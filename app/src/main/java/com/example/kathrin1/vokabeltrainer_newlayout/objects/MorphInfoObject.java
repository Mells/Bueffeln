package com.example.kathrin1.vokabeltrainer_newlayout.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kathrin1 on 16.06.17.
 */

public class MorphInfoObject {

    private String lemma = "";
    private String pos = "";
    private String number = "";
    private String lingTime = "";
    private String lingCase = "";
    private String gender = "";
    private String reading;

    private String[] posList = {"A", "Adv", "Comp", "Conj", "Det", "G", "I", "N", "NVC", "Part",
            "Punct", "Pron", "Prep", "PropN", "V", "VVC"};
    private String[] caseList = {"acc", "nom", "GEN", "nomacc"};
    private String[] timeList = {"PAST", "PRES", "PPART", "PROG", "INF"};
    private String[] genderList = {"masc", "fem", "neut", "reffem", "refmasc"};
    private String[] numberList = {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
            "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};

    private Boolean isWeakVerb = false;
    private Boolean isStrongVerb = false;
    private Boolean isContraction = false;
    private Boolean isNegation = false;
    private Boolean isPassive = false;
    private Boolean isIndicativeAuxiliary = false;
    private Boolean isComparative = false;
    private Boolean isSuperlative = false;
    private Boolean isReflexive = false;
    private Boolean isTO = false;
    private Boolean isWh = false;

    /**
     *
     * @param mReading
     */
    public MorphInfoObject(String mReading){

        //String[] mReading_withLemma = readingInformation.split("#");
        //lemma = mReading_withLemma[1].trim();

        //String[] mReading = mReading_withLemma[0].trim().split("\\s");

        String[] readingWithLemma;
        if (mReading.contains("#")) {
            readingWithLemma = mReading.split("#");
            // print("MorpInfo: lemmaSplit", reading_with_lemma)
            lemma = readingWithLemma[1].trim();
        }
        else {
            readingWithLemma = new String[]{mReading};
            // print("MorpInfo: NoLemmaSplit", reading_with_lemma)
        }
        reading = readingWithLemma[0];
        // self.word_reading = reading_with_lemma[0]
        String[] readingInformation = readingWithLemma[0].trim().split(" ");

        switch (readingInformation[0]){
            case "Punct":
                setPOSTag(readingInformation);
                break;
            case "Prep":
                setPOSTag(readingInformation);
                break;
            case "Conj":
                setPOSTag(readingInformation);
                break;
            case "Comp":
                setPOSTag(readingInformation);
                break;
            case "G":
                setPOSTag(readingInformation);
                break;
            case "I":
                setPOSTag(readingInformation);
                break;
            case "Part":
                setPOSTag(readingInformation);
                break;
            case "Adv":
                setAdverbTag(readingInformation);
                break;
            case "A":
                setAdjectiveTag(readingInformation);
                break;
            case "VVC":
                setVerbVerbCombinationTag(readingInformation);
                break;
            case "PropN":
                setProperNounTag(readingInformation);
                break;
            case "N":
                setNounTag(readingInformation);
                break;
            case "Det":
                setDeterminerTag(readingInformation);
                break;
            case "NVC":
                setNounVerbCombinationTag(readingInformation);
                break;
            case "V":
                setVerbTag(readingInformation);
                break;
            case "Pron":
                setPronounTag(readingInformation);
                break;
        }
    }

    // Adv {'wh'}
    private void setAdverbTag(String[] mReading){
        setPOSTag(mReading);
        setWhTag(mReading);
    }

    // A {'SUPER', 'COMP'}
    private void setAdjectiveTag(String[] mReading){
        setPOSTag(mReading);
        if (Arrays.asList(mReading).contains("COMP")){
            isComparative = true;
        }
        else if (Arrays.asList(mReading).contains("SUPER")){
            isSuperlative = true;
        }
    }

    // VVC {'PRES', 'INF'}
    private void setVerbVerbCombinationTag(String[] mReading){
        setPOSTag(mReading);
        setTimeTag(mReading);
    }

    // PropN {'3sg', '3pl', 'GEN'}
    private void setProperNounTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
    }

    // N {'3sg', 'masc', '3pl', 'GEN'}
    private void setNounTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
        setGenderTag(mReading);
    }

    // Det {'ref2sg', 'ref3pl', 'ref2nd', 'ref1sg', 'ref3sg', 'ref1pl', 'refmasc', 'wh', 'reffem', 'GEN'}
    private void setDeterminerTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
    }

    // NVC {'1sg', '3sg', 'masc', 'fem', 'neut', 'STR', 'wh', '3pl', 'PAST', 'PRES', '1pl', '2nd'}
    private void setNounVerbCombinationTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        // setCaseTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
        setTimeTag(mReading);
        setStrongVerbTag(mReading);
    }

    // V {'1sg', 'INDAUX', '3sg', 'NEG', 'INF', 'PROG', 'TO', 'STR', 'pl', '3pl', 'PAST', 'PRES', 'WK', 'PPART', 'PASSIVE', 'CONTR', '2sg'}
    private void setVerbTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        setINDAUXTag(mReading);
        setNegationTag(mReading);
        setTimeTag(mReading);
        setTOTag(mReading);
        setStrongVerbTag(mReading);
        setWeakVerbTag(mReading);
        setPassiveTag(mReading);
        setContractionTag(mReading);
    }

    // Pron {'ref2sg', '3sg', 'masc', 'ref3pl', '2pl', '3rd', '1sg', 'fem', 'reffem', 'refmasc', 'neut', 'wh', '3pl', 'nomacc', 'ref1sg', '1pl', 'ref3sg', 'GEN', 'ref1pl', 'ref2nd', 'NEG', 'nom', 'acc', 'refl', '2nd', '2sg'}
    private void setPronounTag(String[] mReading){
        setPOSTag(mReading);
        setNumberTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
        setCaseTag(mReading);
        setNegationTag(mReading);
        setReflexiveTag(mReading);
    }


    private void setPOSTag(String[] mReading){
        pos = mReading[0];
    }

    private void setWhTag(String[] mReading){
        if (Arrays.asList(mReading).contains("wh")){
            isWh = true;
        }
    }

    private void setStrongVerbTag(String[] mReading){
        if (Arrays.asList(mReading).contains("STR")){
            isStrongVerb = true;
        }
    }

    private void setWeakVerbTag(String[] mReading){
        if (Arrays.asList(mReading).contains("WK")){
            isWeakVerb = true;
        }
    }

    private void setINDAUXTag(String[] mReading){
        if (Arrays.asList(mReading).contains("INDAUX")){
            isIndicativeAuxiliary = true;
        }
    }

    private void setNegationTag(String[] mReading){
        if (Arrays.asList(mReading).contains("NEG")){
            isNegation = true;
        }
    }

    private void setTOTag(String[] mReading){
        if (Arrays.asList(mReading).contains("TO")){
            isTO = true;
        }
    }

    private void setPassiveTag(String[] mReading){
        if (Arrays.asList(mReading).contains("PASSIVE")){
            isPassive = true;
        }
    }

    private void setContractionTag(String[] mReading){
        if (Arrays.asList(mReading).contains("CONTR")){
            isContraction = true;
        }
    }

    private void setReflexiveTag(String[] mReading){
        if (Arrays.asList(mReading).contains("refl")){
            isReflexive = true;
        }
    }

    private void setTimeTag(String[] mReading){

        lingTime = intersection(Arrays.asList(mReading), Arrays.asList(timeList));
    }

    private void setNumberTag(String[] mReading){
        number = intersection(Arrays.asList(mReading), Arrays.asList(numberList));
    }

    private void setCaseTag(String[] mReading){
        lingCase = intersection(Arrays.asList(mReading), Arrays.asList(caseList));
    }

    private void setGenderTag(String[] mReading){
        gender = intersection(Arrays.asList(mReading), Arrays.asList(genderList));
    }

    private String intersection(List list1, List list2) {
        ArrayList<String> list = new ArrayList<String>();
        for (Object t : list1) {
            if(list2.contains(t)) {
                list.add((String) t);
            }
        }
        if (list.size() == 1){
            return list.get(0);
        }
        else {
            return "";
        }
    }

    public String getLemma() { return lemma; }
    public String getPOS() { return pos; }
    public String getNumber() { return number; }
    public String getLingTime() { return lingTime; }
    public String getLingCase() { return lingCase; }
    public String getGender() { return gender; }
    public Boolean getIsWeakVerb() { return isWeakVerb; }
    public Boolean getIsStrongVerb() { return isStrongVerb; }
    public Boolean getIsContractions() { return isContraction; }
    public Boolean getIsNegation() { return isNegation; }
    public Boolean getIsPassive() { return isPassive; }
    public Boolean getIsIndicativeAuxiliary() { return isIndicativeAuxiliary; }
    public Boolean getIsComparative() { return isComparative; };
    public Boolean getIsSuperlative() { return isSuperlative; };
    public Boolean getIsReflexive() { return isReflexive; };
    public Boolean getIsTO() { return isTO; };
    public Boolean getIsWh() { return isWh; };

    @Override
    public String toString()
    {
        return reading;
    }
}
