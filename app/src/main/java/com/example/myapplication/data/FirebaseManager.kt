package com.example.myapplication.data

import android.annotation.SuppressLint
import android.content.Context
import com.example.myapplication.model.person.IMAGE_TYPE
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
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

    fun updatePersonImage(person: Person, imageUri: String, imageType: IMAGE_TYPE) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        db.collection("users").document(email).collection("personList").document(person.id)
            .update(
                "imagePath", imageUri,
                "imageType", imageType
            )
    }

    fun addPersonToUser(person: Person, finished: () -> Unit) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        getUser(email) { user ->
            db.collection("users").document(email).collection("personList").document(person.id)
                .set(person)
            finished()
        }
    }

    private fun getUser(email: String, callback: (user: User) -> Unit) {
        db.collection("users").document(email).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    callback(user)
                }
            }
    }

    fun createUser(user: User): Task<Void> {
        return db.collection("users").document(user.email).set(user)
    }

    fun updatePerson(person: Person): Task<Void> {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        return db.collection("users/${email}/personList").document().set(person)
    }

    fun removePerson(person: Person): Query {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        return db.collection("users/${email}/personList")
    }
}


