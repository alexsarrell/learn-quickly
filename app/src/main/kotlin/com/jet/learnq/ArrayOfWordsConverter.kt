package com.jet.learnq

import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.PairDTO
import java.util.*

class ArrayOfWordsConverter {
    fun getDTOsFromString(text: String, pairs: List<WordDTO>): WordDTO {
        val sb = convertStringToWordDTO(text)
        var wordToDelete: WordDTO = pairs[0]
        for (wd in pairs) {
            if (wd.name == sb.toString()) {
                wordToDelete = wd
                break
            }
        }
        return wordToDelete
    }

    fun getWordDTOsFromStringArray(
        stringArray: List<String>
    ): List<PairDTO> {
        val pairs = kotlin.collections.ArrayList<PairDTO>()
        for (str in stringArray) {
            val sb = StringBuilder()
            val translations = kotlin.collections.ArrayList<String>()
            for (i in str.indices) {
                if (str[i] != '-') {
                    sb.append(str[i])
                } else {
                    var transBuilder = StringBuilder()
                    for (j in i + 1 until str.length) {
                        if (str[j] != ',' && j != str.length - 1) {
                            transBuilder.append(str[j])
                        } else {
                            if (j == str.length - 1) {
                                transBuilder.append(str[j])
                            }
                            translations.add(transBuilder.toString().trim())
                            transBuilder = StringBuilder()
                        }
                    }
                    break
                }
            }
            pairs.add(PairDTO(sb.toString().trim(), translations))
        }
        return pairs
    }

    private fun convertStringToWordDTO(text: String): StringBuilder {
        val sb = StringBuilder()
        val iterator: Iterator<Int> = text.chars().iterator()
        while (iterator.hasNext()) {
            val ch: Char = iterator.next().toChar()
            if (ch != '-') {
                sb.append(ch.code.toChar())
            } else {
                sb.deleteCharAt(sb.length - 1)
                break
            }
        }
        return sb
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
                val stringBuilder = java.lang.StringBuilder(
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
}