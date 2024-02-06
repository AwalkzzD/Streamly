package com.example.streamly.adapter

import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.streamly.R
import com.example.streamly.data.Data
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ResultDataAdapter(private val context: Activity, private val dataList: List<Data>) :
    RecyclerView.Adapter<ResultDataAdapter.ResultViewHolder>() {

    private val mediaPlayer = MediaPlayer()
    private val TAG = "ResultDataAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.song_card_item, parent, false)
        return ResultViewHolder((itemView))
    }

    override fun onViewDetachedFromWindow(holder: ResultViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("TAG", "onViewDetachedFromWindow: Page Changed")
        holder.playPauseBtn.setImageResource(R.drawable.play_icon)
    }


    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: Data List Size = " + dataList.size)

//        if (mediaPlayer != null) {
        mediaPlayer.stop()
        mediaPlayer.reset()
//        }

        with(holder) {
            with(dataList[position]) {
                Picasso.get().load(this.album.cover_big).transform(
                    mutableListOf(
                        BlurTransformation(context, 2, 1),
                    )
                ).into(songPoster)

                songTitle.text = this.title_short
                /*                mediaPlayer.setDataSource(
                                    context, dataList[getItemViewType(position)].preview.toUri()
                                )*/

                Log.d("TAG", "onBindViewHolder: " + this.title_short)

                songArtist.text = this.artist.name
                playPauseBtn.tag = R.drawable.play_icon

                playPauseBtn.setOnClickListener {
                    /*Log.e("clcik====>",position.toString())
                    Log.e("Song URL====>",this.preview.toUri().toString())*/
                    mediaPlayer.setDataSource(context, this.preview.toUri())
                    if (playPauseBtn.tag == R.drawable.play_icon) {
                        GlobalScope.launch {
                            mediaPlayer.prepare()
                            mediaPlayer.setOnPreparedListener {
                                mediaPlayer.start()
                            }
                        }
                        playPauseBtn.tag = R.drawable.pause_icon
                        playPauseBtn.setImageResource(R.drawable.pause_icon)
                    } else {
                        GlobalScope.launch {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                            }
                        }
                        playPauseBtn.tag = R.drawable.play_icon
                        playPauseBtn.setImageResource(R.drawable.play_icon)
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songPoster: ImageView
        val songTitle: TextView
        val songArtist: TextView
        val playPauseBtn: ImageButton

        init {
            songPoster = itemView.findViewById(R.id.songPoster)
            songTitle = itemView.findViewById(R.id.songTitle)
            songArtist = itemView.findViewById(R.id.songArtist)
            playPauseBtn = itemView.findViewById(R.id.playPauseBtn)
        }
    }
}