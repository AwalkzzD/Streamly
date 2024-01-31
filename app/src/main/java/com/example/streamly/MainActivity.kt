package com.example.streamly

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.streamly.adapter.ResultDataAdapter
import com.example.streamly.api.DeezerApi
import com.example.streamly.data.ResultData
import com.google.android.material.carousel.CarouselLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: ResultDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songRecyclerView = findViewById(R.id.songRecyclerView)

        val retrofitBuilder =
            Retrofit.Builder().baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(DeezerApi::class.java)

        val retrofitData = retrofitBuilder.getData("arijit singh")

        retrofitData.enqueue(object : Callback<ResultData?> {
            override fun onResponse(call: Call<ResultData?>, response: Response<ResultData?>) {
//                successfull response

                val dataList = response.body()?.data!!

                songAdapter = ResultDataAdapter(this@MainActivity, dataList)
                songRecyclerView.adapter = songAdapter
                songRecyclerView.layoutManager = CarouselLayoutManager()

                Log.d("TAG: success", "onResponse: " + dataList.toString())
            }

            override fun onFailure(call: Call<ResultData?>, t: Throwable) {
//                failed/no response
                Log.d("TAG: failure", "onFailure: " + t.toString())
            }
        })
    }
}