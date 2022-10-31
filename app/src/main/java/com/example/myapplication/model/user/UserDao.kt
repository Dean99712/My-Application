package com.example.myapplication.model.user

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface UserDao {

    @Insert
    fun addUser(user: User)


}
