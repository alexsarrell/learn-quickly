package com.jet.learnq

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.example.learnq1.R
import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.Dictionary

class DialogView(
    context: Context, var dictionary: Dictionary,
    private var activity: com.jet.learnq.SearchActivity
) : Dialog(context) {
    @JvmField
    var cansel: Button = findViewById(R.id.warning_box_cansel_button)

    @JvmField
    var delete: Button = findViewById(R.id.warning_box_delete_button)

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.approve_dialog_box)

        cansel.setOnClickListener { dismiss() }
        setCancelable(false)
    }

    fun onDeleteClick(wordDTO: WordDTO?, languageOn: String?, languageTo: String?) {
        delete.setOnClickListener {
            dictionary.deleteFromDatabase(wordDTO, languageOn, languageTo)
            activity.update()
            dismiss()
        }
    }
}