package com.example.myapplication.model.person

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

enum class IMAGE_TYPE {
    URI, URL, BMP
}

@Parcelize
@Entity(tableName = "peopleTable")
data class Person(

    @ColumnInfo(name = "person_name") var name: String,
    @ColumnInfo(name = "person_details") var details: String? = null,
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(name = "image_path") var imagePath: String? = null,
    @ColumnInfo(name = "image_type") var imageType: IMAGE_TYPE? = null,

    ) : Parcelable {

    constructor() : this("", "", "", null)
}