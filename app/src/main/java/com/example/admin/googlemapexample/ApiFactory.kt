package com.example.admin.googlemapexample

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


import okhttp3.Cache
import java.util.concurrent.TimeUnit

class ApiFactory(private val context: Context) {

    private val retrofit = buildRetrofitInstance()

    var apiService: BikeApi = retrofit.create(BikeApi::class.java)

    private fun buildRetrofitInstance() =
            Retrofit.Builder()
                    .baseUrl("http://api.citybik.es/")
                    .client(buildClient())
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()

    private fun buildClient() = OkHttpClient.Builder().apply {
        connectTimeout(1, TimeUnit.MINUTES)
        addInterceptor(ChuckInterceptor(context))
        cache(Cache(context.cacheDir, 10 * 1024 * 1024)) // 10 mb
        readTimeout(1, TimeUnit.MINUTES)
        writeTimeout(1, TimeUnit.MINUTES)
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            //StethoInterceptorDebugOnly.configureInterceptor(this)
        }
    }.build()
}