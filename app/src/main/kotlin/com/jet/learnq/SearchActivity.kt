package com.jet.learnq

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learnq1.R
import com.jet.learnq.controller.MainActivity
import com.jet.learnq.controller.OptionsActivity
import com.jet.learnq.controller.SQLiteDatabaseController
import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.Dictionary
import com.jet.learnq.model.LanguageModel
import java.util.*
import java.util.stream.Collectors
import kotlin.math.abs

class SearchActivity : AppCompatActivity() {
    private var preferences: SharedPreferences = getSharedPreferences("Properties", MODE_PRIVATE)
    private var firstOrSecondLanguage = false
    private var scrollViewBuilder: ScrollViewBuilder = ScrollViewBuilder(applicationContext)
    private var searchEditText: EditText = findViewById(R.id.search_language_search_edit_text)
    private var isDictionary = false
    private var languagesList: LinearLayout = findViewById(R.id.search_language_scroll_layout)
    private var extra: Bundle? = intent.extras
    private var dictionary = Dictionary(
        SQLiteDatabaseController(this@SearchActivity), this@SearchActivity
    )
    private var sortedWords: MutableList<String> = ArrayList()
    private var sortedTranslations: MutableList<String> = ArrayList()
    private lateinit var searchPreferences: SharedPreferences
    private var currentLanguagesTextView: TextView = findViewById(R.id.activity_search_current_languages_textview)
    private var typeface: Typeface
    private var pairs: List<WordDTO> = ArrayList()
    private lateinit var converter: ArrayOfWordsConverter
    private var pairsTranslations: List<WordDTO> = ArrayList()
    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f

    init {
        typeface = resources.getFont(R.font.open_sans_medium_italic)
        addTextViewOnClickListener(currentLanguagesTextView)
        scrollViewBuilder = ScrollViewBuilder(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (extra?.getBoolean("lanOrPairs") == true) { //if true catch pairs if false catch languages
            dictionaryWindow()
        } else {
            languagesWindow()
        }
    }

    private fun updateCurrentLanguages() {
        val sb = StringBuilder()
        sb.append(preferences.getString("default_language_on", "Choose a language"))
        sb.append(" | ")
        sb.append(preferences.getString("default_language_to", "Choose a language"))
        currentLanguagesTextView.text = sb
    }

    private fun dictionaryWindow() {
        isDictionary = true //dictionary
        updateCurrentLanguages()
        dictionaries
        if (sortedWords.isNotEmpty()) {
            //addTouchListener(sortedWords);
            typeface = resources.getFont(R.font.open_sans_medium_italic)
            addTouchListener(sortedWords)
            fillItemsInList(sortedWords, languagesList, typeface)
            converter = ArrayOfWordsConverter()
        }
    }

    private fun languagesWindow() {
        isDictionary = false //languages
        firstOrSecondLanguage = extra!!.getBoolean("language", false)
        var languageModels = dictionary.languageModels
        languageModels = languageModels.stream()
            .sorted(Comparator.comparing { obj: LanguageModel -> obj.name })
            .collect(Collectors.toList())
        val display: MutableList<String> = ArrayList()
        for (lm in languageModels) {
            display.add(lm.name)
        }
        addTouchListener(display)
        typeface = resources.getFont(R.font.open_sans_medium_italic)
        fillItemsInList(display, languagesList, typeface)
    }

    private val dictionaries: Unit
        get() {
            pairs = dictionary.dictionary
            pairsTranslations = dictionary.reversedDictionary
            if (pairs.isNotEmpty() && pairsTranslations.isNotEmpty()) {
                sortedWords = ArrayList()
                sortedTranslations = ArrayList()
                fillPairs(pairs, sortedWords)
                fillPairs(pairsTranslations, sortedTranslations)
                sortedWords = sortedWords.stream().sorted().collect(Collectors.toList())
                sortedTranslations = sortedTranslations.stream().sorted().collect(Collectors.toList())
            } else {
                languagesList.removeAllViews()
            }
        }

    private fun fillPairs(items: List<WordDTO>, stringItem: MutableList<String>) {
        stringItem.clear()
        for (wd in items) {
            var contains = false
            for (s in stringItem) {
                contains = s.lowercase(Locale.getDefault()).startsWith(wd.name.lowercase(Locale.getDefault()))
                if (contains) {
                    break
                }
            }
            if (!contains) {
                val stringBuilder = StringBuilder(
                    wd.name + " - " +
                            wd.translations[0].name
                )
                if (wd.translations.size > 1) {
                    for (i in 1 until wd.translations.size) {
                        stringBuilder.append(", ").append(wd.translations[i].name)
                    }
                }
                stringItem.add(stringBuilder.toString())
            }
        }
    }

    private fun <T : String?> addTouchListener(models: List<T>) {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val writtenText = searchEditText.text.toString()
                val filteredNames = models.stream()
                    .filter { model: T ->
                        model!!.startsWith(writtenText) ||
                                model.lowercase(Locale.getDefault()).startsWith(writtenText)
                    }
                    .collect(Collectors.toList())
                val typeface = resources.getFont(R.font.open_sans_medium_italic)
                languagesList.removeAllViews()
                if (filteredNames.size > 0) {
                    fillItemsInList(filteredNames, languagesList, typeface)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun update() {
        if (extra!!.getBoolean("lanOrPairs")) { //if true catch pairs if false catch languages
            sortedWords.clear()
            sortedTranslations.clear()
            dictionaryWindow()
        }
    }

    private fun <T : String?> fillItemsInList(languageModels: List<T>, languagesList: LinearLayout?, font: Typeface?) {
        languagesList!!.removeAllViews()
        var firstLetter = languageModels[0]!![0]
        languagesList.addView(
            scrollViewBuilder.setFirstLetter(
                firstLetter,
                font,
                getColor(R.color.text_labels_color)
            )
        )
        scrollViewBuilder.strokeDiv(
            languagesList, R.drawable.ic_line_divider,
            0, 0, 200, -40
        )
        for (lm in languageModels) {
            if (firstLetter != lm!![0]) {
                firstLetter = lm[0]
                scrollViewBuilder.strokeDiv(languagesList, R.drawable.small_divider)
                languagesList.addView(
                    scrollViewBuilder.setFirstLetter(
                        firstLetter, font,
                        getColor(R.color.text_labels_color)
                    )
                )
                scrollViewBuilder.strokeDiv(
                    languagesList, R.drawable.ic_line_divider,
                    0, 0, 200, -40
                )
            }
            val item = TextView(applicationContext)
            if (extra!!.getBoolean("lanOrPairs")) {
                setOnLongClickListener(item)
            }
            scrollViewBuilder.setTextOptions(
                item,
                lm,
                font,
                17,
                getColor(R.color.icons_text_color),
                getDrawable(R.drawable.item_cell)
            )
            item.gravity = Gravity.CENTER_VERTICAL
            if (!isDictionary) {
                item.setOnClickListener {
                    val chosenLanguage = item.text.toString().trim { it <= ' ' }
                    val editor = preferences.edit()
                    if (!firstOrSecondLanguage) editor.putString(
                        "default_language_on",
                        chosenLanguage
                    ) else editor.putString("default_language_to", chosenLanguage)
                    editor.apply()
                    val i = Intent(this@SearchActivity, OptionsActivity::class.java)
                    startActivity(i)
                }
            }
            languagesList.addView(item)
        }
    }

    private fun setOnLongClickListener(item: TextView) {
        item.setOnLongClickListener {
            val onLongTouchBox = DialogView(this@SearchActivity, dictionary, this@SearchActivity)
            onLongTouchBox.show()
            onLongTouchBox.onDeleteClick(
                converter.getDTOsFromString(item.text.toString().trim { it <= ' ' }, pairs),
                preferences.getString("default_language_on", "Error"),
                preferences.getString("default_language_to", "Error")
            )
            Log.println(
                Log.INFO, "message", "what word " +
                        item.text
            )
            false
        }
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
    private fun addTextViewOnClickListener(textView: TextView?) {
        textView!!.setOnClickListener {
            mirrorPairs()
            updateCurrentLanguages()
        }
    }

    private fun mirrorPairs() {
        var editor = preferences.edit()
        val languageOn = preferences.getString("default_language_on", "Error")
        editor.putString("default_language_on", preferences.getString("default_language_to", "Error"))
        editor.putString("default_language_to", languageOn)
        editor.apply()
        searchPreferences = getSharedPreferences("Pairs_properties", MODE_PRIVATE)
        editor = searchPreferences.edit()
        if (searchPreferences.getString("search_state", "Error") == "original") {
            dictionaries
            if (sortedTranslations.isNotEmpty()) {
                fillItemsInList(sortedTranslations, languagesList, typeface)
            }
            editor.putString("search_state", "translation")
            updateCurrentLanguages()
        } else if (searchPreferences.getString("search_state", "Error") == "translation") {
            dictionaries
            if (sortedWords.isNotEmpty()) {
                fillItemsInList(sortedWords, languagesList, typeface)
            }
            editor.putString("search_state", "original")
            updateCurrentLanguages()
        }
        editor.apply()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (extra!!.getBoolean("lanOrPairs")) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = motionEvent.x
                    y1 = motionEvent.y
                }
                MotionEvent.ACTION_UP -> {
                    x2 = motionEvent.x
                    y2 = motionEvent.y
                    if (x1 - x2 > applicationContext.resources.displayMetrics.widthPixels.toFloat() / 10
                        && abs(y1 - y2) < applicationContext.resources.displayMetrics.heightPixels.toFloat() / 10
                    ) {
                        val i = Intent(this@SearchActivity, MainActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(R.anim.slide_on_right, R.anim.slide_out_right)
                    }
                    if (x2 - x1 > applicationContext.resources.displayMetrics.widthPixels.toFloat() / 10
                        && abs(y1 - y2) < applicationContext.resources.displayMetrics.heightPixels.toFloat() / 20
                    ) {
                        mirrorPairs()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }
}