package com.example.admin.googlemapexample

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


import okhttp3.Cache
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class ApiFactory(private val context: Context) {

    private val retrofit = buildRetrofitInstance()
    private val TEN_MEGABYTES: Long = 10 * 1024 * 1024
    private val baseUrl = "http://api.citybik.es/"

    var apiService: BikeApi = retrofit.create(BikeApi::class.java)

    private fun buildRetrofitInstance() =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(buildClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()

    private fun buildClient() = OkHttpClient.Builder()
            .apply {
        connectTimeout(1, TimeUnit.MINUTES)
        addInterceptor(ChuckInterceptor(context))
        cache(Cache(context.cacheDir, TEN_MEGABYTES))
        readTimeout(1, TimeUnit.MINUTES)
        writeTimeout(1, TimeUnit.MINUTES)
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }
            .build()
}