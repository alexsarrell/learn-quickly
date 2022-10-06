package com.jet.learnq

import android.content.Context
import com.example.learnq1.R

class StringsValidator(private val context: Context) {
    fun catchForbiddenString(str: String): Boolean {
        if (str.length > 256) return false
        for (ch in str) {
            if (context.getString(R.string.forbidden_symbols).contains(ch)) {
                return false
            }
        }
        return true
    }
}