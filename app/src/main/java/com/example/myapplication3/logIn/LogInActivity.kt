package com.example.myapplication3.logIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    private lateinit var et_email_login: TextView
    private lateinit var et_password_login: TextView
    private lateinit var btn_login: Button
    private lateinit var tv_sign_up: TextView

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        et_email_login = findViewById(R.id.et_email_login)
        et_password_login = findViewById(R.id.et_password_login)
        btn_login = findViewById(R.id.btn_login)
        tv_sign_up = findViewById(R.id.tv_sign_up)

        auth = Firebase.auth
        db = Firebase.firestore

        btn_login.setOnClickListener {
            if (et_email_login.text.isEmpty() || et_password_login.text.isEmpty()) {
                Toast.makeText(this, "التسجيل غير كامل", Toast.LENGTH_SHORT).show()
            } else {
                LogInAccount(et_email_login.text.toString(), et_password_login.text.toString())
            }
        }

        tv_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun LogInAccount(email: String, passWord: String) {
        auth.signInWithEmailAndPassword(email, passWord).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
//                Toast.makeText(this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show()

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
            } else {
                Toast.makeText(this, "فشل في التسجيل", Toast.LENGTH_SHORT).show()
            }
        }
    }
}