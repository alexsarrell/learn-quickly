package com.jet.learnq.model;

public class LanguageModel implements Entity {
    private final Integer language_id;
    private final String language;

    public LanguageModel(Integer language_id, String language) {
        this.language_id = language_id;
        this.language = language;
    }

    public Integer getLanguage_id() {
        return language_id;
    }

    public String getName() {
        return language;
    }
}
