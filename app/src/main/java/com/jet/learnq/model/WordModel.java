package com.jet.learnq.model;

import java.util.List;

public class WordModel implements Entity {
    private final Integer word_id;
    private final Integer language_id;
    private final String word;
    private List<WordModel> translations;

    public WordModel(Integer word_id, Integer language_id, String word, List<WordModel> translations) {
        this.word_id = word_id;
        this.language_id = language_id;
        this.word = word;
        this.translations = translations;
    }

    public String getWord() {
        return word;
    }

    public List<WordModel> getTranslations() {
        return translations;
    }

    public void setTranslations(List<WordModel> translations) {
        this.translations = translations;
    }

    public void addTranslation(WordModel translation) {
        translations.add(translation);
    }

    public Integer getWord_id() {
        return word_id;
    }

    public Integer getLanguage_id() {
        return language_id;
    }

    public String getName() {
        return word;
    }
}
