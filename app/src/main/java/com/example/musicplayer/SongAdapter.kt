package com.example.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter: RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    var songList = mutableListOf<SongEntity>()

    interface OnClickItemSong {
        fun onClickSong(position: Int)
    }

    private var onClickItemSong: OnClickItemSong? = null

    fun setOnClickItemSong(onClickSong: OnClickItemSong) {
        onClickItemSong = onClickSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songList[position])

        holder.itemView.setOnClickListener {
            onClickItemSong?.onClickSong(position)
        }
    }

    class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        val tvArtist = itemView.findViewById<TextView>(R.id.tv_artist)
        fun bind(songEntity: SongEntity) {
            tvTitle.text = songEntity.title
            tvArtist.text = songEntity.artist
        }
    }
}