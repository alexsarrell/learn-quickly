package com.jet.learnq

object Pairs {
    private var sortedWords: ArrayList<String>? = null
    private var sortedTranslations: ArrayList<String>? = null

    fun setWords(words: ArrayList<String>) {
        sortedWords = words
    }

    fun setTranslations(translations: ArrayList<String>) {
        sortedTranslations = translations
    }

    fun getWords(): ArrayList<String> {
        return if (sortedWords != null) sortedWords!!
        else ArrayList()
    }

    fun getTranslations(): ArrayList<String> {
        return if (sortedTranslations != null) sortedTranslations!!
        else ArrayList()
    }
}