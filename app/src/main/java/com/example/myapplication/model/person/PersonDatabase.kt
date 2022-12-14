package com.example.myapplication.model.person

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.model.user.User
import com.example.myapplication.model.user.UserDao

@Database(entities = [Person::class, User::class], version = 1, exportSchema = false)

abstract class PersonDatabase : RoomDatabase() {

    abstract fun getPersonDao(): PeopleDao
    abstract fun getUserDao(): UserDao

    companion object{
        fun getDatabase(context: Context): PersonDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PersonDatabase::class.java,
                "people_database"
            ).build()
        }
    }
}