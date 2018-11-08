package com.example.admin.googlemapexample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.support.v7.app.AppCompatActivity
import com.example.admin.googlemapexample.db.AppDatabase
import com.example.admin.googlemapexample.extensions.toTimestamp
import com.example.admin.googlemapexample.model.BikeResponse
import com.example.admin.googlemapexample.model.Station
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class StationListViewModel(application: Application) : AndroidViewModel(application) {

    var items = Pair("", listOf<Station>())
    private var requestInterface = ApiFactory(application).apiService
    private var stationsDao = AppDatabase.getInstance(application)?.stationDao()
    private var timestamp = { Calendar.getInstance().time.toTimestamp() }
    private val sharedPreferences = application.getSharedPreferences("BikeAppSP", AppCompatActivity.MODE_PRIVATE)
    var myLastLocation = ""

    fun getStationsFromWeb(): Maybe<BikeResponse> = requestInterface.getStations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { items = timestamp() to it.network.stations.toList() }

    fun getStationsFromDatabase(): Maybe<List<Station>>? =
            stationsDao?.getAllStations()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())

    fun stationsInsertion(bikeStations: List<Station>?) = stationsDao?.insertAllStations(bikeStations)
    fun setMyLastLocationInPrefs(text: String) = sharedPreferences?.edit()?.putString(MainActivity.MY_LAST_LOCATION, text)?.apply()
    fun getMyLastLocationFromPrefs() = sharedPreferences?.getString(MainActivity.MY_LAST_LOCATION, "")

}