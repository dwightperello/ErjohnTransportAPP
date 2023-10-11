package com.example.erjohnandroid.domain.network

import com.example.erjohnandroid.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkBuilder {
    private const val NETWORK_CALL_TIMEOUT = 60L

    fun create(baseURl: String, cacheDir: File, cacheSize: Long): NetworkService {
        return Retrofit.Builder()
            .baseUrl(baseURl)
            .client(okHttpClint(cacheDir, cacheSize))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }

    private fun okHttpClint(cacheDir: File, cacheSize: Long): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                }
            )
            .readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
}