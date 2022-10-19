package com.example.myapplication.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {

//https://pixabay.com/api/?key=29958309-9a80dea824f08f90c70b785ca&image_type=photo&pretty=true&q=person

    @GET("https://pixabay.com/api/?key=29958309-9a80dea824f08f90c70b785ca&image_type=photo&pretty=true&q=person&min_width=410&per_page=200")
    fun getImages(): Call<ApiResponse>

    companion object {
        val BASE_URL = "https://pixabay.com/api/"
        fun create(): ApiInterface {
            val retroFit = Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retroFit.create(ApiInterface::class.java)
        }
    }
}