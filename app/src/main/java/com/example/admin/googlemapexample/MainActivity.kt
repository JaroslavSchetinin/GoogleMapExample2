package com.example.admin.googlemapexample

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.admin.googlemapexample.model.Stations
import com.google.android.gms.maps.*
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.widget.*
import com.example.admin.googlemapexample.extensions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var map: GoogleMap
    private lateinit var requestInterface: BikeApi
    private val compositeDisposable = CompositeDisposable()

    private var bottomSheetFragment = BottomSheetFragment()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var lastLocation: LatLng

    companion object {
        var bikeStations: Pair<String, List<Stations>>? = null
        const val MY_LAST_LOCATION = "my_location"
        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault())
    }

    private val SHOW_ALL = 0
    private val SHOW_WITH_BIKES = 1
    private val SHOW_WITH_PARKING_SLOTS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestInterface = ApiFactory(this).apiService

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.bottom_sheet_fragment_container, bottomSheetFragment)
                .commit()

        requestStations(SHOW_ALL)
        setupSearchButtons()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val stationListFragment = StationListFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_list ->
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.recycler_view_fragment_container, stationListFragment)
                            .addToBackStack(null)
                            .commit()
                R.id.action_map -> {
                    if (supportFragmentManager.backStackEntryCount > 0)
                        supportFragmentManager.popBackStack()
                }
            }
            true
        }
    }

    private fun setupSearchButtons() {
        val lookingForBikeButton = findViewById<Button>(R.id.looking_for_a_bike)
        lookingForBikeButton.setOnClickListener {
            val differenceInMinutes = getDifferenceInMinutes()
            if (differenceInMinutes > 1)
                requestStations(SHOW_WITH_BIKES)
            else
                showStationsWithBikes(bikeStations!!.second)
        }

        val lookingForParking = findViewById<Button>(R.id.looking_for_a_parking)
        lookingForParking.setOnClickListener {
            val differenceInMinutes = getDifferenceInMinutes()

            if (differenceInMinutes > 1)
                requestStations(SHOW_WITH_PARKING_SLOTS)
            else
                showStationsWithParking(bikeStations!!.second)
        }

        val showAllStations = findViewById<Button>(R.id.show_all_stations)
        showAllStations.setOnClickListener {
            map.clear()
            val differenceInMinutes = getDifferenceInMinutes()
            if (differenceInMinutes > 1)
                requestStations(SHOW_ALL)
            else
                showAllStations(bikeStations!!.second)
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun chooseIcon(it: Stations): Int {
        val total = it.empty_slots!!.toInt() + it.free_bikes!!.toInt()
        val coefOfFree: Double = it.free_bikes!!.toDouble().div(total)

        return when {
            coefOfFree == 0.0 -> R.drawable.loc_0
            0.0 < coefOfFree && coefOfFree <= 0.25 -> R.drawable.loc_25
            0.25 < coefOfFree && coefOfFree <= 0.5 -> R.drawable.loc_50
            0.5 < coefOfFree && coefOfFree <= 0.75 -> R.drawable.loc_75
            else -> R.drawable.loc_100
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            mFusedLocationClient?.lastLocation
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful && task.result != null) {
                            lastLocation = LatLng(task.result.latitude, task.result.longitude)
                            val sharedPreferences = getSharedPreferences("AA", MODE_PRIVATE).edit()
                            sharedPreferences.putString(MY_LAST_LOCATION, "${task.result.latitude}, ${task.result.longitude}").apply()

                            map.addMarker(MarkerOptions().position(lastLocation)
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
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        getLastLocation()
        map.setOnMarkerClickListener { p0 ->
            fillOutTheForm(bikeStations?.second?.firstOrNull { it.name == p0.title })
            bottomSheetFragment.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            false
        }
    }

    private fun fillOutTheForm(station: Stations?) {

        val freeBikesTextView = findViewById<TextView>(R.id.free_bikes_text_view)
        val emptySlotsTextView = findViewById<TextView>(R.id.empty_slots_text_view)
        val addressTextView = findViewById<TextView>(R.id.address_text_view)
        val navigateButton = findViewById<TextView>(R.id.navigate_button)
        val distanceTextView = findViewById<TextView>(R.id.distance_text_view)

        val freeBikes = " : ${station?.free_bikes}"
        val emptySlots = " : ${station?.empty_slots}"
        val stationAddress = "${station?.name?.replace("(PK)", "")?.toLowerCase()?.split(' ', '/')?.joinToString(" ") { it.capitalize() }}"
        val distance = " ${(lastLocation to (station?.getLatLng() ?: lastLocation)).getDistance()} m."

        freeBikesTextView.text = freeBikes
        emptySlotsTextView.text = emptySlots
        addressTextView.text = stationAddress
        distanceTextView.text = distance
        navigateButton.text = " Go Here"

        navigateButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${station?.latitude}, ${station?.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun requestStations(type: Int) {
        compositeDisposable.add(requestInterface.getStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { response ->
                    bikeStations = Calendar.getInstance().time.toTimestamp() to (response.network?.stations?.toList()
                            ?: listOf())
                    val bikesStationSecond = bikeStations!!.second
                    when (type) {
                        SHOW_WITH_BIKES -> showStationsWithBikes(bikesStationSecond)
                        SHOW_WITH_PARKING_SLOTS -> showStationsWithParking(bikesStationSecond)
                        else -> showAllStations(bikesStationSecond)
                    }
                }
                .subscribe())
    }

    private fun showStationsWithParking(second: List<Stations>) {
        map.clear()
        second.filter { it.empty_slots!!.toInt() > 0 }.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showStationsWithBikes(second: List<Stations>) {
        map.clear()
        second.filter { it.free_bikes!!.toInt() > 0 }.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun showAllStations(second: List<Stations>) {
        "count: ${second.size}".makeToast(this@MainActivity)
        second.forEach {
            map.addMarker(MarkerOptions().position(it.getLatLng()).title(it.name)
                    .icon(BitmapDescriptorFactory.fromResource(chooseIcon(it))))
        }
    }

    private fun getDifferenceInMinutes(): Int {
        return if (bikeStations?.first!!.isEmpty() || bikeStations?.first == null) {
            0
        } else {
            val timeNow = Calendar.getInstance().time
            val timestampOfLastCall = bikeStations!!.first.toDate()
            getDifferenceBetweenDates(timeNow, timestampOfLastCall) / 60000
        }
    }

    private fun getDifferenceBetweenDates(timeNow: Date, timestampOfLastCall: Date): Int = timeNow.time.toInt() - timestampOfLastCall.time.toInt()
}
