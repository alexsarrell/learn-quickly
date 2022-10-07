package com.jet.learnq

import android.content.Context
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

class Dictionary(private val database: SQLiteDatabaseController, context: Context) {
    private var dictionary: MutableList<WordModel> = ArrayList()
    private var reversedDictionary: MutableList<WordModel> = ArrayList()
    private var wordDTOS: MutableList<WordDTO> = ArrayList()
    private var reversedWordDTOS: MutableList<WordDTO> = ArrayList()
    private var languageModels: MutableList<LanguageModel> = database.allLanguages
    private var pairs: MutableList<MutableList<WordModel>> = ArrayList()
    private var preferences: SharedPreferences
    private var changed = false

    init {
        preferences = context.getSharedPreferences("Properties", Context.MODE_PRIVATE)
        reloadDictionary()
    }

    suspend fun getSortedStringPairs(): ArrayList<ArrayList<String>> {
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

    private fun reloadDictionary() {
        changed = false
        wordDTOS.clear()
        reversedWordDTOS.clear()
        pairs = database.getAllPairs(
            preferences.getString("default_language_on", "English"),
            preferences.getString("default_language_to", "English")
        )
        dictionary = pairs[0]
        reversedDictionary = pairs[1]
        for (wm in dictionary) {
            wordDTOS.add(WordDTO(wm.translations, wm.name))
        }
        for (wm in reversedDictionary) {
            reversedWordDTOS.add(WordDTO(wm.translations, wm.name))
        }
    }
}