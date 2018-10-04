package com.example.admin.googlemapexample.extensions

import com.example.admin.googlemapexample.MainActivity.Companion.timeStampFormat
import java.util.*

fun Date.toTimestamp(): String = timeStampFormat.format(this)