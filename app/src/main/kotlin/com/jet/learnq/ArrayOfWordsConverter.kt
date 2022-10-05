package com.jet.learnq

import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.Dictionary

class ArrayOfWordsConverter() {
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

    fun addAnArrayOfFords(array: List<String>, dictionary: Dictionary) {
        val dtoArray: List<WordDTO> = ArrayList()
        for (string in array) {
            getDTOsFromString(string, dtoArray)
        }
    }
}