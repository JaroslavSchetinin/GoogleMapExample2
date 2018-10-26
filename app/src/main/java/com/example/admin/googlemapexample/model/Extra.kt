package com.example.admin.googlemapexample.model

data class Extra(
        val NearbyStationList: List<Int>,
        val address: String,
        val districtCode: String,
        val status: String,
        val uid: Int,
        val zip: String
) {
    override fun toString(): String {
        return "ClassPojo [uid = $uid, zip = $zip, districtCode = $districtCode, status = $status, address = $address, NearbyStationList = $NearbyStationList]"
    }
}