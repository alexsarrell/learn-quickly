package com.jet.learnq.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.learnq1.R;
import com.jet.learnq.ArrayOfWordsConverter;
import com.jet.learnq.CoroutineRecord;
import com.jet.learnq.Dictionary;
import com.jet.learnq.MainActivity;
import com.jet.learnq.model.PairDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OptionsActivity extends AppCompatActivity {
    TextView textViewLanguage1;
    TextView textViewLanguage2;
    ImageButton buttonLanguage1;
    ImageButton buttonLanguage2;
    ImageButton arrayButton;
    ImageButton changeThemeButton;
    EditText pasteArrayEditText;
    SharedPreferences sharedPreferences;
    ArrayOfWordsConverter converter;
    float x1, x2, y1, y2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        converter = new ArrayOfWordsConverter();

        sharedPreferences = getApplicationContext().getSharedPreferences("current_theme", MODE_PRIVATE);
        textViewLanguage1 = findViewById(R.id.options_activity_text_view_language1);
        textViewLanguage2 = findViewById(R.id.options_activity_text_view_language2);
        SharedPreferences preferences = getSharedPreferences("Properties", MODE_PRIVATE);

        textViewLanguage1.setText(preferences.getString("default_language_on", "Choose a language"));
        textViewLanguage2.setText(preferences.getString("default_language_to", "Choose a language"));

        buttonLanguage1 = findViewById(R.id.options_activity_language1);
        buttonLanguage2 = findViewById(R.id.options_activity_language2);
        changeThemeButton = findViewById(R.id.options_activity_change_theme_button);
        arrayButton = findViewById(R.id.options_activity_add_an_array_button);

        pasteArrayEditText = findViewById(R.id.options_activity_editText_array);
        arrayButton.setOnClickListener(click -> {
            String str = pasteArrayEditText.getText().toString();
            List<PairDTO> pairs = converter.getWordDTOsFromStringArray(
                    Arrays.stream(str.split("\n")).collect(Collectors.toList()));
            CoroutineRecord coroutineRecord = new CoroutineRecord();
            coroutineRecord.addAllPairs(pairs, new Dictionary(new SQLiteDatabaseController(this),
                    getApplicationContext()));
            pasteArrayEditText.getText().clear();
        });
        buttonLanguage1.setOnClickListener(view -> {
            Intent i = new Intent(OptionsActivity.this, com.jet.learnq.SearchActivity.class);
            i.putExtra("language", false);
            startActivity(i);
        });
        buttonLanguage2.setOnClickListener(view -> {
            Intent i = new Intent(OptionsActivity.this, com.jet.learnq.SearchActivity.class);
            i.putExtra("language", true);
            startActivity(i);
        });
        changeThemeButton.setOnClickListener(view -> {
            if (("Light").equals(sharedPreferences.getString("current_theme", "Light"))) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("current_theme", "Dark");
                editor.apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("current_theme", "Light");
                editor.apply();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent touch) {
        switch (touch.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touch.getX();
                y1 = touch.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touch.getX();
                y2 = touch.getY();
                if (y1 - y2 > ((float) (getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 10)) {
                    Intent i = new Intent(OptionsActivity.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_on_bottom, R.anim.slide_out_bottom);
                }
                break;
        }
        return false;
    }
}
