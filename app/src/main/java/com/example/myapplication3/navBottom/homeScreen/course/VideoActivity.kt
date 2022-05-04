package com.example.myapplication3.navBottom.homeScreen.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class VideoActivity : AppCompatActivity(), Player.Listener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var nameVideo: TextView
    private lateinit var backPageCourseVideo: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        progressBar = findViewById(R.id.progressBar)
        nameVideo = findViewById(R.id.nameVideo)
        playerView = findViewById(R.id.video_view)
        backPageCourseVideo = findViewById(R.id.backPageCourseVideo)

        auth = Firebase.auth
        database = Firebase.database.reference

        nameVideo.text = intent.getStringExtra("Name_Video").toString()

        setupPlayer()
        setMP4File(intent.getStringExtra("Uri_Video").toString())

        if (savedInstanceState != null) {
            val seekTime = savedInstanceState.getLong("SeekTime")
            player.seekTo(seekTime)
            player.play()
        }

        backPageCourseVideo.setOnClickListener {
            database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        intent(Intent(this, CoursePage::class.java))
                        Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            database.child("Student").get().addOnSuccessListener { dataSnapshot ->
                for (document in dataSnapshot.children) {
                    if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                        intent(Intent(this, CoursePage::class.java))
                        Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setMP4File(Uri_Video: String) {
        val mediaItem = MediaItem.fromUri(Uri_Video)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        player.addListener(this)
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {}
            Player.STATE_IDLE -> {}
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime", player.currentPosition)
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    fun intent(Intent_Page: Intent) {
        Intent_Page.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        Intent_Page.putExtra("id_Lecturer", intent.getStringExtra("id_Lecturer").toString())
        Intent_Page.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        Intent_Page.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        Intent_Page.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(Intent_Page)
    }
}