package com.jet.learnq

import android.content.SharedPreferences

//TODO нужно сделать страничку с переводами и реализовать наблюдателя, чтобы каждый раз, когда мы добавляли пару, обновлялись словечки в словаре
//TODO возможно обсервер здесь не нужен, в этом разберемся во время разработки
interface UpdateLanguagesObserver {
    fun update(preferences: SharedPreferences)
}