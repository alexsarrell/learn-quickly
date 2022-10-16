package com.jet.learnq

import com.jet.learnq.model.PairDTO
import kotlin.concurrent.thread

class CoroutineRecord {
    fun addAllPairs(
        pairs: List<PairDTO>,
        dictionary: Dictionary
    ) {
        thread {
            for (pairDTO in pairs) {
                for (string in pairDTO.translations) {
                    dictionary.addANewPair(pairDTO.word, string)
                }
            }
        }
    }
}