package com.jet.learnq.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learnq1.R;
import com.jet.learnq.ArrayOfWordsConverter;
import com.jet.learnq.DialogView;
import com.jet.learnq.ScrollViewBuilder;
import com.jet.learnq.dto.WordDTO;
import com.jet.learnq.model.Dictionary;
import com.jet.learnq.model.LanguageModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {
    SharedPreferences preferences;
    boolean firstOrSecondLanguage;
    ScrollViewBuilder scrollViewBuilder;
    EditText searchEditText;
    boolean isDictionary;
    LinearLayout languagesList;
    Bundle extra;
    Dictionary dictionary;
    List<String> sortedWords;
    List<String> sortedTranslations;
    SharedPreferences searchPreferences;
    TextView currentLanguagesTextView;
    Typeface typeface;
    List<WordDTO> pairs;
    ArrayOfWordsConverter converter;
    List<WordDTO> pairsTranslations;
    float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEditText = findViewById(R.id.search_language_search_edit_text);
        languagesList = findViewById(R.id.search_language_scroll_layout);
        currentLanguagesTextView = findViewById(R.id.activity_search_current_languages_textview);
        addTextViewOnClickListener(currentLanguagesTextView);
        typeface = getResources().getFont(R.font.open_sans_medium_italic);
        extra = getIntent().getExtras();
        preferences = getSharedPreferences("Properties", MODE_PRIVATE);
        scrollViewBuilder = new ScrollViewBuilder(getApplicationContext());
        dictionary = new Dictionary(
                new SQLiteDatabaseController(SearchActivity.this), SearchActivity.this);

        if (extra.getBoolean("lanOrPairs")) { //if true catch pairs if false catch languages
            dictionaryWindow();
        } else {
            languagesWindow();
        }

    }

    private void updateCurrentLanguages() {
        StringBuilder sb = new StringBuilder();
        sb.append(preferences.getString("default_language_on", "Choose a language"));
        sb.append(" | ");
        sb.append(preferences.getString("default_language_to", "Choose a language"));
        currentLanguagesTextView.setText(sb);
    }

    private void dictionaryWindow() {
        isDictionary = true; //dictionary
        updateCurrentLanguages();
        getDictionaries();
        if (sortedWords != null && !sortedWords.isEmpty()) {
            //addTouchListener(sortedWords);
            typeface = getResources().getFont(R.font.open_sans_medium_italic);
            addTouchListener(sortedWords);
            fillItemsInList(sortedWords, languagesList, typeface);
            converter = new ArrayOfWordsConverter();
        }
    }

    private void languagesWindow() {
        isDictionary = false; //languages
        firstOrSecondLanguage = extra.getBoolean("language", false);

        List<LanguageModel> languageModels = dictionary.getLanguageModels();
        languageModels = languageModels.stream()
                .sorted(Comparator.comparing(LanguageModel::getName))
                .collect(Collectors.toList());
        List<String> display = new ArrayList<>();
        for (LanguageModel lm : languageModels) {
            display.add(lm.getName());
        }
        addTouchListener(display);
        typeface = getResources().getFont(R.font.open_sans_medium_italic);

        fillItemsInList(display, languagesList, typeface);
    }

    private void getDictionaries() {

        pairs = dictionary.getDictionary();
        pairsTranslations = dictionary.getReversedDictionary();


        if (!pairs.isEmpty() && !pairsTranslations.isEmpty()) {
            sortedWords = new ArrayList<>();
            sortedTranslations = new ArrayList<>();

            fillPairs(pairs, sortedWords);
            fillPairs(pairsTranslations, sortedTranslations);
            sortedWords = sortedWords.stream().sorted().collect(Collectors.toList());
            sortedTranslations = sortedTranslations.stream().sorted().collect(Collectors.toList());
        } else {
            languagesList.removeAllViews();
        }
    }

    private void fillPairs(List<WordDTO> items, List<String> stringItem) {
        stringItem.clear();
        for (WordDTO wd : items) {
            boolean contains = false;
            for (String s : stringItem) {
                contains = s.toLowerCase().startsWith(wd.getName().toLowerCase());
                if (contains) {
                    break;
                }
            }
            if (!contains) {
                StringBuilder stringBuilder = new StringBuilder(wd.getName() + " - " +
                        wd.getTranslations().get(0).getName());
                if (wd.getTranslations().size() > 1) {
                    for (int i = 1; i < wd.getTranslations().size(); i++) {
                        stringBuilder.append(", ").append(wd.getTranslations().get(i).getName());
                    }
                }
                stringItem.add(stringBuilder.toString());
            }
        }
    }

    private <T extends String> void addTouchListener(List<T> models) {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String writtenText = searchEditText.getText().toString();
                List<T> filteredNames = models.stream()
                        .filter(model ->
                                model.startsWith(writtenText) ||
                                        model.toLowerCase().startsWith(writtenText))
                        .collect(Collectors.toList());
                Typeface typeface = getResources().getFont(R.font.open_sans_medium_italic);
                languagesList.removeAllViews();
                if (filteredNames.size() > 0) {
                    fillItemsInList(filteredNames, languagesList, typeface);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void update() {
        if (extra.getBoolean("lanOrPairs")) { //if true catch pairs if false catch languages
            sortedWords.clear();
            sortedTranslations.clear();
            dictionaryWindow();
        }
    }

    private <T extends String> void fillItemsInList(List<T> languageModels, LinearLayout languagesList, Typeface font) {

        languagesList.removeAllViews();
        char firstLetter = languageModels.get(0).charAt(0);

        languagesList.addView(scrollViewBuilder.setFirstLetter(firstLetter, font, getColor(R.color.text_labels_color)));
        scrollViewBuilder.strokeDiv(languagesList, (R.drawable.ic_line_divider),
                0, 0, 200, -40);
        for (T lm : languageModels) {

            if (firstLetter != lm.charAt(0)) {
                firstLetter = lm.charAt(0);
                scrollViewBuilder.strokeDiv(languagesList, (R.drawable.small_divider));
                languagesList.addView(scrollViewBuilder.setFirstLetter(firstLetter, font,
                        getColor(R.color.text_labels_color)));

                scrollViewBuilder.strokeDiv(languagesList, (R.drawable.ic_line_divider),
                        0, 0, 200, -40);
            }
            TextView item = new TextView(getApplicationContext());
            if (extra.getBoolean("lanOrPairs")) {
                setOnLongClickListener(item);
            }
            scrollViewBuilder.setTextOptions(item,
                    lm,
                    font,
                    17,
                    getColor(R.color.icons_text_color),
                    getDrawable(R.drawable.item_cell));
            item.setGravity(Gravity.CENTER_VERTICAL);
            if (!isDictionary) {
                item.setOnClickListener(view -> {
                    String chosenLanguage = item.getText().toString().trim();
                    SharedPreferences.Editor editor = preferences.edit();
                    if (!firstOrSecondLanguage) editor.putString("default_language_on", chosenLanguage);
                    else editor.putString("default_language_to", chosenLanguage);
                    editor.apply();
                    Intent i = new Intent(SearchActivity.this, OptionsActivity.class);
                    startActivity(i);
                });
            }
            languagesList.addView(item);
        }
    }

    private void setOnLongClickListener(TextView item) {
        item.setOnLongClickListener(listener -> {
            DialogView onLongTouchBox = new DialogView(SearchActivity.this, dictionary, SearchActivity.this);
            onLongTouchBox.show();
            onLongTouchBox.onDeleteClick(converter.getDTOsFromString(item.getText().toString().trim(), pairs),
                    preferences.getString("default_language_on", "Error"),
                    preferences.getString("default_language_to", "Error"));
            Log.println(Log.INFO, "message", "what word " +
                    item.getText());
            return false;
        });
    }

    /*private WordDTO getDTOsFromString(String text, List<WordDTO> pairs){
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> iterator = text.chars().iterator();
        while(iterator.hasNext()){
            char ch = (char)(int)iterator.next();
            if(ch != '-'){
                sb.append((char)(int)ch);
            }
            else{
                sb.deleteCharAt(sb.length() - 1);
                break;
            }
        }
        Log.println(Log.INFO, "message", "what word " +
                sb);
        WordDTO wordToDelete = pairs.get(0);
        for(WordDTO wd : pairs){
            if(wd.getName().equals(sb.toString())){
                wordToDelete = wd;
                break;
            }
        }
        return wordToDelete;
    }*/
    private void addTextViewOnClickListener(TextView textView) {
        textView.setOnClickListener(view -> {
            mirrorPairs();
            updateCurrentLanguages();
        });
    }

    private void mirrorPairs() {
        SharedPreferences.Editor editor = preferences.edit();
        String languageOn = preferences.getString("default_language_on", "Error");
        editor.putString("default_language_on", preferences.getString("default_language_to", "Error"));
        editor.putString("default_language_to", languageOn);
        editor.apply();
        searchPreferences = getSharedPreferences("Pairs_properties", MODE_PRIVATE);
        editor = searchPreferences.edit();
        if (searchPreferences.getString("search_state", "Error").equals("original")) {
            getDictionaries();
            if (sortedTranslations != null && !sortedTranslations.isEmpty()) {
                fillItemsInList(sortedTranslations, languagesList, typeface);
            }
            editor.putString("search_state", "translation");
            updateCurrentLanguages();
        } else if (searchPreferences.getString("search_state", "Error").equals("translation")) {
            getDictionaries();
            if (sortedWords != null && !sortedWords.isEmpty()) {
                fillItemsInList(sortedWords, languagesList, typeface);
            }
            editor.putString("search_state", "original");
            updateCurrentLanguages();
        }
        editor.apply();
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (extra.getBoolean("lanOrPairs")) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = motionEvent.getX();
                    y1 = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = motionEvent.getX();
                    y2 = motionEvent.getY();
                    if (x1 - x2
                            > ((float) getApplicationContext().getResources().getDisplayMetrics().widthPixels / 10)
                            && Math.abs(y1 - y2)
                            < ((float) getApplicationContext().getResources().getDisplayMetrics().heightPixels / 10)) {
                        Intent i = new Intent(SearchActivity.this, MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_on_right, R.anim.slide_out_right);
                    }
                    if (x2 - x1
                            > ((float) getApplicationContext().getResources().getDisplayMetrics().widthPixels / 10)
                            && Math.abs(y1 - y2)
                            < ((float) getApplicationContext().getResources().getDisplayMetrics().heightPixels / 20)) {
                        mirrorPairs();
                    }
                    break;
            }

        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
