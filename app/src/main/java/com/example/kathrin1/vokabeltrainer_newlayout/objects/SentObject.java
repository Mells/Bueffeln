package com.example.kathrin1.vokabeltrainer_newlayout.objects;

/**
 * Created by kathrin1 on 30.01.17.
 */

public class SentObject {

    private int id;
    private String book;
    private String chapter;
    private String sentence;
    private String tagged;
    private String lemma;

    public SentObject(int id, String book, String chapter, String sentence, String tagged, String lemma) {

        this.id = id;
        this.book = book;
        this.chapter = chapter;
        this.sentence = sentence;
        this.tagged = tagged;
        this.lemma = lemma;
    }

    public int getId() {
        return id;
    }

    public String getBook() {
        return book;
    }

    public String getChapter() {
        return chapter;
    }

    public String getSentence() { return sentence; }

    public String getTagged() { return tagged; }

    public String getLemma() { return lemma; }

}
