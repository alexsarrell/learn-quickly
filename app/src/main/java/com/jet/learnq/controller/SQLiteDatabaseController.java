package com.jet.learnq.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.learnq1.R;
import com.jet.learnq.dto.WordDTO;
import com.jet.learnq.model.LanguageModel;
import com.jet.learnq.model.WordModel;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseController extends SQLiteOpenHelper {
    public static final String LANGUAGE_ID = "LANGUAGE_ID";
    public static final String LANGUAGE_NAME = "LANGUAGE_NAME";
    public static final String TRANSLATION_ID = "TRANSLATION_ID";
    public static final String WORD_TEXT = "WORD_TEXT";
    public static final String WORD_ID = "WORD_ID";
    public static final String LANGUAGE_TABLE = "LANGUAGE_TABLE";
    public static final String TRANSLATION_TABLE = "TRANSLATION_TABLE";
    public static final String WORD_TABLE = "WORD_TABLE";
    public static final String WORD_TRANSLATION_TABLE = "WORD_TRANSLATION_TABLE";

    public SQLiteDatabaseController(@Nullable @org.jetbrains.annotations.Nullable Context context) {
        super(context, context.getString(R.string.database_name), null, Integer.parseInt(
                context.getString(R.string.version_number)));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createLanguageTableStatement = "CREATE TABLE " + LANGUAGE_TABLE + "(" + LANGUAGE_ID + " INTEGER PRIMARY KEY, " + LANGUAGE_NAME + " TEXT NOT NULL)";
        String createTranslationTableStatement = "CREATE TABLE " + TRANSLATION_TABLE + "(" + TRANSLATION_ID + " INTEGER PRIMARY KEY, " + WORD_TEXT + " TEXT NOT NULL, " + LANGUAGE_ID + " INTEGER NOT NULL, CONSTRAINT tr_fk_lan FOREIGN KEY(LANGUAGE_ID) REFERENCES " + LANGUAGE_TABLE + "(" + LANGUAGE_ID + "))";
        String createWordTableStatement = "CREATE TABLE " + WORD_TABLE + "(" + WORD_ID + " INTEGER PRIMARY KEY, " + WORD_TEXT + " TEXT NOT NULL, " + LANGUAGE_ID + " INTEGER NOT NULL, CONSTRAINT wr_fk_lan FOREIGN KEY(LANGUAGE_ID) REFERENCES " + LANGUAGE_TABLE + "(" + LANGUAGE_ID + "))";
        String createWordTranslationTableStatement = "CREATE TABLE " + WORD_TRANSLATION_TABLE + " (" + WORD_ID + " INTEGER, " + TRANSLATION_ID + " INTEGER, PRIMARY KEY(WORD_ID, TRANSLATION_ID), CONSTRAINT wt_fk_word FOREIGN KEY(WORD_ID) REFERENCES " + WORD_TABLE + "(" + WORD_ID + "), CONSTRAINT wt_fk_translation FOREIGN KEY(" + TRANSLATION_ID + ") REFERENCES " + TRANSLATION_TABLE + "(" + TRANSLATION_ID + "))";
        sqLiteDatabase.execSQL(createLanguageTableStatement);
        sqLiteDatabase.execSQL(createTranslationTableStatement);
        sqLiteDatabase.execSQL(createWordTableStatement);
        sqLiteDatabase.execSQL(createWordTranslationTableStatement);
        String addBasicLanguage1 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(1, \"Chinese\")";
        String addBasicLanguage2 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(2, \"Deutsch\")";
        String addBasicLanguage3 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(3, \"Estonian\")";
        String addBasicLanguage4 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(4, \"English\")";
        String addBasicLanguage5 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(5, \"French\")";
        String addBasicLanguage6 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(6, \"Korean\")";
        String addBasicLanguage7 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(7, \"Polish\")";
        String addBasicLanguage8 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(8, \"Russian\")";
        String addBasicLanguage9 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(9, \"Spanish\")";
        String addBasicLanguage10 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(10, \"Suomi\")";
        String addBasicLanguage11 = "INSERT INTO " + LANGUAGE_TABLE + " VALUES(11, \"Latin\")";
        sqLiteDatabase.execSQL(addBasicLanguage1);
        sqLiteDatabase.execSQL(addBasicLanguage2);
        sqLiteDatabase.execSQL(addBasicLanguage3);
        sqLiteDatabase.execSQL(addBasicLanguage4);
        sqLiteDatabase.execSQL(addBasicLanguage5);
        sqLiteDatabase.execSQL(addBasicLanguage6);
        sqLiteDatabase.execSQL(addBasicLanguage7);
        sqLiteDatabase.execSQL(addBasicLanguage8);
        sqLiteDatabase.execSQL(addBasicLanguage9);
        sqLiteDatabase.execSQL(addBasicLanguage10);
        sqLiteDatabase.execSQL(addBasicLanguage11);
    }

    public boolean addLanguage(LanguageModel languageModel) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANGUAGE_ID, languageModel.getLanguage_id());
        contentValues.put(LANGUAGE_NAME, languageModel.getName());
        long insertLan = database.insert(LANGUAGE_TABLE, null, contentValues);
        database.close();
        return insertLan != -1;
    }

    public int getLanguageId(String name) {
        String queryOn = "SELECT " + LANGUAGE_ID + " LID FROM " + LANGUAGE_TABLE + " LT WHERE LT." + LANGUAGE_NAME + " = \"" + name + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryOn, null);
        return cursor.moveToFirst() ? cursor.getInt(0) : -1;
    }

    private Cursor getAllRelationsFromLinkTable(String languageOn, String languageTo, boolean type) {
        SQLiteDatabase db = this.getReadableDatabase();
        int language1 = getLanguageId(languageOn), language2 = getLanguageId(languageTo);
        //query for words from first word table match to languageOn
        String query = "SELECT WT.WORD_ID, WT.WORD_TEXT, wt.LANGUAGE_ID, " +
                "TT.TRANSLATION_ID, TT.WORD_TEXT, tt.LANGUAGE_ID " +
                "FROM WORD_TRANSLATION_TABLE WTT LEFT JOIN WORD_TABLE WT " +
                "ON WTT.WORD_ID = WT.WORD_ID LEFT JOIN TRANSLATION_TABLE TT " +
                "ON WTT.TRANSLATION_ID = TT.TRANSLATION_ID " +
                "WHERE WT.LANGUAGE_ID =" + language1 +
                " AND TT.LANGUAGE_ID =" + language2;
        //query for words from second word table match to languageTo
        String reversedQuery = "SELECT TT.TRANSLATION_ID, " +
                "TT.WORD_TEXT, tt.LANGUAGE_ID, " +
                "WT.WORD_ID, WT.WORD_TEXT, wt.LANGUAGE_ID " +
                "FROM WORD_TRANSLATION_TABLE WTT " +
                "LEFT JOIN TRANSLATION_TABLE TT " +
                "ON WTT.TRANSLATION_ID = TT.TRANSLATION_ID " +
                "LEFT JOIN WORD_TABLE WT " +
                "ON WTT.WORD_ID = WT.WORD_ID " +
                "WHERE WT.LANGUAGE_ID =" + language2 +
                " AND TT.LANGUAGE_ID =" + language1;
        return !type ? db.rawQuery(query, null) : db.rawQuery(reversedQuery, null);
    }

    public List<List<WordModel>> getAllPairs(String languageOn, String languageTo) {
        SQLiteDatabase database = SQLiteDatabaseController.this.getReadableDatabase();

        Cursor c = database.rawQuery("SELECT * FROM WORD_TABLE", null);
        if (c.moveToFirst()) {
            do {
                Log.println(Log.INFO, "WT", "WT + " + c.getString(1) + c.getInt(0));
            }
            while (c.moveToNext());
        }
        c = database.rawQuery("SELECT * FROM TRANSLATION_TABLE", null);
        if (c.moveToFirst()) {
            do {
                Log.println(Log.INFO, "WT", "TT + " + c.getString(1) + c.getInt(0));
            }
            while (c.moveToNext());
        }

        List<List<WordModel>> pairs = new ArrayList<>();
        Cursor cursor = getAllRelationsFromLinkTable(languageOn, languageTo, false);
        Cursor reversedCursor = getAllRelationsFromLinkTable(languageOn, languageTo, true);
        List<WordModel> words = new ArrayList<>();
        List<WordModel> translations = new ArrayList<>();
        List<WordModel> reversedWords = new ArrayList<>();
        List<WordModel> reversedTranslations = new ArrayList<>();
        buildPairDTOs(cursor, words, translations);
        buildPairDTOs(reversedCursor, reversedWords, reversedTranslations);
        buildPairs(cursor, words, translations);
        buildPairs(reversedCursor, reversedWords, reversedTranslations);
        words.addAll(reversedWords);
        translations.addAll(reversedTranslations);
        pairs.add(words);
        pairs.add(translations);
        return pairs;
    }

    private void buildPairDTOs(Cursor cursor, List<WordModel> words, List<WordModel> translations) {
        if (cursor.moveToFirst()) {
            do {
                words.add(new WordModel(
                        cursor.getInt(0),
                        cursor.getInt(2),
                        cursor.getString(1),
                        new ArrayList<>()));
                translations.add(new WordModel(
                        cursor.getInt(3),
                        cursor.getInt(5),
                        cursor.getString(4),
                        new ArrayList<>()));
            } while (cursor.moveToNext());
        }
    }

    private WordModel findModelInList(List<WordModel> words, int id, int languageId, String word) {

        for (WordModel wm : words) {
            if (wm.getWord_id() == id) {
                return wm;
            }
        }
        return new WordModel(0, 0, "", new ArrayList<>());
    }

    private void buildPairs(Cursor cursor, List<WordModel> words, List<WordModel> translations) {
        for (int i = 0; i < words.size(); i++) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == words.get(i).getWord_id()) {
                        words.get(i).addTranslation(findModelInList(translations,
                                cursor.getInt(3),
                                cursor.getInt(5),
                                cursor.getString(4)));
                        /*words.get(i).addTranslation(new WordModel(
                                cursor.getInt(3),
                                cursor.getInt(5),
                                cursor.getString(4),
                                new ArrayList<>()));*/
                    }
                    if (cursor.getInt(3) == translations.get(i).getWord_id()) {
                        translations.get(i).addTranslation(findModelInList(
                                words,
                                cursor.getInt(0),
                                cursor.getInt(2),
                                cursor.getString(1)));
                        /*translations.get(i).addTranslation(new WordModel(
                                cursor.getInt(0),
                                cursor.getInt(2),
                                cursor.getString(1),
                                new ArrayList<>()));*/
                    }
                } while (cursor.moveToNext());
            }
        }
    }

    public List<LanguageModel> getAllLanguages() {
        List<LanguageModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + LANGUAGE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int LangId = cursor.getInt(0);
                String LangName = cursor.getString(1);
                returnList.add(new LanguageModel(LangId, LangName));
            } while (cursor.moveToNext());
        } else {
            //failure. do not add anything to the list
        }
        cursor.close();
        db.close();
        return returnList;
    }

    private boolean hasSimilar(WordModel wordModel, String COLUMN, String TABLE_NAME) {
        SQLiteDatabase database = SQLiteDatabaseController.this.getReadableDatabase();
        String queryWordString = "SELECT " + COLUMN + ", " + LANGUAGE_ID + " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(queryWordString, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int languageId = cursor.getInt(1);
                return name.equalsIgnoreCase(wordModel.getName())
                        & languageId == wordModel.getLanguage_id();
            } while (cursor.moveToNext());
        }
        database.close();
        return false;
    }

    public boolean addOnePair(WordModel word, WordModel translation) {
        if (hasSimilar(word, WORD_TEXT, WORD_TABLE)) {
            if (hasSimilar(translation, WORD_TEXT, TRANSLATION_TABLE)) {
                if (linkContainsRow(word, translation)) {
                    return false;
                } else {
                    return insertPair(word, translation) != -1;
                }
            } else {
                return insertTranslation(translation) != -1 &&
                        insertPair(word, translation) != -1;
            }
        } else if (hasSimilar(word, WORD_TEXT, TRANSLATION_TABLE)) {
            if (hasSimilar(translation, WORD_TEXT, WORD_TABLE)) {
                if (linkContainsRow(translation, word)) {
                    return false;
                } else {
                    return insertPair(translation, word) != -1;
                }
            } else {
                return insertWord(translation) != -1 && insertPair(translation, word) != -1;
            }
        } else if (!hasSimilar(word, WORD_TEXT, WORD_TABLE)
                && !hasSimilar(word, WORD_TEXT, TRANSLATION_TABLE)) {
            if (hasSimilar(translation, WORD_TEXT, WORD_TABLE)) {
                return insertTranslation(word) != -1 &&
                        insertPair(translation, word) != -1;
            } else if (hasSimilar(translation, WORD_TEXT, TRANSLATION_TABLE)) {
                return insertWord(word) != -1 &&
                        insertPair(word, translation) != -1;
            } else {
                return insertWord(word) != -1 &&
                        insertTranslation(translation) != -1 &&
                        insertPair(word, translation) != -1;
            }
        }
        return false;
    }

    private boolean linkContainsRow(WordModel word, WordModel translation) {
        String query = "SELECT * FROM wtt " + WORD_TRANSLATION_TABLE +
                " LEFT JOIN t1 " + WORD_TABLE + " ON wtt." + WORD_ID +
                " = t1." + WORD_ID + " LEFT JOIN t2" + TRANSLATION_TABLE +
                " ON WTT." + TRANSLATION_ID + " = T2." + TRANSLATION_ID +
                " WHERE T1.WORD_TEXT=" + word.getName() +
                " AND " + " T2.WORD_TEXT=" + translation.getName();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        return cursor.getColumnCount() == 0;
    }

    private long insertPair(WordModel wordModel, WordModel translationModel) {
        SQLiteDatabase database = SQLiteDatabaseController.this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String q1 = "SELECT WORD_ID FROM WORD_TABLE AS WT WHERE WT.WORD_TEXT = \"" + wordModel.getName() + "\"";
        String q2 = "SELECT TRANSLATION_ID FROM TRANSLATION_TABLE AS TT WHERE TT.WORD_TEXT = \"" + translationModel.getName() + "\"";

        contentValues.put(WORD_ID, getId(q1, database));
        contentValues.put(TRANSLATION_ID, getId(q2, database));


        long res = database.insert(WORD_TRANSLATION_TABLE, null, contentValues);
        contentValues.clear();
        database.close();

        return res;
    }

    private int getId(String query, SQLiteDatabase database) {
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int i = cursor.getInt(0);
            return i;
        }
        cursor.close();
        return -1;
    }

    private long insertWord(WordModel wordModel) {
        return insertEntity(wordModel, WORD_TABLE, WORD_ID);
    }

    private long insertTranslation(WordModel translationModel) {
        return insertEntity(translationModel, TRANSLATION_TABLE, TRANSLATION_ID);
    }

    private long insertEntity(WordModel Model, String tableName, String entityId) {
        SQLiteDatabase database = SQLiteDatabaseController.this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = database.rawQuery("SELECT MAX(" + entityId + ") FROM " + tableName, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        contentValues.put(entityId, count + 1);
        cursor.close();
        contentValues.put(WORD_TEXT, Model.getName());
        contentValues.put(LANGUAGE_ID, Model.getLanguage_id());
        long res = database.insert(tableName, null, contentValues);
        contentValues.clear();
        database.close();
        return res;
    }

    public int deleteFromTable(WordDTO wordToDelete, String languageOn, String languageTo) {
        Cursor cursor = getAllRelationsFromLinkTable(languageOn, languageTo, false);
        Cursor reversedCursor = getAllRelationsFromLinkTable(languageOn, languageTo, true);
        int languageOn_id = getLanguageId(languageOn);
        SQLiteDatabase db = getWritableDatabase();
        deleteIfHasLinks(cursor, wordToDelete, WORD_TABLE, TRANSLATION_TABLE, TRANSLATION_ID, languageOn_id, db);
        deleteIfHasLinks(reversedCursor, wordToDelete, TRANSLATION_TABLE, WORD_TABLE, WORD_ID, languageOn_id, db);
        cursor.close();
        reversedCursor.close();
        db.close();
        return 0;
    }

    private void deleteIfHasLinks(Cursor cursor, WordDTO wordToDelete, String TABLE_NAME,
                                  String TRANSLATION_TABLE_NAME, String COLUMN_ID, int languageOn_id, SQLiteDatabase db) {
        if (cursor.moveToFirst()) {
            boolean deleted = false;
            do {
                if (cursor.getString(1).equals(wordToDelete.getName())) {
                    String deleteQuery = "DELETE from "
                            + WORD_TRANSLATION_TABLE
                            + " WHERE "
                            + WORD_TRANSLATION_TABLE + "."
                            + WORD_ID + " = " + cursor.getInt(0); //удаляем запись о связях из смежной таблицы
                    db.execSQL(deleteQuery);
                    if (!deleted) {
                        for (WordModel wordModel : wordToDelete.getTranslations()) {
                            Log.println(Log.INFO, "WM", "WORD MODEL " + wordModel.getName());
                            if (wordModel.getTranslations().size() == 1) { //удаляем из правой таблицы все переводы
                                deleteQuery = "DELETE from "
                                        + TRANSLATION_TABLE_NAME
                                        + " WHERE "
                                        + TRANSLATION_TABLE_NAME + "."
                                        + COLUMN_ID + " = " + wordModel.getWord_id();
                                db.execSQL(deleteQuery);
                            }
                        }
                    }
                    deleteQuery = "DELETE from " + TABLE_NAME + " WHERE " + WORD_TEXT
                            + " = \"" + wordToDelete.getName() + "\"" +
                            " AND " + LANGUAGE_ID + " = "
                            + languageOn_id;
                    db.execSQL(deleteQuery);
                }
            } while (cursor.moveToNext());
        }
    }

    private boolean hasAnotherLinks(Cursor cursor, WordDTO wordDTO) {
        if (cursor.moveToFirst()) {
            do {

            } while (cursor.moveToNext());
        }
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LANGUAGE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSLATION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WORD_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WORD_TRANSLATION_TABLE);
        onCreate(sqLiteDatabase);
    }
}
