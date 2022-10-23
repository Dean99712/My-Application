package com.example.myapplication.model.person

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PeopleDao {

    @Insert
    fun insertPerson(person: Person)

    @Query("UPDATE peopleTable SET person_name =:person, person_details = :personDetails WHERE id = :id")
    fun updatePersonById(id: Int, person: String, personDetails: String)

    @Delete
    fun deletePerson(person: Person)

    @Query("Select * from peopleTable")
    fun getAllPeople(): LiveData<List<Person>>

    @Query("Select * from peopleTable")
    fun getAllPeopleList(): List<Person>

    @Update
    fun updatePerson(person: Person)

    fun updatePersonImage(person: Person, imagePath : String, imageType: IMAGE_TYPE){
        person.imagePath = imagePath
        person.imageType = imageType
        updatePerson(person)
    }

}
