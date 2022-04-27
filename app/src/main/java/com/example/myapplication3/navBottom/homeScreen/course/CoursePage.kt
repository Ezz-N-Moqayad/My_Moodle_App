package com.example.myapplication3.navBottom.homeScreen.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CoursePage : AppCompatActivity() {

    private lateinit var textViewCou: TextView
    private lateinit var textView2Cou: TextView
    private lateinit var textView3Cou: TextView
    private lateinit var backHome: ImageView
    private lateinit var btnPopup: ImageView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_page)

        textViewCou = findViewById(R.id.textViewCou)
        textView2Cou = findViewById(R.id.textView2Cou)
        textView3Cou = findViewById(R.id.textView3Cou)
        backHome = findViewById(R.id.backHome)
        btnPopup = findViewById(R.id.btnPopup)

        auth = Firebase.auth
        db = Firebase.firestore

        textView3Cou.text = intent.getStringExtra("Name_Course").toString()
        textView2Cou.text = intent.getStringExtra("Number_Course").toString()
        textViewCou.text = intent.getStringExtra("Lecturer").toString()

        backHome.setOnClickListener {
            db!!.collection("Student").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            db!!.collection("Lecturer").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        btnPopup.setOnClickListener {
            db!!.collection("Lecturer").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            val popup = PopupMenu(this, btnPopup)
                            popup.menuInflater.inflate(R.menu.toolbar_coures, popup.menu)
                            popup.setOnMenuItemClickListener { x ->
                                when (x.itemId) {
                                    R.id.addsFile -> intent(Intent(this, AddFile::class.java))
                                    R.id.addsAssignment -> intent(
                                        Intent(
                                            this,
                                            AddAssignment::class.java
                                        )
                                    )
                                    R.id.addsVideo -> intent(Intent(this, AddVideo::class.java))
                                }
                                true
                            }
                            popup.show()
                        }
                    }
                }
            db!!.collection("Student").get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        if (document.get("Email") == auth.currentUser!!.email) {
                            Toast.makeText(
                                this, "It's not for you, only for Lecturer",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
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