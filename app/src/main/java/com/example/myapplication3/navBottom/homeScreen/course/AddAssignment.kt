package com.example.myapplication3.navBottom.homeScreen.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddAssignment : AppCompatActivity() {

    private lateinit var textViewAss: TextView
    private lateinit var textView2Ass: TextView
    private lateinit var textView3Ass: TextView
    private lateinit var backPageCourseAss: ImageView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_assignment)

        textViewAss = findViewById(R.id.textViewAss)
        textView2Ass = findViewById(R.id.textView2Ass)
        textView3Ass = findViewById(R.id.textView3Ass)
        backPageCourseAss = findViewById(R.id.backPageCourseAss)

        auth = Firebase.auth
        db = Firebase.firestore

        textView3Ass.text = intent.getStringExtra("Name_Course").toString()
        textView2Ass.text = intent.getStringExtra("Number_Course").toString()
        textViewAss.text = intent.getStringExtra("Lecturer").toString()

        backPageCourseAss.setOnClickListener {
            db!!.collection("Student").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            intent(Intent(this, CoursePage::class.java))
                        }
                    }
                }
            db!!.collection("Lecturer").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            intent(Intent(this, CoursePage::class.java))
                        }
                    }
                }
        }
    }
    fun intent(Intent_Page: Intent) {
        val i = Intent_Page
        i.putExtra("id", intent.getStringExtra("id").toString())
        i.putExtra("Name_Course", intent.getStringExtra("Name_Course").toString())
        i.putExtra("Number_Course", intent.getStringExtra("Number_Course").toString())
        i.putExtra("Lecturer", intent.getStringExtra("Lecturer").toString())
        startActivity(i)
    }
}