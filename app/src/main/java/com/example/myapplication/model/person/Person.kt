package com.example.myapplication.model.person

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class IMAGE_TYPE {
    URI, URL, BMP
}

@Parcelize
@Entity(tableName = "peopleTable")
data class Person(
    @ColumnInfo(name = "person_name") var name: String,
    @ColumnInfo(name = "person_details") var details: String? = null,
    @ColumnInfo(name = "image_path") var imagePath: String? = null,
    @ColumnInfo(name = "image_type") var imageType: IMAGE_TYPE? = null
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    constructor() : this("", "", "", null)
}