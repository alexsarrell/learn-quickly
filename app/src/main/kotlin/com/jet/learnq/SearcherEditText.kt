package com.jet.learnq

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet

class SearcherEditText : androidx.appcompat.widget.AppCompatEditText {
    private var watchers: MutableList<TextWatcher>? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun addTextChangedListener(watcher: TextWatcher) {
        super.addTextChangedListener(watcher)
        if (watchers == null) {
            watchers = ArrayList()
        } else {
            watchers!!.add(watcher)
        }
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        if (watchers!!.isNotEmpty()) {
            for (textWatcher in watchers!!) {
                super.removeTextChangedListener(textWatcher)
            }
        }
    }
}