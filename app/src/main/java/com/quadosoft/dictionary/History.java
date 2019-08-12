package com.quadosoft.dictionary;

public class History {
    public History() {

    }

    public String getEn_word() {
        return en_word;
    }

    public void setEn_word(String en_word) {
        this.en_word = en_word;
    }

    private String en_word;

    public History(String word) {
        en_word = word;
    }
}
