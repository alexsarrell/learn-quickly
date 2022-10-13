package com.jet.learnq

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.learnq1.R
import com.jet.learnq.controller.OptionsActivity
import com.jet.learnq.controller.SQLiteDatabaseController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var searchPreferences: SharedPreferences
    private lateinit var addTheNewCouple: ImageButton
    private lateinit var editTextWord: EditText
    private lateinit var editTextTranslation: EditText
    private lateinit var currentLanguages: TextView
    private lateinit var dictionaryController: SQLiteDatabaseController
    private lateinit var search: Intent
    private lateinit var options: Intent
    private lateinit var currentLanguageOn: String
    private lateinit var currentLanguageTo: String
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        search = SearchActivity.newIntent(this)
        options = Intent(this@MainActivity, OptionsActivity::class.java)
        val sharedPreferences = getSharedPreferences("current_theme", MODE_PRIVATE)
        //getApplicationContext().deleteDatabase("dictionaries.db");
        if ("Light" == sharedPreferences.getString("current_theme", "Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        init()
        currentLanguages = findViewById(R.id.activity_main_current_languages_textview)
        updateCurrentLanguages()
        addTextViewOnClickListener(currentLanguages)
        setOnAddClickListener(addTheNewCouple)
    }

    private fun setOnAddClickListener(button: View?) {
        button!!.setOnClickListener {
            val sv = StringsValidator(applicationContext)
            val rotation = AnimationUtils.loadAnimation(applicationContext, R.anim.center_rotate)
            button.startAnimation(rotation)
            val word = editTextWord.text.toString()
            val translation = editTextTranslation.text.toString()
            if (preferences.getString("default_language_on", "empty") != "empty"
                && preferences.getString("default_language_to", "empty") != "empty"
            ) {
                if (word.isNotEmpty() && translation.isNotEmpty()) {
                    if (word.replace("\\s".toRegex(), "").isNotEmpty()
                        && translation.replace("\\s".toRegex(), "").isNotEmpty()
                    ) {
                        if (sv.catchForbiddenString(word) && sv.catchForbiddenString(translation)) {
                            Dictionary.addANewPair(word, translation, preferences, dictionaryController)
                            Toast.makeText(
                                applicationContext, "The pair is successfully saved",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext, "You used a forbidden symbol or the word is too long",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        editTextWord.setText("")
                        editTextTranslation.setText("")
                    } else {
                        Toast.makeText(
                            applicationContext, "You can't save a whitespace word",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext, "You can't save an empty word",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext, "Choose languages at first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateCurrentLanguages() {
        val sb = StringBuilder()
        currentLanguageOn = preferences.getString("default_language_on", "Choose a language").toString()
        currentLanguageTo = preferences.getString("default_language_to", "Choose a language").toString()
        sb.append(currentLanguageOn)
        sb.append(" | ")
        sb.append(currentLanguageTo)
        currentLanguages.text = sb
    }

    private fun mirrorLanguages() {
        val language = preferences.getString("default_language_on", "Choose a language")
        val editor = preferences.edit()
        editor.putString(
            "default_language_on",
            preferences.getString("default_language_to", "Choose a language")
        )
        editor.putString("default_language_to", language)
        editor.apply()
    }

    private fun addTextViewOnClickListener(textView: TextView?) {
        textView!!.setOnClickListener {
            if (currentLanguageOn == "Choose a language" && currentLanguageTo == "Choose a language") {
                options.putExtra("lanOrPairs", true)
                startActivity(options)
            } else {
                mirrorLanguages()
                updateCurrentLanguages()
            }
        }
    }

    private fun init() {
        preferences = getSharedPreferences("Properties", MODE_PRIVATE)
        searchPreferences = getSharedPreferences("Pairs_properties", MODE_PRIVATE)
        val editor = searchPreferences.edit()
        editor.putString("search_state", "original")
        editor.apply()
        addTheNewCouple = findViewById(R.id.main_activity_newCoupleButton)
        editTextWord = findViewById(R.id.main_activity_editText_word)
        editTextTranslation = findViewById(R.id.main_activity_editText_translation)
        dictionaryController = SQLiteDatabaseController(this@MainActivity)


        CoroutineScope(Dispatchers.Default).launch {
            val lists = Dictionary.getSortedStringPairs(preferences, dictionaryController)
            val sortedWords = lists[0]
            val sortedTranslations = lists[1]
            search.putStringArrayListExtra("sorted_words", sortedWords)
            search.putStringArrayListExtra("sorted_translations", sortedTranslations)
        }
    }

    override fun onTouchEvent(touch: MotionEvent): Boolean {
        when (touch.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = touch.x
                y1 = touch.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = touch.x
                y2 = touch.y
                if (y2 - y1 > applicationContext.resources.displayMetrics.heightPixels.toFloat() / 10
                    && abs(x1 - x2) < applicationContext.resources.displayMetrics.heightPixels.toFloat() / 10
                ) {
                    startActivity(options)
                    overridePendingTransition(R.anim.slide_on_top, R.anim.slide_out_top)
                }
                if (x2 - x1 > applicationContext.resources.displayMetrics.widthPixels.toFloat() / 10
                    && abs(y1 - y2) < applicationContext.resources.displayMetrics.heightPixels.toFloat() / 10
                ) {
                    search.putExtra("lanOrPairs", true)
                    startActivity(search)
                    overridePendingTransition(R.anim.slide_on_left, R.anim.slide_out_left)
                }
            }
        }
        return false
    }
}