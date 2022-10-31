package com.example.myapplication.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.myapplication.model.user.User

class SharedPreferencesManager private constructor(context: Context) {

    val SHARED_PREFERENCES = "sharedPref"
    val SHARED_PREFERENCES_EMAIL = "sharedUserEmail"
    val SHARED_PREFERENCES_NAME = "sharedUserName"
    val SHARED_PREFERENCES_LAST_NAME = "sharedUserLastName"

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

    companion object {

        private lateinit var instance: SharedPreferencesManager

        fun getInstance(context: Context): SharedPreferencesManager {
            if (!Companion::instance.isInitialized) {
                instance = SharedPreferencesManager(context)
            }
            return instance
        }
    }
}