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
import android.location.Location
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    lateinit var map: GoogleMap
    private lateinit var requestInterface: BikeApi
    private val LOCATION_REQUEST_CODE = 101
    private val compositeDisposable = CompositeDisposable()
    lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private var mFusedLocationClient: FusedLocationProviderClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestInterface = ApiFactory(this).apiService

        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e("onSlide", "onSlide")


            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        bottomSheet.tv.text = "EXPANDED"

                    BottomSheetBehavior.STATE_COLLAPSED ->
                            bottomSheet.tv.text = "COLAPSED"

                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }

                }


            }

        })

        behavior.peekHeight = 200

        bottom_sheet.setOnClickListener(View.OnClickListener {
            expandCloseSheet()
        })


//        val retrofit = Retrofit.Builder()
//                .baseUrl("http://api.citybike.es/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//        val bikeApi = retrofit.create(BikeApi::class.java)
//


        compositeDisposable.add(requestInterface.getStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { response ->
                    response?.let { onResponse(it) }
                }
                .doOnError { it.message?.makeToast(this) }
                .subscribe())


    }

    private fun expandCloseSheet() {
        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun onResponse(bikeResponse: BikeResponse) {

        "${bikeResponse.network?.name} count: ${bikeResponse.network?.stations?.size}".makeToast(this@MainActivity)
        bikeResponse.network?.stations?.forEach { map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)) }
//
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        map.uiSettings.isZoomControlsEnabled = true

        val value = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)


        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(this, value, LOCATION_REQUEST_CODE)
        }



        map.uiSettings.isMyLocationButtonEnabled = true

    }
}

private fun Stations.getLatLng(): LatLng = LatLng(this.latitude.toDouble(), this.longitude?.toDouble()
        ?: 0.0)


