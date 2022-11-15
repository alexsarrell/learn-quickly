package com.jet.learnq

import android.content.Context
import android.content.SharedPreferences
import com.jet.learnq.controller.SQLiteDatabaseController
import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.LanguageModel
import com.jet.learnq.model.WordModel
import java.util.*

class Dictionary(private val database: SQLiteDatabaseController, context: Context) {
    private var dictionary: MutableList<WordModel> = ArrayList()
    private var reversedDictionary: MutableList<WordModel> = ArrayList()
    private var wordDTOS: MutableList<WordDTO> = ArrayList()
    private var reversedWordDTOS: MutableList<WordDTO> = ArrayList()
    private var languageModels: MutableList<LanguageModel> = ArrayList()
    private var pairs: MutableList<MutableList<WordModel>> = ArrayList()
    private var preferences: SharedPreferences
    private var changed = true

    init {
        preferences = context.getSharedPreferences("Properties", Context.MODE_PRIVATE)
        languageModels = database.allLanguages // TODO сделать его обратно синглтоном
    }

    fun fillPairs(items: List<WordDTO>, stringItem: MutableList<String>) {
        stringItem.clear()
        for (wd: WordDTO in items) {
            var contains = false
            for (s: String in stringItem) {
                contains = s.lowercase(Locale.getDefault()).startsWith(wd.name.lowercase(Locale.getDefault()))
                if (contains) {
                    break
                }
            }
            if (!contains) {
                val stringBuilder = StringBuilder(
                    wd.name + " - " +
                        wd.translations[0].name
                )
                if (wd.translations.size > 1) {
                    for (i in 1 until wd.translations.size) {
                        stringBuilder.append(", ").append(wd.translations[i].name)
                    }
                }
                stringItem.add(stringBuilder.toString())
            }
        }
    }

    fun deleteFromDatabase(
        toDeleteDTO: WordDTO,
        languageOn: String,
        languageTo: String
    ) {
        database.deleteFromTable(toDeleteDTO, languageOn, languageTo)
        reloadDictionary()
    }

    fun getDictionary(): List<WordDTO> {
        if (changed) reloadDictionary()
        return wordDTOS
    }

    fun getReversedDictionary(): List<WordDTO> {
        if (changed) reloadDictionary()
        return reversedWordDTOS
    }

    fun getLanguageModels(): List<LanguageModel> {
        return languageModels
    }

    fun addANewPair(word: String, translation: String) {
        database.addOnePair(
            WordModel(
                1,
                database.getLanguageId(
                    preferences.getString(
                        "default_language_on",
                        "Error"
                    )
                ),
                word,
                ArrayList()
            ),
            WordModel(
                1,
                database.getLanguageId(
                    preferences.getString(
                        "default_language_to",
                        "Error"
                    )
                ),
                translation,
                ArrayList()
            )
        )
    }

    private fun reloadDictionary() {
        changed = false
        wordDTOS.clear()
        reversedWordDTOS.clear()
        val languageOn = preferences.getString("default_language_on", "English")
        val languageTo = preferences.getString("default_language_to", "English")
        pairs = database.getAllPairs(
            languageOn,
            languageTo
        )
        dictionary = pairs[0]
        reversedDictionary = pairs[1]
        for (wm in dictionary) {
            wordDTOS.add(WordDTO(wm.translations, wm.name, languageOn))
        }
        for (wm in reversedDictionary) {
            reversedWordDTOS.add(WordDTO(wm.translations, wm.name, languageTo))
        }
    }
}
