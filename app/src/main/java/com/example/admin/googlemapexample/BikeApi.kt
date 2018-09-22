package com.example.admin.googlemapexample

import com.example.admin.googlemapexample.model.BikeResponse
import com.google.gson.annotations.JsonAdapter
import io.reactivex.Maybe
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

interface BikeApi {

    @GET("v2/networks/bicing")
    fun getStations(): Maybe<BikeResponse>
}