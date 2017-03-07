package com.example.kathrin1.vokabeltrainer_newlayout.objects;

public class VocObject {

    private int id;
    private String voc;
    private String translation;
    private String status;
    private String book;
    private String chapter;
    private String pos;
    private String lemma;
    private String sentences;
    private int tested;

    public VocObject(int id, String voc, String lemma, String translation, String status, String book, String chapter, String pos,
                     String sentences, int tested) {
        this.id = id;
        this.voc = voc;
        this.lemma = lemma;
        this.translation = translation;
        this.status = status;
        this.book = book;
        this.chapter = chapter;
        this.pos = pos;
        this.sentences = sentences;
        this.tested = tested;
    }

    public int getId() {
        return id;
    }

    public String getVoc() {
        return voc;
    }

    public String getLemma() { return lemma; }

    public String getTranslation() { return translation; }

    public String getStatus() { return status; }

    public String getBook() {
        return book;
    }

    public String getChapter() {
        return chapter;
    }

    public String getPOS() {
        return pos;
    }

    public String getSentences() {
        return sentences;
    }

    public int getTested() { return tested; }

}
