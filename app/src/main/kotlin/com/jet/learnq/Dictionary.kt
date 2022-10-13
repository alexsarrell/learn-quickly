package com.jet.learnq

import android.content.SharedPreferences
import com.jet.learnq.controller.SQLiteDatabaseController
import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.LanguageModel
import com.jet.learnq.model.WordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*
import java.util.stream.Collectors

object Dictionary {
    private var dictionary: MutableList<WordModel> = ArrayList()
    private var reversedDictionary: MutableList<WordModel> = ArrayList()
    private var wordDTOS: MutableList<WordDTO> = ArrayList()
    private var reversedWordDTOS: MutableList<WordDTO> = ArrayList()
    private var languageModels: MutableList<LanguageModel> = ArrayList()
    private var pairs: MutableList<MutableList<WordModel>> = ArrayList()
    private var changed = false

    suspend fun getSortedStringPairs(
        preferences: SharedPreferences,
        database: SQLiteDatabaseController
    )
            : ArrayList<ArrayList<String>> {
        reloadDictionary(preferences, database)
        val dif = CoroutineScope(Dispatchers.Default).async {
            var sortedWords = ArrayList<String>()
            var sortedTranslations = ArrayList<String>()
            val sortedStringPairs = ArrayList<ArrayList<String>>()
            fillPairs(wordDTOS, sortedWords)
            fillPairs(reversedWordDTOS, sortedTranslations)
            sortedWords = sortedWords.stream().sorted().collect(Collectors.toList()) as ArrayList<String>
            sortedTranslations = sortedTranslations.stream().sorted().collect(Collectors.toList()) as ArrayList<String>
            sortedStringPairs.add(sortedWords)
            sortedStringPairs.add(sortedTranslations)
            return@async sortedStringPairs
        }
        return dif.await()
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
        languageTo: String,
        preferences: SharedPreferences,
        database: SQLiteDatabaseController
    ) {
        database.deleteFromTable(toDeleteDTO, languageOn, languageTo)
        reloadDictionary(preferences, database)
    }

    fun getDictionary(
        preferences: SharedPreferences,
        database: SQLiteDatabaseController
    ): List<WordDTO> {
        if (changed) reloadDictionary(preferences, database)
        return wordDTOS
    }

    fun getReversedDictionary(
        preferences: SharedPreferences,
        database: SQLiteDatabaseController
    ): List<WordDTO> {
        if (changed) reloadDictionary(preferences, database)
        return reversedWordDTOS
    }

    fun getLanguageModels(database: SQLiteDatabaseController): List<LanguageModel> {
        if (languageModels.isEmpty()) {
            languageModels = database.allLanguages
            return languageModels
        }
        return languageModels
    }

    fun addANewPair(
        word: String, translation: String,
        preferences: SharedPreferences, database: SQLiteDatabaseController
    ) {
        database.addOnePair(
            WordModel(
                1,
                database.getLanguageId(
                    preferences.getString(
                        "default_language_on", "Error"
                    )
                ), word, ArrayList()
            ),
            WordModel(
                1,
                database.getLanguageId(
                    preferences.getString(
                        "default_language_to", "Error"
                    )
                ), translation, ArrayList()
            )
        )
        changed = true
    }

    private fun reloadDictionary(preferences: SharedPreferences, database: SQLiteDatabaseController) {
        changed = false
        wordDTOS.clear()
        val languageOn = preferences.getString("default_language_on", "English")
        val languageTo = preferences.getString("default_language_to", "English")
        reversedWordDTOS.clear()
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