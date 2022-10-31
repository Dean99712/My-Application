//package com.example.myapplication.util
//
//import androidx.room.TypeConverter
//import com.example.myapplication.model.person.Person
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class PersonListTypeConvertor {
//    @TypeConverter
//    fun fromPersonObject(personList: Map<String,Person>): String {
//        val listType = object : TypeToken<Map<String,Person>>() {}
//            .type
//        return Gson().toJson(personList, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: String): Map<String,Person> {
//        val type = object : TypeToken<Map<String,Person>>() {}.type
//        return Gson().fromJson(list, type)
//    }
//}