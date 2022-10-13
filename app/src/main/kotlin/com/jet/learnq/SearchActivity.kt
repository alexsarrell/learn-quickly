package com.jet.learnq

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learnq1.R
import com.jet.learnq.controller.OptionsActivity
import com.jet.learnq.controller.SQLiteDatabaseController
import com.jet.learnq.dto.WordDTO
import com.jet.learnq.model.LanguageModel
import kotlinx.android.synthetic.main.approve_dialog_box.*
import java.util.stream.Collectors
import kotlin.math.abs


class SearchActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private var firstOrSecondLanguage = false
    private lateinit var scrollViewBuilder: ScrollViewBuilder
    private lateinit var searchEditText: SearcherEditText
    private var isDictionary = false
    private lateinit var languagesList: LinearLayout
    private var extra: Bundle? = null
    private var sortedWords: MutableList<String> = ArrayList()
    private var sortedTranslations: MutableList<String> = ArrayList()
    private lateinit var searchPreferences: SharedPreferences
    private lateinit var currentLanguagesTextView: TextView
    private lateinit var typeface: Typeface
    private var pairs: List<WordDTO> = ArrayList()
    private lateinit var converter: ArrayOfWordsConverter
    private var pairsTranslations: List<WordDTO> = ArrayList()
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchEditText = findViewById(R.id.search_language_search_edit_text)
        languagesList = findViewById(R.id.search_language_scroll_layout)
        currentLanguagesTextView = findViewById(R.id.activity_search_current_languages_textview)
        addTextViewOnClickListener(currentLanguagesTextView)
        typeface = resources.getFont(R.font.open_sans_medium_italic)
        extra = intent.extras
        preferences = getSharedPreferences("Properties", MODE_PRIVATE)
        scrollViewBuilder = ScrollViewBuilder(applicationContext)
        if (extra!!.getBoolean("lanOrPairs")) { //if true catch pairs if false catch languages
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
        if (extra!!.getStringArrayList("sorted_words") != null
            && extra!!.getStringArrayList("sorted_translations") != null
        ) {
            sortedWords = extra!!.getStringArrayList("sorted_words")!!
            sortedTranslations = extra!!.getStringArrayList("sorted_translations")!!

            pairs = Dictionary.getDictionary(preferences, SQLiteDatabaseController(this))
            pairsTranslations = Dictionary.getReversedDictionary(preferences, SQLiteDatabaseController(this))
        } else {
            dictionaries()
            if (sortedWords.isEmpty()) {
                if (linearLayout != null) linearLayout.removeAllViews()
            }
        }
        updateCurrentLanguages()
        if (sortedWords.isNotEmpty()) {
            typeface = resources.getFont(R.font.open_sans_medium_italic)
            addSearchListener(sortedWords)
            fillItemsInList(sortedWords, languagesList, typeface)
            converter = ArrayOfWordsConverter()
        }
    }

    private fun languagesWindow() {
        isDictionary = false //languages
        firstOrSecondLanguage = extra!!.getBoolean("language", false)
        var languageModels = Dictionary.getLanguageModels(SQLiteDatabaseController(this))
        languageModels = languageModels.stream()
            .sorted(Comparator.comparing { obj: LanguageModel -> obj.name })
            .collect(Collectors.toList())
        val display: MutableList<String> = ArrayList()
        for (lm: LanguageModel in languageModels) {
            display.add(lm.name)
        }
        addSearchListener(display)
        typeface = resources.getFont(R.font.open_sans_medium_italic)
        fillItemsInList(display, languagesList, typeface)
    }

    private fun dictionaries() {
        pairs = Dictionary.getDictionary(preferences, SQLiteDatabaseController(this))
        pairsTranslations = Dictionary.getReversedDictionary(preferences, SQLiteDatabaseController(this))

        if (pairs.isNotEmpty() && pairsTranslations.isNotEmpty()) {
            sortedWords = ArrayList()
            sortedTranslations = ArrayList()
            Dictionary.fillPairs(pairs, sortedWords)
            Dictionary.fillPairs(pairsTranslations, sortedTranslations)
            sortedWords = sortedWords.stream().sorted().collect(Collectors.toList())
            sortedTranslations = sortedTranslations.stream().sorted().collect(Collectors.toList())
        } else {
            languagesList.removeAllViews()
        }
    }

    private fun addSearchListener(names: List<String>) {
        searchEditText.removeTextChangedListener(null)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //TODO сделай валидацию по текущему языку, можно передавать в дто язык элемента и проверять по нему
                val writtenText = searchEditText.text.toString()
                val filteredNames = names.stream()
                    .filter { model: String ->
                        model.lowercase().startsWith(writtenText.lowercase())
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

    private fun fillItemsInList(itemsText: List<String>, linearLayout: LinearLayout, font: Typeface) {
        linearLayout.removeAllViews()
        var firstLetter = itemsText[0][0]
        linearLayout.addView(
            scrollViewBuilder.setFirstLetter(
                firstLetter,
                font,
                getColor(R.color.text_labels_color)
            )
        )
        scrollViewBuilder.strokeDiv(
            linearLayout, (R.drawable.ic_line_divider),
            0, 0, 200, -40
        )
        for (lm: String in itemsText) {
            if (firstLetter != lm[0]) {
                firstLetter = lm[0]
                scrollViewBuilder.strokeDiv(linearLayout, (R.drawable.small_divider))
                linearLayout.addView(
                    scrollViewBuilder.setFirstLetter(
                        firstLetter, font,
                        getColor(R.color.text_labels_color)
                    )
                )
                scrollViewBuilder.strokeDiv(
                    linearLayout, (R.drawable.ic_line_divider),
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
                    val chosenLanguage: String = item.text.toString().trim()
                    val editor: SharedPreferences.Editor = preferences.edit()
                    if (!firstOrSecondLanguage) editor.putString(
                        "default_language_on",
                        chosenLanguage
                    ) else editor.putString("default_language_to", chosenLanguage)
                    editor.apply()
                    val i = Intent(applicationContext, OptionsActivity::class.java)
                    startActivity(i)
                }
            }
            linearLayout.addView(item)
        }
    }

    private fun setOnLongClickListener(item: TextView) {
        item.setOnLongClickListener {
            val onLongTouchBox = DialogView(
                this@SearchActivity,
                this@SearchActivity
            )
            onLongTouchBox.show()
            if (firstOrSecondLanguage) {
                onLongTouchBox.onDeleteClick(
                    converter.getDTOsFromString(item.text.toString().trim(), (pairsTranslations)),
                    preferences.getString("default_language_on", "Error"),
                    preferences.getString("default_language_to", "Error")
                )
            } else {
                onLongTouchBox.onDeleteClick(
                    converter.getDTOsFromString(item.text.toString().trim(), (pairsTranslations)),
                    preferences.getString("default_language_on", "Error"),
                    preferences.getString("default_language_to", "Error")
                )
            }

            false
        }
    }
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
        if ((searchPreferences.getString("search_state", "Error") == "original")) {
            dictionaries()
            if (sortedTranslations.isNotEmpty()) {
                addSearchListener(sortedTranslations)
                fillItemsInList(sortedTranslations, languagesList, typeface)
            }
            editor.putString("search_state", "translation")
            updateCurrentLanguages()
        } else if ((searchPreferences.getString("search_state", "Error") == "translation")) {
            dictionaries()
            if (sortedWords.isNotEmpty()) {
                fillItemsInList(sortedWords, languagesList, typeface)
                addSearchListener(sortedWords)
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
                    if ((x1 - x2
                                > (applicationContext.resources.displayMetrics.widthPixels.toFloat() / 10)
                                && abs(y1 - y2)
                                < (applicationContext.resources.displayMetrics.heightPixels.toFloat() / 10))
                    ) {
                        val i = Intent(this@SearchActivity, com.jet.learnq.MainActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(R.anim.slide_on_right, R.anim.slide_out_right)
                    }
                    if ((x2 - x1
                                > (applicationContext.resources.displayMetrics.widthPixels.toFloat() / 10)
                                && abs(y1 - y2)
                                < (applicationContext.resources.displayMetrics.heightPixels.toFloat() / 20))
                    ) {
                        mirrorPairs()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}