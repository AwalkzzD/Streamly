package com.example.streamly.api

import com.example.streamly.data.ResultData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DeezerApi {

    @Headers(
        "X-RapidAPI-Key: 8bc2aa87e6msh157790c5da2ab23p1c7f63jsnffc34ed23df4",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"
    )
    @GET("search")
    fun getData(@Query("q") query: String): Call<ResultData>
}