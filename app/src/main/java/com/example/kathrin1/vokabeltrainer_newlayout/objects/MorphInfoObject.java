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
    private String infoLemma = "";
    private String lingCase = "";
    private String gender = "";

    private String[] posList = {"A", "Adv", "Comp", "Conj", "Det", "G", "I", "N", "NVC", "Part",
            "Punct", "Pron", "Prep", "PropN", "V", "VVC"};
    private String[] caseList = {"acc", "nom", "GEN", "nomacc"};
    private String[] timeList = {"PAST", "PRES", "PPART", "PROG"};
    private String[] genderList = {"masc", "fem", "neut", "reffem", "refmasc"};
    private String[] numberList = {"1sg", "2sg", "3sg", "1pl", "2pl", "3pl", "pl", "SG", "2nd",
            "3rd", "ref1sg", "ref2sg", "ref3sg", "ref1pl", "ref2pl", "ref3pl"};

    private Boolean isWeakVerb = false;
    private Boolean isStrongVerb = false;
    private Boolean isInfinitive = false;
    private Boolean isContractions = false;
    private Boolean isNegation = false;
    private Boolean isPassive = false;
    private Boolean isIndaux = false;
    private Boolean isComparative = false;
    private Boolean isSuperlative = false;
    private Boolean isReflexive = false;
    private Boolean isTO = false;
    private Boolean isWh = false;
    private Boolean isCONTR = false;

    /**
     *
     * @param reading
     */
    public MorphInfoObject(String reading){

        String[] mReading_withLemma = reading.split("#");
        lemma = mReading_withLemma[1].trim();

        String[] mReading = mReading_withLemma[0].trim().split("\\s");;
        switch (mReading[0]){
            case "Punct":
                setPOSTAG(mReading);
                break;
            case "Prep":
                setPOSTAG(mReading);
                break;
            case "Conj":
                setPOSTAG(mReading);
                break;
            case "Comp":
                setPOSTAG(mReading);
                break;
            case "G":
                setPOSTAG(mReading);
                break;
            case "I":
                setPOSTAG(mReading);
                break;
            case "Part":
                setPOSTAG(mReading);
                break;
            case "Adv":
                setAdverbTag(mReading);
                break;
            case "A":
                setAdjective(mReading);
                break;
            case "VVC":
                setVVCTag(mReading);
                break;
            case "PropN":
                setProperNounTag(mReading);
                break;
            case "N":
                setNounTag(mReading);
                break;
            case "Det":
                setDeterminerTag(mReading);
                break;
            case "NVC":
                setNVCTag(mReading);
                break;
            case "V":
                setVerbTag(mReading);
                break;
            case "Pron":
                setPronounTag(mReading);
                break;
        }
    }

    // Adv {'wh'}
    private void setAdverbTag(String[] mReading){
        setPOSTAG(mReading);
        setWhTag(mReading);
    }

    // A {'SUPER', 'COMP'}
    private void setAdjective(String[] mReading){
        if (Arrays.asList(mReading).contains("COMP")){
            isComparative = true;
        }
        else if (Arrays.asList(mReading).contains("SUPER")){
            isSuperlative = true;
        }
    }

    // VVC {'PRES', 'INF'}
    private void setVVCTag(String[] mReading){
        setPOSTAG(mReading);
        setTimeTag(mReading);
        setInfinitiveTag(mReading);
    }

    // PropN {'3sg', '3pl', 'GEN'}
    private void setProperNounTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
    }

    // N {'3sg', 'masc', '3pl', 'GEN'}
    private void setNounTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
        setGenderTag(mReading);
    }

    // Det {'ref2sg', 'ref3pl', 'ref2nd', 'ref1sg', 'ref3sg', 'ref1pl', 'refmasc', 'wh', 'reffem', 'GEN'}
    private void setDeterminerTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
    }

    // NVC {'1sg', '3sg', 'masc', 'fem', 'neut', 'STR', 'wh', '3pl', 'PAST', 'PRES', '1pl', '2nd'}
    private void setNVCTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setCaseTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
        setTimeTag(mReading);
        setStrongVerbTag(mReading);
    }

    // V {'1sg', 'INDAUX', '3sg', 'NEG', 'INF', 'PROG', 'TO', 'STR', 'pl', '3pl', 'PAST', 'PRES', 'WK', 'PPART', 'PASSIVE', 'CONTR', '2sg'}
    private void setVerbTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setINDAUXTag(mReading);
        setNegationTag(mReading);
        setInfinitiveTag(mReading);
        setTimeTag(mReading);
        setTOTag(mReading);
        setStrongVerbTag(mReading);
        setWeakVerbTag(mReading);
        setPassiveTag(mReading);
        setCONTRTag(mReading);
    }

    // Pron {'ref2sg', '3sg', 'masc', 'ref3pl', '2pl', '3rd', '1sg', 'fem', 'reffem', 'refmasc', 'neut', 'wh', '3pl', 'nomacc', 'ref1sg', '1pl', 'ref3sg', 'GEN', 'ref1pl', 'ref2nd', 'NEG', 'nom', 'acc', 'refl', '2nd', '2sg'}
    private void setPronounTag(String[] mReading){
        setPOSTAG(mReading);
        setNumberTag(mReading);
        setGenderTag(mReading);
        setWhTag(mReading);
        setCaseTag(mReading);
        setNegationTag(mReading);
        setReflexiveTag(mReading);
    }


    private void setPOSTAG(String[] mReading){
        pos = mReading[0];
    }

    private void setWhTag(String[] mReading){
        if (Arrays.asList(mReading).contains("wh")){
            isWh = true;
        }
    }

    private void setInfinitiveTag(String[] mReading){
        if (Arrays.asList(mReading).contains("INF")){
            isInfinitive = true;
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
            isIndaux = true;
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

    private void setCONTRTag(String[] mReading){
        if (Arrays.asList(mReading).contains("CONTR")){
            isCONTR = true;
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
        number = intersection(Arrays.asList(mReading), Arrays.asList(timeList));
    }

    private void setCaseTag(String[] mReading){
        lingCase = intersection(Arrays.asList(mReading), Arrays.asList(timeList));
    }

    private void setGenderTag(String[] mReading){
        gender = intersection(Arrays.asList(mReading), Arrays.asList(timeList));
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
    public String getInfoLemma() { return infoLemma; }
    public String getLingCase() { return lingCase; }
    public String getGender() { return gender; }
    public Boolean getIsWeakVerb() { return isWeakVerb; }
    public Boolean getIsStrongVerb() { return isStrongVerb; }
    public Boolean getIsInfinitive() { return isInfinitive; }
    public Boolean getIsContractions() { return isContractions; }
    public Boolean getIsNegation() { return isNegation; }
    public Boolean getIsPassive() { return isPassive; }
    public Boolean getIsIndaux() { return isIndaux; }
    public Boolean getIsComparative() { return isComparative; };
    public Boolean getIsSuperlative() { return isSuperlative; };
    public Boolean getIsReflexive() { return isReflexive; };
    public Boolean getIsTO() { return isTO; };
    public Boolean getIsWh() { return isWh; };
    public Boolean getIsCONTR() { return isCONTR; };
}
