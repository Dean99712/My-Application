package com.example.myapplication.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.ApiInterface
import com.example.myapplication.api.ApiResponse
import com.example.myapplication.data.Repository
import com.example.myapplication.model.person.IMAGE_TYPE
import com.example.myapplication.model.person.Person
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


object ImageManager {

    private final val REQUEST_CODE = 1

    fun takePicture(person: Person, getContent: ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getContent.launch(intent)
    }




    @OptIn(DelicateCoroutinesApi::class)
    fun onImageResultFromCamera(
        result: ActivityResult,
        person: Person,
        context: Context
    ) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap
            val arrayOfBytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOfBytes)

            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "title",
                null
            )
            val uri = Uri.parse(path)
            GlobalScope.launch(Dispatchers.IO) {
                addImageToPerson(person, uri.toString(), IMAGE_TYPE.BMP, context)
            }
        }
    }

    fun getImageFromGallery(person: Person, getContent: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    fun onImageResultFromGallery(
        result: ActivityResult, person: Person, context: Context
    ) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                addImageToPerson(person, uri.toString(), IMAGE_TYPE.URI, context)
            }
        }
    }

    fun addImageToPerson(
        person: Person, imagePath: String, imageType: IMAGE_TYPE, context: Context
    ) {
        thread(start = true) {
            Repository.getInstance(context).updatePersonImage(person, imagePath, imageType)
        }
    }

    fun getImageFromApi(person: Person, context: Context) {
        val retrofit = ApiInterface.create()
        retrofit.getImages().enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                val apiImage = apiResponse!!.imagesList.toList()
                val randomImage = apiImage.random()
                addImageToPerson(person, randomImage.imageUrl, IMAGE_TYPE.URL, context)
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d(t.message, "onFailure: Wrong Api Response ")
            }
        })
    }
}
