package com.example.admin.googlemapexample

import com.example.admin.googlemapexample.model.BikeResponse
import com.google.gson.annotations.JsonAdapter
import retrofit2.Call
import retrofit2.http.GET

interface BikeApi {

    @GET("v2/networks/bicing")
    fun getStations(): Call<BikeResponse>
}