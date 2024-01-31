package com.example.streamly.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.streamly.R
import com.example.streamly.data.Data
import com.squareup.picasso.Picasso

class ResultDataAdapter(val context: Activity, val dataList: List<Data>) :
    RecyclerView.Adapter<ResultDataAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.song_card_item, parent, false)
        return ResultViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentSong = dataList[position]

        Picasso.get().load(currentSong.album.cover_big).into(holder.songPoster)
        holder.songTitle.text = currentSong.title_short
        holder.songArtist.text = currentSong.artist.name

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songPoster: ImageView
        val songTitle: TextView
        val songArtist: TextView
        val playBtn: ImageButton
        val pauseBtn: ImageButton

        init {
            songPoster = itemView.findViewById(R.id.songPoster)
            songTitle = itemView.findViewById(R.id.songTitle)
            songArtist = itemView.findViewById(R.id.songArtist)
            playBtn = itemView.findViewById(R.id.playBtn)
            pauseBtn = itemView.findViewById(R.id.pauseBtn)
        }

    }

}