package com.example.myapplication3.navBottom.homeScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.example.myapplication3.logIn.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity3 : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var backHome: Button

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        backHome = findViewById(R.id.backHome)

        auth = Firebase.auth
        db = Firebase.firestore

        textView3.text = intent.getStringExtra("Name_Course").toString()
        textView2.text = intent.getStringExtra("Number_Course").toString()
        textView.text = intent.getStringExtra("Lecturer").toString()


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

    }
}