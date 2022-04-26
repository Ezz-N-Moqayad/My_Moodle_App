package com.example.myapplication3.navBottom.homeScreen.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddVideo : AppCompatActivity() {

    private lateinit var textViewVideo: TextView
    private lateinit var textView2Video: TextView
    private lateinit var textView3Video: TextView
    private lateinit var backPageCourseVideo: ImageView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        textViewVideo = findViewById(R.id.textViewVideo)
        textView2Video = findViewById(R.id.textView2Video)
        textView3Video = findViewById(R.id.textView3Video)
        backPageCourseVideo = findViewById(R.id.backPageCourseVideo)

        auth = Firebase.auth
        db = Firebase.firestore

        textView3Video.text = intent.getStringExtra("Name_Course").toString()
        textView2Video.text = intent.getStringExtra("Number_Course").toString()
        textViewVideo.text = intent.getStringExtra("Lecturer").toString()

        backPageCourseVideo.setOnClickListener {
            intent(Intent(this, CoursePage::class.java))
        }
    }

    fun intent(Intent_Page: Intent) {
        val i = Intent_Page
        i.putExtra("id_Course", intent.getStringExtra("id_Course").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(i)
    }
}