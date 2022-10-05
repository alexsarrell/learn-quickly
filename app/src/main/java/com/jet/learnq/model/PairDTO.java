package com.jet.learnq.model;

import java.util.List;

public class PairDTO {
    String word;
    List<String> translations;

    public PairDTO(String word, List<String> translations) {
        this.word = word;
        this.translations = translations;
    }

    public void addTranslation(String tr) {
        translations.add(tr);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }
}
