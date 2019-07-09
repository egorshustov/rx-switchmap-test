package com.egorshustov.rxswitchmaptest.data.remote

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com"

    private val retrofitBuilder =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = retrofitBuilder.build()
    private val requestApi = retrofit.create(RequestApi::class.java)

    fun getRequestApi(): RequestApi {
        return requestApi
    }
}