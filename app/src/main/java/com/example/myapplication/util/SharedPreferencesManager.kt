package com.example.myapplication.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.user.User

class SharedPreferencesManager private constructor(context: Context) {

    val sharedPreferences =
        context.getSharedPreferences(R.string.app_name.toString(), AppCompatActivity.MODE_PRIVATE)


    companion object {

        lateinit var myUser: User

        private lateinit var instance: SharedPreferencesManager

        fun getInstance(context: Context): SharedPreferencesManager {
            if (!Companion::instance.isInitialized) {
            }
            instance = SharedPreferencesManager(context)
            return instance
        }
    }
}