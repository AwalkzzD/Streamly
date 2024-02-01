package com.example.streamly

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: ResultDataAdapter
    private lateinit var bottomDialog: BottomSheetDialog
    private lateinit var searchBtn: ImageButton
    private lateinit var retrofitBuilder: DeezerApi
    private var SEARCH_QUERY = "arijit singh"
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private lateinit var searchQuery: EditText

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val results = result.data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                ) as ArrayList<String>
                searchQuery.setText(results[0])
            }
        }

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
            searchQuery = bottomView.findViewById<EditText>(R.id.searchQry)
            val searchBtn = bottomView.findViewById<Button>(R.id.searchBtn)
            val voiceTypingBtn = bottomView.findViewById<ImageButton>(R.id.voiceTyping)

            voiceTypingBtn.setOnClickListener {
                showVoiceTypingDialog()
            }

            searchBtn.setOnClickListener {
                if (searchQuery.text.toString().contains("[A-Za-z0-9]+".toRegex())) {
                    SEARCH_QUERY = searchQuery.text.toString()
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
    }

    private fun showVoiceTypingDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()
        )

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking...")

        try {
            result.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, " " + e.message, Toast.LENGTH_LONG).show()
            Log.d("TAG", "showVoiceTypingDialog: " + e.message)
        }
    }

    private fun fetchSongs(searchQry: String) {

        lifecycleScope.launch {
            retrofitBuilder =
                Retrofit.Builder().baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(DeezerApi::class.java)

            val retrofitData = retrofitBuilder.getData(searchQry)
            val mProgressDialog = ProgressDialog(this@MainActivity)
            mProgressDialog.setMessage("Loading...")
            mProgressDialog.show()

            retrofitData.enqueue(object : Callback<ResultData?> {
                override fun onResponse(call: Call<ResultData?>, response: Response<ResultData?>) {
                    if (mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }

                    songAdapter = ResultDataAdapter(this@MainActivity, response.body()?.data!!)
                    songRecyclerView.adapter = songAdapter
                    songAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<ResultData?>, t: Throwable) {
                    if (mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }
                }
            })
        }
    }
}