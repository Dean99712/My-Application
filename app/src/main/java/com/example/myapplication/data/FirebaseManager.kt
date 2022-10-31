package com.example.myapplication.data

import android.annotation.SuppressLint
import android.content.Context
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseManager private constructor(val context: Context) {

    companion object {

        @SuppressLint("StaticFieldLeak")
        val db = Firebase.firestore

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: FirebaseManager

        fun getInstance(context: Context): FirebaseManager {
            if (!Companion::instance.isInitialized)
                instance = FirebaseManager(context)
            return instance
        }
    }

    fun addPersonToUser(user: User, person: Person): Task<DocumentReference> {
        return db.collection("users/${user.email}/personList").add(person)
    }

    fun createUser(user: User): Task<Void> {
        return db.collection("users").document(user.email).set(user)
    }

}

