package com.jet.learnq

import android.content.SharedPreferences
import com.jet.learnq.controller.SQLiteDatabaseController
import com.jet.learnq.model.PairDTO
import kotlin.concurrent.thread

class CoroutineRecord {
    fun addAllPairs(
        pairs: List<PairDTO>, preferences: SharedPreferences,
        databaseController: SQLiteDatabaseController
    ) {
        thread {
            for (pairDTO in pairs) {
                for (string in pairDTO.translations) {
                    Dictionary.addANewPair(pairDTO.word, string, preferences, databaseController)
                }
            }
        }
    }
}