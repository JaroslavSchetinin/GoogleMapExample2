package com.example.admin.googlemapexample.model

class Location {
    var longitude: String? = null

    var latitude: String? = null

    var country: String? = null

    var city: String? = null

    override fun toString(): String {
        return "ClassPojo [longitude = $longitude, latitude = $latitude, country = $country, city = $city]"
    }
}