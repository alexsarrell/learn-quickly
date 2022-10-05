package com.jet.learnq;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.example.learnq1.R;
import com.jet.learnq.controller.SearchActivity;
import com.jet.learnq.dto.WordDTO;
import com.jet.learnq.model.Dictionary;

public class DialogView extends Dialog {
    Button cansel;
    Button delete;
    Dictionary dictionary;
    SearchActivity activity;

    public DialogView(@NonNull Context context, Dictionary dictionary, SearchActivity activity) {
        super(context);
        this.dictionary = dictionary;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.approve_dialog_box);
        cansel = findViewById(R.id.warning_box_cansel_button);
        delete = findViewById(R.id.warning_box_delete_button);
        cansel.setOnClickListener(click -> dismiss());
        setCancelable(false);
    }

    public void onDeleteClick(WordDTO wordDTO, String languageOn, String languageTo) {
        delete.setOnClickListener(onDelete -> {
            dictionary.deleteFromDatabase(wordDTO, languageOn, languageTo);
            activity.update();
            dismiss();
        });
    }
}
