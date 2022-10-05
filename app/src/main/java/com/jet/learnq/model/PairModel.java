package com.jet.learnq.model;

import java.util.List;

public class PairModel implements Entity {
    WordModel word;
    List<WordModel> translations;

    public void addTranslation(WordModel tr) {
        translations.add(tr);
    }

    public WordModel getWord() {
        return word;
    }

    public void setWord(WordModel word) {
        this.word = word;
    }

    public String getName() {
        return word.getName();
    }

    public List<WordModel> getTranslations() {
        return translations;
    }

    public void setTranslations(List<WordModel> translations) {
        this.translations = translations;
    }
}
