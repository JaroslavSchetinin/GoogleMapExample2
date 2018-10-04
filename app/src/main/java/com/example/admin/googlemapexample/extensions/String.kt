package com.example.admin.googlemapexample.extensions

import android.content.Context
import android.widget.Toast
import com.example.admin.googlemapexample.MainActivity.Companion.timeStampFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.makeToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_LONG).show()

fun String.toDate(): Date = timeStampFormat.parse(this)
