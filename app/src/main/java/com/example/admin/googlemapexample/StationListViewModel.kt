package com.example.admin.googlemapexample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.admin.googlemapexample.db.AppDatabase
import com.example.admin.googlemapexample.model.BikeResponse
import com.example.admin.googlemapexample.model.Station
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationListViewModel(application: Application) : AndroidViewModel(application) {

    var items: List<Station>? = mutableListOf()
    private var requestInterface = ApiFactory(application).apiService
    private var stationsDao = AppDatabase.getInstance(application)?.stationDao()


    fun getStationsFromWeb(): Maybe<BikeResponse> = requestInterface.getStations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { items = it.network.stations.toList() }

    fun getStationsFromDatabase(): Maybe<List<Station>>? =
            stationsDao?.getAllStations()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())

    fun stationsInsertion(bikeStations: List<Station>?) = stationsDao?.insertAllStations(bikeStations)

}