package com.example.streamly

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.streamly.adapter.ResultDataAdapter
import com.example.streamly.api.DeezerApi
import com.example.streamly.data.ResultData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: ResultDataAdapter
    private lateinit var bottomDialog: BottomSheetDialog
    private lateinit var searchBtn: FloatingActionButton
    private lateinit var retrofitBuilder: DeezerApi
    private var SEARCH_QUERY = "arijit singh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomDialog = BottomSheetDialog(this@MainActivity)
        val bottomView = layoutInflater.inflate(R.layout.bottom_dialog, null)
        bottomDialog.setCancelable(false)
        bottomDialog.setContentView(bottomView)

        searchBtn = findViewById(R.id.searchAction)
        searchBtn.setOnClickListener {
            bottomDialog.show()
            Log.d("TAG: Bottom Sheet", "onCreate: Bottom Sheet")
            val searhQuery = bottomView.findViewById<EditText>(R.id.searchQry)
            val searchBtn = bottomView.findViewById<ImageButton>(R.id.searchBtn)

            searchBtn.setOnClickListener {
                if (searhQuery.text.toString().contains("[A-Za-z0-9]+".toRegex())) {
                    SEARCH_QUERY = searhQuery.text.toString()
                    Log.d("TAG: SEARCH QUERY", "search query: " + SEARCH_QUERY)
                    bottomDialog.hide()
                    fetchSongs(SEARCH_QUERY)
                } else {
                    Toast.makeText(this, "No Input", Toast.LENGTH_SHORT).show()
                    bottomDialog.hide()
                }
            }
        }

        songRecyclerView = findViewById(R.id.songRecyclerView)

        songRecyclerView.layoutManager =
            CarouselLayoutManager(FullScreenCarouselStrategy(), RecyclerView.VERTICAL)
        CarouselSnapHelper().attachToRecyclerView(songRecyclerView)

        fetchSongs(SEARCH_QUERY)

        /*retrofitBuilder =
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
                songRecyclerView.layoutManager =
                    CarouselLayoutManager(FullScreenCarouselStrategy(), RecyclerView.VERTICAL)

                Log.d("TAG: success", "onResponse: " + dataList.toString())
            }

            override fun onFailure(call: Call<ResultData?>, t: Throwable) {
//                failed/no response
                Log.d("TAG: failure", "onFailure: " + t.toString())
            }
        })*/
    }

    private fun fetchSongs(searchQry: String) {

        lifecycleScope.launch {
            retrofitBuilder =
                Retrofit.Builder().baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(DeezerApi::class.java)

            val retrofitData = retrofitBuilder.getData(searchQry)
            val mProgressDialog = ProgressDialog(this@MainActivity)
            mProgressDialog.isIndeterminate = true
            mProgressDialog.setMessage("Loading...")
            mProgressDialog.show()

            retrofitData.enqueue(object : Callback<ResultData?> {
                override fun onResponse(call: Call<ResultData?>, response: Response<ResultData?>) {
//                successfull response

                    if (mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }

                    val dataList = response.body()?.data!!

                    songAdapter = ResultDataAdapter(this@MainActivity, dataList)
                    songRecyclerView.adapter = songAdapter
                    songAdapter.notifyDataSetChanged()

                    Log.d("TAG: success", "onResponse: " + dataList.toString())
                }

                override fun onFailure(call: Call<ResultData?>, t: Throwable) {
//                failed/no response
                    if (mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }
                    Log.d("TAG: failure", "onFailure: " + t.toString())
                }
            })
        }
    }
}