package com.example.myapplication.data

import android.annotation.SuppressLint
import android.content.Context
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.user.User
import com.example.myapplication.util.SharedPreferencesManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseManager private constructor(val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = "Users"

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: FirebaseManager

        fun getInstance(context: Context): FirebaseManager {
            if (!Companion::instance.isInitialized)
                instance = FirebaseManager(context)
            return instance
        }
    }

    fun addUser(user: User): Task<Void> {
        return db.collection(usersCollection)
            .document(user.email)
            .set(user)
    }

    fun getUser(): Task<DocumentSnapshot> {
        return db.collection(usersCollection).document("dean2910997@gmail.com").get()
    }

    fun addPeopleToUser(person: Person) {
        val user = SharedPreferencesManager.myUser
        db.collection(usersCollection).document(SharedPreferencesManager.myUser.email)
            .set(user)
    }
}