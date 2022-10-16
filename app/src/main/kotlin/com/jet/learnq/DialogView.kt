package com.jet.learnq

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.example.learnq1.R
import com.jet.learnq.dto.WordDTO

class DialogView(
    context: Context,
    private var activity: SearchActivity
) : Dialog(context) {
    lateinit var cansel: Button

    lateinit var delete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.approve_dialog_box)
        cansel = findViewById(R.id.warning_box_cansel_button)
        delete = findViewById(R.id.warning_box_delete_button)
        cansel.setOnClickListener { dismiss() }
        setCancelable(false)
    }

    fun onDeleteClick(wordDTO: WordDTO, languageOn: String?, languageTo: String?, dictionary: Dictionary) {
        delete.setOnClickListener {
            if (languageOn != null && languageTo != null) {
                dictionary.deleteFromDatabase(
                    wordDTO, languageOn, languageTo
                )
            }
            activity.update()
            dismiss()
        }
    }
}