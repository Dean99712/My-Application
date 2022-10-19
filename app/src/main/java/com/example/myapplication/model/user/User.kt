package com.example.myapplication.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.model.person.Person

@Entity(tableName = "userTable")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "firstName")
    val firstName : String,
    @ColumnInfo(name = "lastName")
    val lastName : String,
//    @ColumnInfo(name = "peopleList")
//    val personList: List<Person>,
    @ColumnInfo(name = "imageUrl")
    val imageUrl : String? = null
)
