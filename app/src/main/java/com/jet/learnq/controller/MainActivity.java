package com.jet.learnq.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.learnq1.R;
import com.jet.learnq.StringsValidator;
import com.jet.learnq.model.Dictionary;

public class MainActivity extends AppCompatActivity {
    Dictionary dictionary;
    SharedPreferences preferences;
    SharedPreferences searchPreferences;
    ImageButton addTheNewCouple;
    EditText editTextWord;
    EditText editTextTranslation;
    TextView currentLanguages;
    SQLiteDatabaseController dictionaryController;
    Intent search;
    Intent options;
    String currentLanguageOn;
    String currentLanguageTo;

    float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = new Intent(MainActivity.this, SearchActivity.class);
        options = new Intent(MainActivity.this, OptionsActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("current_theme", MODE_PRIVATE);
        //getApplicationContext().deleteDatabase("dictionaries.db");
        if (("Light").equals(sharedPreferences.getString("current_theme", "Light"))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        init();
        currentLanguages = findViewById(R.id.activity_main_current_languages_textview);
        updateCurrentLanguages();
        addTextViewOnClickListener(currentLanguages);
        setOnAddClickListener(addTheNewCouple);
    }

    private void setOnAddClickListener(View button) {
        button.setOnClickListener(view -> {
            StringsValidator sv = new StringsValidator(getApplicationContext());
            Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_rotate);
            button.startAnimation(rotation);
            String word = editTextWord.getText().toString();
            String translation = editTextTranslation.getText().toString();
            if (!preferences.getString("default_language_on", "empty").equals("empty")
                    && !preferences.getString("default_language_to", "empty").equals("empty")) {
                if (!word.isEmpty() && !translation.isEmpty()) {
                    if (!word.replaceAll("\\s", "").isEmpty()
                            && !translation.replaceAll("\\s", "").isEmpty()) {
                        if (sv.catchForbiddenString(word) && sv.catchForbiddenString(translation)) {
                            dictionary.addANewPair(word, translation);
                            Toast.makeText(getApplicationContext(), "The pair is successfully saved",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "You used a forbidden symbol or the word is too long",
                                    Toast.LENGTH_LONG).show();
                        }
                        editTextWord.setText("");
                        editTextTranslation.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "You can't save a whitespace word",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You can't save an empty word",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Choose languages at first",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCurrentLanguages() {
        StringBuilder sb = new StringBuilder();
        currentLanguageOn = preferences.getString("default_language_on", "Choose a language");
        currentLanguageTo = preferences.getString("default_language_to", "Choose a language");
        sb.append(currentLanguageOn);
        sb.append(" | ");
        sb.append(currentLanguageTo);
        currentLanguages.setText(sb);
    }

    private void mirrorLanguages() {
        String language = preferences.getString("default_language_on", "Choose a language");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("default_language_on",
                preferences.getString("default_language_to", "Choose a language"));
        editor.putString("default_language_to", language);
        editor.apply();
    }

    private void addTextViewOnClickListener(TextView textView) {

        textView.setOnClickListener(view -> {
            if (currentLanguageOn.equals("Choose a language")
                    && currentLanguageTo.equals("Choose a language")) {
                options.putExtra("lanOrPairs", true);
                startActivity(options);
            } else {
                mirrorLanguages();
                updateCurrentLanguages();
            }
        });
    }

    private void init() {
        preferences = getSharedPreferences("Properties", MODE_PRIVATE);
        searchPreferences = getSharedPreferences("Pairs_properties", MODE_PRIVATE);
        SharedPreferences.Editor editor = searchPreferences.edit();
        editor.putString("search_state", "original");
        editor.apply();
        addTheNewCouple = findViewById(R.id.main_activity_newCoupleButton);

        editTextWord = findViewById(R.id.main_activity_editText_word);
        editTextTranslation = findViewById(R.id.main_activity_editText_translation);
        dictionaryController = new SQLiteDatabaseController(MainActivity.this);
        dictionary = new Dictionary(dictionaryController, MainActivity.this);
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
                if (y2 - y1
                        > ((float) getApplicationContext().getResources().getDisplayMetrics().heightPixels / 10)
                        && Math.abs(x1 - x2)
                        < ((float) getApplicationContext().getResources().getDisplayMetrics().heightPixels / 10)) {
                    startActivity(options);
                    overridePendingTransition(R.anim.slide_on_top, R.anim.slide_out_top);
                }
                if (x2 - x1 > ((float) getApplicationContext().getResources().getDisplayMetrics().widthPixels / 10)
                        && Math.abs(y1 - y2)
                        < ((float) getApplicationContext().getResources().getDisplayMetrics().heightPixels / 10)) {
                    search.putExtra("lanOrPairs", true);
                    startActivity(search);
                    overridePendingTransition(R.anim.slide_on_left, R.anim.slide_out_left);
                }
                break;
        }
        return false;
    }
}