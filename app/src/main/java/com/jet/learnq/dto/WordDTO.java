package com.jet.learnq.dto;

import com.jet.learnq.model.WordModel;

import java.util.List;

public class WordDTO {
    private List<WordModel> translations;
    private String name;

    public WordDTO(List<WordModel> translations, String name) {
        this.translations = translations;
        this.name = name;
    }

    public List<WordModel> getTranslations() {
        return translations;
    }

    public String getName() {
        return name;
    }
}
