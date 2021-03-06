package com.example.admin.googlemapexample.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.admin.googlemapexample.model.Station
import io.reactivex.Maybe

@Dao
interface StationsDao {

    @Insert
    fun insertStation(station: Station)

    @Insert
    fun insertAllStations(stations: List<Station>?)

    @Delete
    fun deleteStation(station: Station)

    @Query("DELETE FROM stations WHERE id = :id")
    fun deleteStationById(id: String)

    @Query("SELECT * FROM stations")
    fun getAllStations(): Maybe<List<Station>>
}