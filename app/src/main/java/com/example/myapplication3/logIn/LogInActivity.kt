package com.example.myapplication3.logIn

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    private lateinit var et_email_login: TextView
    private lateinit var et_password_login: TextView
    private lateinit var btn_login: Button
    private lateinit var tv_sign_up: TextView

    lateinit var progressDialog: ProgressDialog
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        et_email_login = findViewById(R.id.et_email_login)
        et_password_login = findViewById(R.id.et_password_login)
        btn_login = findViewById(R.id.btn_login)
        tv_sign_up = findViewById(R.id.tv_sign_up)

        auth = Firebase.auth
        database = Firebase.database.reference

        btn_login.setOnClickListener {
            if (et_email_login.text.isEmpty() || et_password_login.text.isEmpty()) {
                Toast.makeText(this, "Registration is Incomplete", Toast.LENGTH_SHORT).show()
            } else {
                showDialog()
                logInAccount(et_email_login.text.toString(), et_password_login.text.toString())
            }
        }
        tv_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun logInAccount(email: String, passWord: String) {
        auth.signInWithEmailAndPassword(email, passWord).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                database.child("Lecturer").get().addOnSuccessListener { dataSnapshot ->
                    for (document in dataSnapshot.children) {
                        if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "Lecturer", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                database.child("Student").get().addOnSuccessListener { dataSnapshot ->
                    for (document in dataSnapshot.children) {
                        if (document.child("Email").value.toString() == auth.currentUser!!.email) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                hideDialog()
            } else {
                Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                hideDialog()
            }
        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Uploading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }
}