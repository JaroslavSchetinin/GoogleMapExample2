package com.example.admin.googlemapexample


import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat

import android.util.Log

import com.example.admin.googlemapexample.extensions.makeToast
import com.example.admin.googlemapexample.model.BikeResponse
import com.example.admin.googlemapexample.model.Stations
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.annotation.SuppressLint
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    lateinit var map: GoogleMap
    private lateinit var requestInterface: BikeApi
    private val LOCATION_REQUEST_CODE = 101

    private var mFusedLocationClient: FusedLocationProviderClient? = null


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)






        requestInterface = ApiFactory(this).apiService

        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        val retrofit = Retrofit.Builder()
//                .baseUrl("http://api.citybike.es/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//        val bikeApi = retrofit.create(BikeApi::class.java)

        requestInterface.getStations().enqueue(object : Callback<BikeResponse> {
            override fun onFailure(call: Call<BikeResponse>?, t: Throwable?) {
                Log.e("TAG", "ERROR")
                "error".makeToast(this@MainActivity)
            }

            override fun onResponse(call: Call<BikeResponse>?, response: Response<BikeResponse>) {
                val nn = response.body()
                "${nn?.network?.name} count: ${nn?.network?.stations?.size}".makeToast(this@MainActivity)
                nn?.network?.stations?.forEach { map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)) }
//
            }
        })


    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient?.lastLocation
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        map.addMarker(MarkerOptions().position(LatLng(task.result!!.latitude, task.result!!.longitude))
                                .title("My Position")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(task.result!!.latitude, task.result!!.longitude), 15f))


                    } else {
                        Log.w("TAG", "getLastLocation:exception", task.exception)

                    }
                }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true

        val value = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)


        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, value, LOCATION_REQUEST_CODE)
        }


        map.uiSettings.isMyLocationButtonEnabled = true

        getLastLocation()
    }
}
private fun Stations.getLatLng(): LatLng = LatLng(this.latitude.toDouble(), this.longitude?.toDouble()
        ?: 0.0)


