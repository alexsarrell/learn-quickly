package com.jet.learnq

import android.content.SharedPreferences
import com.jet.learnq.controller.SQLiteDatabaseController

//сделаем словарь наблюдателем и он будет проверять, если входящие языки те же, но зеркальные - меняем
//один новый один старый? Обновляем только новый
interface UpdateLanguagesObservable {
    fun addObserver(updateLanguagesObserver: UpdateLanguagesObserver)
    fun deleteObserver(updateLanguagesObserver: UpdateLanguagesObserver)
    fun notifyObservers(preferences: SharedPreferences, sqLiteDatabaseController: SQLiteDatabaseController)
}