package com.example.admin.googlemapexample.model

data class Location(
        val city: String,
        val country: String,
        val latitude: Double,
        val longitude: Double
) {
    override fun toString(): String {
        return "ClassPojo [longitude = $longitude, latitude = $latitude, country = $country, city = $city]"
    }
}