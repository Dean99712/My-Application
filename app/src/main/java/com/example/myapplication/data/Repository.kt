package com.example.myapplication.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.myapplication.model.person.IMAGE_TYPE
import com.example.myapplication.model.person.Person
import com.example.myapplication.model.person.PersonDatabase
import com.example.myapplication.model.user.User
import com.example.myapplication.util.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository private constructor(context: Context) {

    private val peopleDao = PersonDatabase.getDatabase(context).getPersonDao()
    private val firebaseManager = FirebaseManager.getInstance(context)

    companion object{
        private lateinit var instance: Repository

        fun getInstance(context: Context) : Repository {
            if (!Companion::instance.isInitialized){
                instance = Repository(context)
            }
            return instance
        }
    }

    fun getAllPeopleAsLiveData(): LiveData<List<Person>> {
        return peopleDao.getAllPeople()
    }

    fun addPerson(person: Person) {
        peopleDao.insertPerson(person)
    }

    fun deletePerson(person: Person) {
        peopleDao.deletePerson(person)
    }

//    fun updatePerson(id : Int , personName: String, personDetails: String){
//        return peopleDao.updatePersonById(id , personName, personDetails)
//    }

    fun updatePersonImage(person: Person, imagePath: String, imageType: IMAGE_TYPE) {
        peopleDao.updatePersonImage(person, imagePath, imageType)
    }

}