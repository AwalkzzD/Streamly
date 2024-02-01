package com.example.streamly.adapter

import android.app.Activity
import android.media.MediaPlayer
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


class ResultDataAdapter(val context: Activity, val dataList: List<Data>) :
    RecyclerView.Adapter<ResultDataAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.song_card_item, parent, false)
        return ResultViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentSong = dataList[position]
        var mediaPlayer = MediaPlayer.create(context, currentSong.preview.toUri())

        Picasso.get().load(currentSong.album.cover_big).transform(
            mutableListOf(
                BlurTransformation(context, 2, 1),
            )
        ).into(holder.songPoster)
        holder.songTitle.text = currentSong.title_short
        holder.songArtist.text = currentSong.artist.name

        holder.playPauseBtn.tag = R.drawable.play_icon

        holder.playPauseBtn.setOnClickListener {
            if (holder.playPauseBtn.tag == R.drawable.play_icon) {
                GlobalScope.launch {
                    mediaPlayer.start()
                }
                holder.playPauseBtn.tag = R.drawable.pause_icon
                holder.playPauseBtn.setImageResource(R.drawable.pause_icon)
            } else {
                GlobalScope.launch {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause();
                    }
                }
                holder.playPauseBtn.tag = R.drawable.play_icon
                holder.playPauseBtn.setImageResource(R.drawable.play_icon)
            }

        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songPoster: ImageView
        val songTitle: TextView
        val songArtist: TextView
        val playPauseBtn: ImageButton

        init {
            songPoster = itemView.findViewById(R.id.songPoster)
            songTitle = itemView.findViewById(R.id.songTitle)
            songArtist = itemView.findViewById(R.id.songArtist)
//            playBtn = itemView.findViewById(R.id.playBtn)
//            pauseBtn = itemView.findViewById(R.id.pauseBtn)
            playPauseBtn = itemView.findViewById(R.id.playPauseBtn)
        }

    }

}