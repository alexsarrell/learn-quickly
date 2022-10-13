package com.jet.learnq.dto;

import com.jet.learnq.model.Translationizable;
import com.jet.learnq.model.WordModel;

import java.util.List;

public class WordDTO implements Translationizable {
    private final List<WordModel> translations;
    private final String name;
    private final String languageName;

    public WordDTO(List<WordModel> translations, String name, String languageName) {
        this.translations = translations;
        this.languageName = languageName;
        this.name = name;
    }

    @Override
    public String getLanguageName() {
        return languageName;
    }

    public List<WordModel> getTranslations() {
        return translations;
    }

    public String getName() {
        return name;
    }
}
