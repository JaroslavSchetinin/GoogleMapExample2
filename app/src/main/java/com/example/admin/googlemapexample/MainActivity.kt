package com.example.admin.googlemapexample

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.bind
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.example.admin.googlemapexample.extensions.makeToast
import com.example.admin.googlemapexample.model.BikeResponse
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    lateinit var map: GoogleMap
    private lateinit var requestInterface: BikeApi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestInterface = ApiFactory(this).apiService

        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val retrofit = Retrofit.Builder()
                .baseUrl("http://api.citybik.es/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val bikeApi = retrofit.create(BikeApi::class.java)

        requestInterface.getStations().enqueue(object : Callback<BikeResponse> {
            override fun onFailure(call: Call<BikeResponse>?, t: Throwable?) {
                Log.e("TAG", "ERROR")
                "error".makeToast(this@MainActivity)
            }

            override fun onResponse(call: Call<BikeResponse>?, response: Response<BikeResponse>) {
                val nn = response.body()
                "${nn?.network?.name} count: ${nn?.network?.stations?.size}".makeToast(this@MainActivity)
//                val starWarsPerson = response.body()
//                if (starWarsPerson != null) {
//                    bind(starWarsPerson)
//                }
            }
        })




    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true


        val location1 = LatLng(41.390205, 2.154007)
        map.addMarker(MarkerOptions().position(location1).title("Location"))

        val location2 = LatLng(40.73, -73.99)
        map.addMarker(MarkerOptions().position(location2).title("New York"))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location2, 20f))
    }


}


