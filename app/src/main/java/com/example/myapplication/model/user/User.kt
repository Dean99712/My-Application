package com.example.myapplication.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.model.person.Person


@Entity(tableName = "userTable")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "firstName") var firstName: String,
    @ColumnInfo(name = "lastName") var lastName: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String? = null,
) {
    constructor() : this("", "", "", "")
}



