package com.example.admin.googlemapexample.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
        var empty_slots: String? = null,
        @Ignore
        var extra: Extra? = null,
        var free_bikes: String? = null,
        @PrimaryKey
        var id: String = "",
        var latitude: String? = null,
        var longitude: String? = null,
        var name: String? = null,
        @Ignore
        var timestamp: String? = null,
        @Ignore
        var distanceToMe: Int = 0
) {
    override fun toString(): String {
        return "ClassPojo [timestamp = $timestamp, id = $id, free_bikes = $free_bikes, extra = $extra, name = $name, empty_slots = $empty_slots, longitude = $longitude, latitude = $latitude]"
    }

}
