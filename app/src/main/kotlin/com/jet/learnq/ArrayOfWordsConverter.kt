package com.jet.learnq

import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.PairDTO

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

    private fun isValid(str: String): Boolean {
        var dashes = 0
        for (char in str) {
            if (dashes > 1 || char == '\"') return false
            if (char == '-') {
                dashes++
            }
        }
        return dashes == 1
    }

    fun getWordDTOsFromStringArray(
        stringArray: List<String>
    ): List<PairDTO> {
        val pairs = kotlin.collections.ArrayList<PairDTO>()
        for (str in stringArray) {
            if (isValid(str)) {
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
}