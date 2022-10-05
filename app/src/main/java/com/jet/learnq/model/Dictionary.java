package com.jet.learnq.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.jet.learnq.controller.SQLiteDatabaseController;
import com.jet.learnq.dto.WordDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dictionary implements Serializable {
    List<WordModel> dictionary;
    List<WordModel> reversedDictionary;

    List<WordDTO> wordDTOS;
    List<WordDTO> reversedWordDTOS;

    SQLiteDatabaseController database;
    List<LanguageModel> languageModels;
    List<List<WordModel>> pairs;
    SharedPreferences preferences;
    boolean changed;

    public Dictionary(SQLiteDatabaseController database, Context context) {
        dictionary = new ArrayList<>();
        reversedDictionary = new ArrayList<>();
        wordDTOS = new ArrayList<>();
        reversedWordDTOS = new ArrayList<>();
        this.database = database;
        languageModels = database.getAllLanguages();
        preferences = context.getSharedPreferences("Properties", Context.MODE_PRIVATE);
        reloadDictionary();
    }

    public boolean editSet(List<WordModel> changed) {
        return true;
    }

    public void deleteFromDatabase(WordDTO toDeleteDTO,
                                   String languageOn,
                                   String languageTo) {

        database.deleteFromTable(toDeleteDTO, languageOn, languageTo);
        reloadDictionary();
    }

    public List<WordDTO> getDictionary() {
        if (changed) reloadDictionary();
        return wordDTOS;
    }

    public List<WordDTO> getReversedDictionary() {
        if (changed) reloadDictionary();
        return reversedWordDTOS;
    }

    public List<LanguageModel> getLanguageModels() {
        return languageModels;
    }

    public void addANewPair(String word, String translation) {
        database.addOnePair(
                new WordModel(
                        1,
                        database.getLanguageId(
                                preferences.getString(
                                        "default_language_on", "Error")), word, new ArrayList<>()),
                new WordModel(
                        1,
                        database.getLanguageId(
                                preferences.getString(
                                        "default_language_to", "Error")), translation, new ArrayList<>()));
        changed = true;
    }

    private void reloadDictionary() {
        changed = false;
        wordDTOS.clear();
        reversedWordDTOS.clear();
        pairs = database.getAllPairs(
                preferences.getString("default_language_on", "English"),
                preferences.getString("default_language_to", "English"));
        dictionary = pairs.get(0);
        Log.println(Log.INFO, "update", "dic size " + dictionary.size());
        reversedDictionary = pairs.get(1);
        Log.println(Log.INFO, "update", "r dic size " + reversedDictionary.size());

        for (WordModel wm : dictionary) {
            wordDTOS.add(new WordDTO(wm.getTranslations(), wm.getName()));
        }
        for (WordModel wm : reversedDictionary) {
            reversedWordDTOS.add(new WordDTO(wm.getTranslations(), wm.getName()));
        }
    }
}
