package com.example.admin.googlemapexample.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "stations")
class Station {
    @Ignore
    var distanceToMe: Int = 0

    @Ignore
    var timestamp: String? = null

    @PrimaryKey
    var id: String = ""

    var free_bikes: String? = null

    @Ignore
    var extra: Extra? = null

    var name: String? = null

    var empty_slots: String? = null

    var longitude: String? = null

    var latitude: String? = null

    override fun toString(): String {
        return "ClassPojo [timestamp = $timestamp, id = $id, free_bikes = $free_bikes, extra = $extra, name = $name, empty_slots = $empty_slots, longitude = $longitude, latitude = $latitude]"
    }
}
