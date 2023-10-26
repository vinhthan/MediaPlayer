package com.example.musicplayer

import android.content.ContentResolver
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: SongAdapter
    private lateinit var rv: RecyclerView
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showAllSong()
    }

    private fun showAllSong() {
        mAdapter = SongAdapter()
        getAllSong()
        //getAll()
        rv = findViewById(R.id.rv_song)
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
        mAdapter.setOnClickItemSong(object : SongAdapter.OnClickItemSong{
            override fun onClickSong(position: Int) {
                try {
                    //mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.file_example)

                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(mAdapter.songList[position].path)
                    //mediaPlayer.prepare()
                } catch (e: IOException){}
                //mediaPlayer.isLooping = true
                //mediaPlayer.seekTo(0)
                mediaPlayer.setVolume(0.5f, 0.5f)
                mediaPlayer.start()
                Log.d("123123", "click: ${mAdapter.songList[position].path}")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun getAllSong() {
        val contentResolver: ContentResolver = contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                val title = songCursor.getString(indexTitle)
                val artist = songCursor.getString(indexArtist)
                val path = songCursor.getString(indexPath)

                val song = SongEntity(title, artist, path)

                mAdapter.songList.add(song)
            } while (songCursor.moveToNext())

            Log.d("123123", "songss: ${mAdapter.songList[1].path}")
        }
    }

    private fun getAll() {
        // Need the READ_EXTERNAL_STORAGE permission if accessing Audio files that your
// app didn't create.

        // Container for information about each Audio.
        val AudioList = mutableListOf<SongEntity>()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
        )

// Show only Audios that are at least 5 minutes in duration.
        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
        )

// Display Audios in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                // Get values of columns for a given Audio.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val artist= cursor.getString(artistColumn)
                val path = cursor.getString(pathColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                val song = SongEntity(name, artist, path)
                mAdapter.songList.add(song)
            }
            Log.d("123123", "getAll: ${mAdapter.songList.size}")
        }
    }
}